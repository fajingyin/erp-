package cn.itcast.erp.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.erp.biz.IReportBiz;
import cn.itcast.erp.util.WebUtil;

@Controller("reportAction")
@Scope("prototype")
@ParentPackage("struts-default")
@Namespace("/")
@Action("report")
public class ReportAction {

    private Date startDate;
    private Date endDate;
    private int year;
    private Long goodstypeuuid;
    
    private String returnTime;

	@Autowired
    private IReportBiz reportBiz;

	public void setGoodstypeuuid(Long goodstypeuuid) {
		this.goodstypeuuid = goodstypeuuid;
	}
	
    /**
     * 销售统计报表
     */
    public void orderReport() {
        List<Map<String,Object>> list = reportBiz.orderReport(startDate, endDate);
        WebUtil.write(list);
    }

    /**
     * 销售趋势
     */
    public void trendReport() {
        List<Map<String, Object>> list = reportBiz.trendReport(year);
        WebUtil.write(list);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setYear(int year) {
        this.year = year;
    }
    
    
    
    public void setReturnTime(String returnTime) {
		this.returnTime = returnTime;
	}

	/**
     * 销售退货统计
     */
    public void salesReturn(){
    	List<Map<String, Object>> list = reportBiz.salesReturn(startDate, endDate);
    	WebUtil.write(list);
    }
    
    /**
     * 销售退货统计明细
     */
    public void salesReturn2(){
    	List<Map<String, Object>> list = reportBiz.salesReturn2(goodstypeuuid, startDate, endDate);
    	WebUtil.write(list);
    }
    
    /**
     * 销售退货趋势
     */
    public void returnorderstrendReport() {
    	List<Map<String, Object>> list = reportBiz.returnorderstrendReport(year);
    	WebUtil.write(list);
    }
    
    /**
     *销售退货按时间统计
     */
    public void salesReturnTime() {
    	String[] times = returnTime.split("-");
    	times[1]=times[1].substring(0,times[1].length()-1);
    	int mon = Integer.parseInt(times[1]);
    	String startString=times[0];
    	Date  startTime=null;
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	Calendar ca = Calendar.getInstance();
    	if(mon<10) {
    		startString=startString+"-"+"0"+mon+"-"+"01";
    	}else {
    		startString=startString+"-"+mon+"-"+"01";
		}
    	try {
			startTime=format.parse(startString);		
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	ca.setTime(startTime);
    	ca.add(Calendar.MONTH, 1);
    	Date endTime=ca.getTime();
    	List<?> list=reportBiz.returnorderstrendReportTime(startTime,endTime);
    	WebUtil.write(list);
    }
    
}
