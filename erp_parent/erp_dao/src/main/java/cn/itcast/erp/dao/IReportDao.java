package cn.itcast.erp.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报表Dao
 *
 */
public interface IReportDao {

    /**
     * 销售统计
     * @param startDate
     * @param endDate
     * @return
     */
    List<Map<String,Object>> orderReport(Date startDate, Date endDate);

    /**
     * 销售趋势
     * @param month
     * @param year
     * @return
     */
    Map<String,Object> trendReport(int month,int year);
    
    
    
    /**
     * 销售退货统计
     * @param startDate
     * @param endDate
     * @return
     */
    List<Map<String, Object>> salesReturn(Date startDate,Date endDate);
    /**
     * 销售退货统计明细
     * @param startDate
     * @param endDate
     * @return
     */
    List<Map<String, Object>> salesReturn2(Long goodstypeuuid, Date startDate, Date endDate);

	Map<String, Object> returnorders_trendReport(int month, int year);

	Map<String, Object> returnorders_trendReport2(int month, int year);

	List<?> returnorderstrendReportTime(Date startTime, Date endTime);	
    
    
}
