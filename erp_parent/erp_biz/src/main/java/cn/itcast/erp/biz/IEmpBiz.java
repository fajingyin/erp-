package cn.itcast.erp.biz;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.ServletOutputStream;

import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Tree;
/**
 * 员工业务逻辑层接口
 *
 */
public interface IEmpBiz extends IBaseBiz<Emp>{

    Emp findByUsernameAndPwd(String username, String pwd);

    /**
     * 修改密码
     * @param oldPwd
     * @param newPwd
     * @param uuid
     */
    void updatePwd(String oldPwd, String newPwd, Long uuid);

    /**
     * 管理员重置密码
     * @param newPwd
     * @param uuid
     */
    void updatePwd_reset(String newPwd, Long uuid);

    /**
     * 获取用户角色信息
     * @param uuid
     * @return
     */
    List<Tree> readEmpRoles(Long uuid);

    /**
     * 设置用户角色
     * @param uuid  用户编号
     * @param ids 角色编号 多个以逗号分割
     */
    void updateEmpRoles(Long uuid, String ids);

	void export(ServletOutputStream outputStream, Emp t1) throws IOException;

	void doImport(FileInputStream fileInputStream) throws ParseException, IOException;

	void export2(ServletOutputStream outputStream, Emp t1);
}

