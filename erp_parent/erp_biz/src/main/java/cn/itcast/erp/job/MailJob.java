package cn.itcast.erp.job;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import cn.itcast.erp.biz.IStoredetailBiz;
import cn.itcast.erp.entity.Storealert;
import cn.itcast.erp.util.MailUtil;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;

/**
 * 后台定时发送预警邮件
 *
 */
@Component("mailJob")
public class MailJob {

    @Autowired
    private MailUtil mailUtil;

    @Autowired
    private IStoredetailBiz storedetailBiz;

    @Value("${mail.storealert_backend_title}")
    private String title;
    @Value("${mail.storealert_backend_to}")
    private String to;
    @Value("${mail.storealert_backend_content}")
    private String content;
    @Autowired
    private Configuration freeMarke;

    public void doJob() {
    	try {
			Template template = freeMarke.getTemplate("email.html");
			HashMap<String, Object> dataModel = new HashMap<String,Object>();
	        List<Storealert> list = storedetailBiz.getStorealertList();
	        dataModel.put("storealertList", list);
	        if(null != list && list.size() > 0) {
	            // 有预警的商品
	        	
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	            String _title = title.replace("[time]",sdf.format(new Date()));
	           // String _content = content.replace("[count]", list.size() + "");
	           String _content=FreeMarkerTemplateUtils.processTemplateIntoString(template,dataModel);
	            // 存在库存预警
	            try {
	                mailUtil.sendMail(_title+"模板邮件", to, _content);
	                System.out.println("邮件发送成功");
	            } catch (MessagingException e) {
	                e.printStackTrace();
	            }
	        }
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    	
    }
}
