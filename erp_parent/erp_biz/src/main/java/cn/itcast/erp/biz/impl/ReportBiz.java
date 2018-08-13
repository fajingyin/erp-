package cn.itcast.erp.biz.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.itcast.erp.biz.IReportBiz;
import cn.itcast.erp.dao.IReportDao;

@Service("reportBiz")
public class ReportBiz implements IReportBiz {

    @Autowired
    private IReportDao reportDao;

    @Override
    public List<Map<String,Object>> orderReport(Date startDate, Date endDate) {
        return reportDao.orderReport(startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> trendReport(int year) {
        // 保存12个月的数据
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        // 循环12个月，分别每个月查询数据
        Map<String, Object> monthData = null;
        for(int i = 1; i <= 12; i++) {
            monthData = reportDao.trendReport(i, year);
            if(null == monthData) {
                // 没有这个月的销售额，要补0
                //{name:1,y:0}
                monthData = new HashMap<String,Object>();
                monthData.put("name", i);
                monthData.put("y", 0);
            }
            result.add(monthData);
        }
        return result;
    }
    
    
    /**
     * 销售退货统计
     */
	@Override
	public List<Map<String, Object>> salesReturn(Date startDate, Date endDate) {
		
		return reportDao.salesReturn(startDate, endDate);
	}

	@Override
	public List<Map<String, Object>> returnorderstrendReport(int year) {
		 // 保存12个月的数据
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        // 循环12个月，分别每个月查询数据
        Map<String, Object> money_monthData = null;
        for(int i = 1; i <= 12; i++) {
            money_monthData = reportDao.returnorders_trendReport(i, year);
            if(null == money_monthData) {
                // 没有这个月的销售额，要补0
                //{name:1,y:0}
            	money_monthData = new HashMap<String,Object>();
            	money_monthData.put("name", i+"月");
            	money_monthData.put("y", 0);
            	money_monthData.put("num", 0);
            }
            result.add(money_monthData);
        }
        /* Map<String, Object> quantity_monthData = null;
        for(int i = 1; i <= 12; i++) {
        	quantity_monthData = reportDao.returnorders_trendReport2(i, year);
            if(null == quantity_monthData) {
                // 没有这个月的销售额，要补0
                //{name:1,y:0}
            	quantity_monthData = new HashMap<String,Object>();
            	quantity_monthData.put("name", i);
            	quantity_monthData.put("y", 0);
            }
            result.add(quantity_monthData);
        }*/
        return result;
	}
    
	@Override
	public List<Map<String,Object>> salesReturn2(Long goodstypeuuid, Date startDate, Date endDate){
		return reportDao.salesReturn2(goodstypeuuid, startDate, endDate);
		
	}

	@Override
	public List<?> returnorderstrendReportTime(Date startTime, Date endTime) {
		
		return reportDao.returnorderstrendReportTime(startTime,endTime);
	}
    

}
