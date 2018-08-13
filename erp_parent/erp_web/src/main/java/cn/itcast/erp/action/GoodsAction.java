package cn.itcast.erp.action;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.erp.biz.IGoodsBiz;
import cn.itcast.erp.entity.Goods;
import cn.itcast.erp.exception.ErpException;
import cn.itcast.erp.util.WebUtil;

@Controller("goodsAction")
@Scope("prototype")
@ParentPackage("struts-default")
@Namespace("/")
@Action("goods")
public class GoodsAction extends BaseAction<Goods> {

    private IGoodsBiz goodsBiz;

    @Resource(name="goodsBiz")
    public void setGoodsBiz(IGoodsBiz goodsBiz) {
        this.goodsBiz = goodsBiz;
        super.setBaseBiz(this.goodsBiz);
    }
    
    /**
     * 导出文件
     */
    public void export() {
    	String filename = "商品表.xls";
    	HttpServletResponse response = ServletActionContext.getResponse();
    	 try {
    	        response.setHeader("Content-Disposition", "attachment;filename=" +
    	                new String(filename.getBytes(),"ISO-8859-1"));//中文名称进行转码
    	        //调用导出业务
    	        goodsBiz.export(response.getOutputStream(), getT1());
    	    } catch (UnsupportedEncodingException e) {
    	        e.printStackTrace();
    	    } catch (IOException e) {
    	        e.printStackTrace();
    	}
    }
    
    public void doImport() {
    	if(!"application/vnd.ms-execl".equals(getFileContentType()) && !getFileFileName().endsWith(".xls")) {
    		WebUtil.ajaxReturn(false, "上传的文件必须是excel文件!");
    		return;
    	}
    	try {
			goodsBiz.doImport(new FileInputStream(getFile()));
			WebUtil.ajaxReturn(true, "文件上传成功");
		} catch (ErpException e) {
			WebUtil.ajaxReturn(false, e.getMessage());
		}catch (IOException e) {
			WebUtil.ajaxReturn(false, "文件上传失败");
			e.printStackTrace();
		}
    }


    
}
