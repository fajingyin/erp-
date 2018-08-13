package cn.itcast.erp.action;

import javax.annotation.Resource;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.erp.biz.IInventoryBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Inventory;
import cn.itcast.erp.util.WebUtil;
import cn.itcast.erp.exception.ErpException;

@Controller("inventoryAction")
@Scope("prototype")
@ParentPackage("struts-default")
@Namespace("/")
@Action("inventory")
public class InventoryAction extends BaseAction<Inventory> {
	
	private IInventoryBiz inventoryBiz;
    @Resource(name="inventoryBiz")
    public void setInventoryBiz(IInventoryBiz inventoryBiz) {
        this.inventoryBiz = inventoryBiz;
        super.setBaseBiz(this.inventoryBiz);
    }
    /**
     * 添加盘盈盘亏
     */ 
    public void addInventory() {
		//获取登录用户
		Emp loginUser = WebUtil.getLoginUser();
		//判断用户
		if (loginUser == null) {
			WebUtil. ajaxReturn(false, "你还没有登录,请登录");
			return;
		}
		try {
			//获取前端提交的方法,得到仓库和商品的编号
			Inventory inventory = getT(); 	
			inventoryBiz.add(inventory,loginUser.getUuid());
			WebUtil.ajaxReturn(true, "添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "添加失败");
		}
	}
    
    /**
     * 更新盘盈盘亏记录
     */
    public void updateInventory() {
        Emp loginUser = WebUtil.getLoginUser();
        if(null == loginUser) {
            WebUtil.ajaxReturn(false, "你还没有登陆");
            return;
        }
        try {
            inventoryBiz.update(getT());
            WebUtil.ajaxReturn(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            WebUtil.ajaxReturn(false, "修改失败");
        }
    }
    
    /*
     * 审核
     */
    public void doCheck(){
    	Emp loginUser = WebUtil.getLoginUser();
    	if(loginUser==null){
    		 WebUtil.ajaxReturn(false, "你还没有登陆");
             return;
    	}
    	try {
    		inventoryBiz.doCheck(loginUser.getUuid(), getId());
    		WebUtil.ajaxReturn(true, "审核成功");
		} catch(ErpException e){
			WebUtil.ajaxReturn(false, e.getMessage());
		} catch (Exception e) {
			WebUtil.ajaxReturn(false, "审核失败");
		}	
    }
    
}
