package cn.itcast.erp.biz.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.dao.IDepDao;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IRoleDao;
import cn.itcast.erp.entity.Dep;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;
import cn.itcast.erp.entity.Role;
import cn.itcast.erp.entity.Tree;
import cn.itcast.erp.exception.ErpException;
import net.sf.jxls.transformer.XLSTransformer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 员工业务逻辑类
 *
 */
@Service("empBiz")
public class EmpBiz extends BaseBiz<Emp> implements IEmpBiz {

	private IEmpDao empDao;
	/** 散列次数 3 */
	private int hashIteration = 3;
	@Autowired
	private IRoleDao roleDao;

	@Autowired
	private IDepDao depDao;

	@Autowired
	private JedisPool jedisPool;

	@Resource(name = "empDao")
	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
		super.setBaseDao(this.empDao);
	}

	@Override
	public Emp findByUsernameAndPwd(String username, String pwd) {
		// source: 要加密的内容
		// salt: 盐, 扰乱码
		// hashIteration： 散列次数 3
		Md5Hash md5 = new Md5Hash(pwd, username, 3);
		pwd = md5.toString();// 加密后的密码
		System.out.println("pwd:" + pwd);
		return empDao.findByUsernameAndPwd(username, pwd);
	}

	@Override
	@Transactional
	public void add(Emp t) {
		// 把密码加密
		String newPwd = encrypt(t.getUsername(), t.getUsername());
		// 设置成加密后的密码
		t.setPwd(newPwd);
		super.add(t);
	}

	/**
	 * 加密
	 * 
	 * @param src
	 *            要加密的密码
	 * @param salt
	 *            username做为盐
	 * @return
	 */
	private String encrypt(String src, String salt) {
		Md5Hash md5 = new Md5Hash(src, salt, hashIteration);
		return md5.toString();// 加密后的密码
	}

	@Override
	@Transactional
	public void updatePwd(String oldPwd, String newPwd, Long uuid) {
		Emp emp = empDao.get(uuid);
		// 原密码进行加密
		oldPwd = encrypt(oldPwd, emp.getUsername());
		if (!oldPwd.equals(emp.getPwd())) {
			throw new ErpException("原密码不正确");
		}
		// 加密新密码
		newPwd = encrypt(newPwd, emp.getUsername());
		// 更新密码
		empDao.updatePwd(newPwd, uuid);
	}

	@Override
	@Transactional
	public void updatePwd_reset(String newPwd, Long uuid) {
		Emp emp = empDao.get(uuid);
		newPwd = encrypt(newPwd, emp.getUsername());
		// 更新密码
		empDao.updatePwd(newPwd, uuid);
	}

	@Override
	public List<Tree> readEmpRoles(Long uuid) {
		List<Tree> result = new ArrayList<Tree>();
		// 获取用户信息，进入持久态
		Emp emp = empDao.get(uuid);
		// 得到 用户所拥有的角色, 进入持久态
		List<Role> empRoles = emp.getRoles();
		// 角色列表
		List<Role> list = roleDao.getList(null, null, null);
		// 把角色转成树的节点
		for (Role role : list) {
			Tree tree = new Tree();
			tree.setId(role.getUuid() + "");
			tree.setText(role.getName());
			// 判断用户是否拥有这个角色
			if (empRoles.contains(role)) {
				// 让它选中
				tree.setChecked(true);
			}
			result.add(tree);
		}
		return result;
	}

	@Override
	@Transactional
	public void updateEmpRoles(Long uuid, String ids) {
		// 获取用户，进入持久化
		Emp emp = empDao.get(uuid);
		// 清空用户下的所有角色，delete from emp_role where empuuid=?
		emp.setRoles(new ArrayList<Role>());

		// 建立新的关系
		String[] roleIds = ids.split(",");
		for (String roleId : roleIds) {
			Role role = roleDao.get(Long.valueOf(roleId));
			emp.getRoles().add(role);
		}

		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			// 清空用户的权限缓存
			jedis.del(Menu.MENUS_KEY + uuid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != jedis) {
				jedis.close();
			}
			jedis = null;
		}
	}

	@Override
	@Transactional
	public void export(ServletOutputStream os, Emp t1) throws IOException {
		// 得到符合条件的所有数据
		List<Emp> list = empDao.getList(t1, null, null);
		// 创建一个工作簿 HSSF xls
		Workbook wk = new HSSFWorkbook();
		try {
			String shtName = "员工";

			// 创建工作表
			Sheet sht = wk.createSheet(shtName);
			// 创建行 表头
			Row row = sht.createRow(0);// 行的下标从0开始
			// 表头内容
			String[] headers = { "登陆名", "真实姓名", "性别", "邮件地址", "联系电话", "联系地址", "出生年月日", "部门" };
			// 宽度
			int[] widths = { 4000, 4000, 2000, 8000, 8000, 8000, 4000, 4000 };
			int i = 0;
			for (; i < headers.length; i++) {
				// 表头
				row.createCell(i).setCellValue(headers[i]);
				sht.setColumnWidth(i, widths[i]);
			}
			// 内容
			i = 1;
			for (Emp emp : list) {
				if (null == emp) {
					continue;
				}
				row = sht.createRow(i);
				row.createCell(0).setCellValue(emp.getUsername());
				row.createCell(1).setCellValue(emp.getName());
				String ge = "女";
				if (1 == emp.getGender()) {
					ge = "男";
				}
				row.createCell(2).setCellValue(ge);
				row.createCell(3).setCellValue(emp.getEmail());
				row.createCell(4).setCellValue(emp.getTele());
				row.createCell(5).setCellValue(emp.getAddress());
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				String bir = format.format(emp.getBirthday());
				row.createCell(6).setCellValue(bir);
				row.createCell(7).setCellValue(emp.getDep().getName());
				i++;
			}
			// 保存到输出流
			wk.write(os);

		} finally {
			try {
				wk.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	@Transactional
	public void doImport(FileInputStream is) throws ParseException, IOException {
		// 读取工作簿
		Workbook wk = new HSSFWorkbook(is);
		try {
			// 读取第一个工作表
			Sheet sht = wk.getSheetAt(0);
			// 最后一行的下标
			Row row = null;
			String name = null;
			Emp emp = null;
			List<Emp> list = null;
			// i=1是要忽略表头行
			for (int i = 1; i <= sht.getLastRowNum(); i++) {
				row = sht.getRow(i);
				// 供应商名称
				name = row.getCell(0).getStringCellValue();
				emp = new Emp();
				emp.setUsername(name);
				// 判断是否存在
				list = empDao.getList(null, emp, null);
				if (list.size() > 0) {
					// 存在
					emp = list.get(0); // 进入持久化状
				}

				emp.setName(row.getCell(1).getStringCellValue());
				// row.getCell(2).getCellType 判断单元格的数据类型
				String gender = row.getCell(2).getStringCellValue();
				Long gender2 = null;
				if ("男".equals(gender)) {
					gender2 = 1l;
				} else if ("女".equals(gender)) {
					gender2 = 0l;
				} else {
					throw new ErpException("性别数据不符合要求");
				}

				// "登陆名","真实姓名","性别","邮件地址","联系电话","联系地址","出生年月日","部门"
				emp.setGender(gender2);
				emp.setEmail(row.getCell(3).getStringCellValue());
				isString(row.getCell(4));
				emp.setTele(row.getCell(4).getStringCellValue());
				emp.setAddress(row.getCell(5).getStringCellValue());
				String date = row.getCell(6).getStringCellValue();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				emp.setBirthday(format.parse(date));
				Dep dep = new Dep();
				dep.setName(row.getCell(7).getStringCellValue());
				List<Dep> list2 = depDao.getList(dep, null, null);
				if (list2.size() > 0) {
					dep = list2.get(0);

				}
				emp.setDep(dep);
				if (list.size() == 0) {
					empDao.add(emp);
				}

			}
		} finally {
			try {
				wk.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void export2(ServletOutputStream os, Emp t1) {
		List<Emp> list = empDao.getList(t1, null, null);
		HSSFWorkbook ws = null;
		try {
			ws = new HSSFWorkbook(new ClassPathResource("template/export_emp.xls").getInputStream());
			HashMap<String, Object> dataMode = new HashMap<String, Object>();
			for (Emp emp : list) {
				emp.setGenderName(genderChange(emp.getGender()));
				emp.setFormBirthday(formBirthday(emp.getBirthday()));
				emp.setDepName(emp.getDep().getName());
			}
			dataMode.put("emps", list);
			XLSTransformer transformer = new XLSTransformer();
			transformer.transformWorkbook(ws, dataMode);
			ws.write(os);
		} catch (IOException e) {
			e.printStackTrace();
			
		}finally {
			if(null!=ws) {
				try {
					ws.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 格式化性别
	 * 
	 * @param gender
	 * @return
	 */
	private String genderChange(Long gender) {
		String genderName = "女";
		if (Emp.GENDER_MALE.equals(gender)) {
			genderName = "男";
		}
		return genderName;

	}

	private String formBirthday(Date dirthday) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dirthdayName = format.format(dirthday);
		return dirthdayName;
	}

}
