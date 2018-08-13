package cn.itcast.erp.action;

import javax.annotation.Resource;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.erp.biz.IReturnorderdetailBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Returnorderdetail;
import cn.itcast.erp.exception.ErpException;
import cn.itcast.erp.util.WebUtil;

@Controller("returnorderdetailAction")
@Scope("prototype")
@ParentPackage("struts-default")
@Namespace("/")
@Action("returnorderdetail")
public class ReturnorderdetailAction extends BaseAction<Returnorderdetail> {

    private IReturnorderdetailBiz returnorderdetailBiz;
    private Long storeuuid;
    @Resource(name="returnorderdetailBiz")
    public void setReturnorderdetailBiz(IReturnorderdetailBiz returnorderdetailBiz) {
        this.returnorderdetailBiz = returnorderdetailBiz;
        super.setBaseBiz(this.returnorderdetailBiz);
    }
    
    public Long getStoreuuid() {
		return storeuuid;
	}

	public void setStoreuuid(Long storeuuid) {
		this.storeuuid = storeuuid;
	}

	/**
     * 退货订单出库
     * @author user4_3 闻彬 benkey
     */
    public void doOutStore() {
    	Emp loginUser = WebUtil.getLoginUser();
    	if(null == loginUser) {
    		WebUtil.ajaxReturn(false, "请登录...");
    		return;	
    	}
    	try {
    		returnorderdetailBiz.doOutStore(getId(), storeuuid, loginUser.getUuid());
    		WebUtil.ajaxReturn(true, "退单出库成功!");
    	} catch (ErpException e1) {
			e1.printStackTrace();
			WebUtil.ajaxReturn(false, e1.getMessage());
		} catch (UnauthorizedException e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "对不起,权限不足");
		} catch (Exception e2) {
			e2.printStackTrace();
			WebUtil.ajaxReturn(false, "退单出库失败...");
		}
    }
    
    /**
     * 入库
     */
    public void doInStore() {
        // 判断用户是否登陆
        Emp loginUser = WebUtil.getLoginUser();
        if(null == loginUser) {
            WebUtil.ajaxReturn(false, "你还没有登陆");
            return;
        }
        try {
            returnorderdetailBiz.doInStore(getId(), storeuuid, loginUser.getUuid());
            WebUtil.ajaxReturn(true, "入库成功");
        } catch (ErpException e) {
            e.printStackTrace();
            WebUtil.ajaxReturn(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            WebUtil.ajaxReturn(false, "入库失败");
        }
    }

}
