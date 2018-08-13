package cn.itcast.erp.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Supplier;
import cn.itcast.erp.entity.Tree;
import cn.itcast.erp.exception.ErpException;
import cn.itcast.erp.util.WebUtil;

@Controller("empAction")
@Scope("prototype")
@ParentPackage("struts-default")
@Namespace("/")
@Action("emp")
public class EmpAction extends BaseAction<Emp> {

    private IEmpBiz empBiz;
    private String oldPwd;// 原密码
    private String newPwd;// 新密码
    private String ids;// 角色编号，多个以逗号分割

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }

    @Resource(name="empBiz")
    public void setEmpBiz(IEmpBiz empBiz) {
        this.empBiz = empBiz;
        super.setBaseBiz(this.empBiz);
    }

    /**
     * 修改密码
     */
    public void updatePwd() {
        Emp loginUser = WebUtil.getLoginUser();
        if(null == loginUser) {
            WebUtil.ajaxReturn(false, "你还没有登陆");
            return;
        }
        try {
            empBiz.updatePwd(oldPwd, newPwd, loginUser.getUuid());
            WebUtil.ajaxReturn(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            WebUtil.ajaxReturn(false, "修改失败");
        }
    }

    /**
     * 重置密码
     */
    public void updatePwd_reset() {
        try {
            empBiz.updatePwd_reset(newPwd, getId());
            WebUtil.ajaxReturn(true, "重置密码成功");
        } catch (Exception e) {
            e.printStackTrace();
            WebUtil.ajaxReturn(false, "重置密码失败");
        }
    }

    /**
     * 获取用户角色设置信息
     */
    public void readEmpRoles() {
        List<Tree> list = empBiz.readEmpRoles(getId());
        WebUtil.write(list);
    }

    /**
     * 设置用户角色
     */
    public void updateEmpRoles() {
        try {
            empBiz.updateEmpRoles(getId(), ids);
            WebUtil.ajaxReturn(true, "更新成功");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            WebUtil.ajaxReturn(false, "更新失败");
        }
    }
    
    
    /**
     * 导出数据
     */
    public void export() {
        HttpServletResponse res = ServletActionContext.getResponse();
        String filename = "员工表.xls";
        
        try {
            // 文件名含有中文，转成原始的字节流，再以iso-8859-1编码形式组装字符串? 因为浏览读取响应头时用的iso-8859-1.
            filename = new String(filename.getBytes(),"iso-8859-1").toString();
            // 告诉浏览器，下载文件
            res.setHeader("Content-Disposition", "attachment;filename=" + filename);
            empBiz.export(res.getOutputStream(), getT1());
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    /**
     * 员工导出
     */
    public void export2() {
        HttpServletResponse res = ServletActionContext.getResponse();
        String filename = "员工表.xls";
        
        try {
            // 文件名含有中文，转成原始的字节流，再以iso-8859-1编码形式组装字符串? 因为浏览读取响应头时用的iso-8859-1.
            filename = new String(filename.getBytes(),"iso-8859-1").toString();
            // 告诉浏览器，下载文件
            res.setHeader("Content-Disposition", "attachment;filename=" + filename);
            empBiz.export2(res.getOutputStream(), getT1());
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导入数据
     */
    public void doImport() {
        if(!"application/vnd.ms-excel".equals(getFileFileName())) {
            if(!getFileFileName().endsWith(".xls")) {
                WebUtil.ajaxReturn(false, "文件格式不正确!");
                return;
            }
        }
        try {
        	empBiz.doImport(new FileInputStream(getFile()));
            WebUtil.ajaxReturn(true, "导入成功");
        } catch (ErpException e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            WebUtil.ajaxReturn(false, "导入失败");
        } catch (Exception e) {
            e.printStackTrace();
            WebUtil.ajaxReturn(false, "导入失败，发生未知错误，请联系管理员");
        }
    }



    public void setIds(String ids) {
        this.ids = ids;
    }
}
