package cn.itcast.erp.action;

import java.util.List;

import javax.annotation.Resource;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.biz.IReturnordersBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Returnorderdetail;
import cn.itcast.erp.entity.Returnorders;
import cn.itcast.erp.exception.ErpException;
import cn.itcast.erp.util.WebUtil;

@Controller("returnordersAction")
@Scope("prototype")
@ParentPackage("struts-default")
@Namespace("/")
@Action("returnorders")
public class ReturnordersAction extends BaseAction<Returnorders> {

    private IReturnordersBiz returnordersBiz;
    
    private String json;
    

    public void setJson(String json) {
		this.json = json;
	}

	@Resource(name="returnordersBiz")
    public void setReturnordersBiz(IReturnordersBiz returnordersBiz) {
        this.returnordersBiz = returnordersBiz;
        super.setBaseBiz(this.returnordersBiz);
    }
	 /**
     *添加采购退货订单	
     *by  尹飞月
     */
    public void add_return() {
      	 try {
      		 Emp loginUser = WebUtil.getLoginUser();
               // 获取前端提交的订单,主要是得到供应商的编号
              Returnorders returnorders = getT();
               // 把明细的json字符串转成订单明细列表
               List<Returnorderdetail> returnOrderDetails = JSON.parseArray(json, Returnorderdetail.class);
               // 设置订单下的明细
               returnorders.setReturnorderdetails(returnOrderDetails);
               // 设置下单人
               returnorders.setCreater(loginUser.getUuid());
               // 设置订单类型为采购退货登记
               returnorders.setType(Returnorders.RETURN_TYPE_OUT);
               // 订单的状态为未出库
               returnorders.setState(Returnorders.RETURN_STATE_CREATE);
               returnordersBiz.addReturn(returnorders);
               WebUtil.ajaxReturn(true, "添加退货单成功");
           } catch (ErpException e) {
               e.printStackTrace();
               WebUtil.ajaxReturn(false, e.getMessage());
           } catch (Exception e) {
               e.printStackTrace();
               WebUtil.ajaxReturn(false, "添加退货单失败");
           }
      }

   	public String getJson() {
   		return json;
   	}	
    /**
     * 刘正伟
     * 增加销售退货订单的方法
     */
    public  void addReturnOut(){
    	 Emp loginUser = WebUtil.getLoginUser();   		
         Returnorders returnorders = getT ();
         try {
	         //将json转换成数组
	         List <Returnorderdetail> returnorderdetail = JSON.parseArray (json, Returnorderdetail.class);
	         returnorders.setReturnorderdetails (returnorderdetail);
	         //设置下单人员
	         returnorders.setCreater (loginUser.getUuid ());
	         //设置销售退货订单状态为未审核 0 
	         returnorders.setState (Returnorders.RETURNORDERS_STATE_UNCHECK);
	         //设置type为销售  1:采购 2 销售 
	         returnorders.setType (Returnorders.RETURN_TYPE_IN);
	         returnordersBiz.add (returnorders);
	         WebUtil.ajaxReturn(true, "销售退货登记成功");
         } catch (ErpException e) {
        	 WebUtil.ajaxReturn(false, e.getMessage());
             e.printStackTrace ();
         }catch (Exception e) {
        	 WebUtil.ajaxReturn(false, "销售退货登记失败");
             e.printStackTrace ();
         }
    }
    
    
    /**
     * 查询登录用户添加的销售退货登记列表
     */
    public void myListByPage(){
    	Emp loginUser = WebUtil.getLoginUser();
    	 if(loginUser!=null){
             if (getT1 ()==null){
                 setT1 (new Returnorders());
             }
             getT1 ().setCreater (loginUser.getUuid ());
             super.listByPage ();
         }
    }
    
    /**
     * 销售订单退货审核
     */
    public void doCheck(){
        Emp loginUser = WebUtil.getLoginUser();
        if(null == loginUser) {
            WebUtil.ajaxReturn(false, "你还没有登陆");
            return;
        }
        try {
            returnordersBiz.doCheck(getId(),loginUser.getUuid());
            WebUtil.ajaxReturn(true,"审核成功");
        } catch (Exception e) {
            e.printStackTrace();
            WebUtil.ajaxReturn(false,"审核失败");
        }
    }
    
    /**
     * 采购订单退货审核
     */
    public void doOutCheck() {
        // 判断用户是否登陆
        Emp loginUser = WebUtil.getLoginUser();
        if(null == loginUser) {
            WebUtil.ajaxReturn(false, "你还没有登陆");
            return;
        }
        try {
			returnordersBiz.doOutCheck(loginUser.getUuid(),getId());
			WebUtil.ajaxReturn(true, "审核成功");
		} catch (ErpException e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "审核失败");
		}
    }

    
}
