package cn.itcast.erp.biz;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报表业务
 *
 */
public interface IReportBiz {

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
    List<Map<String, Object>> trendReport(int year);
    
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

	List<Map<String, Object>> returnorderstrendReport(int year);

	List<?> returnorderstrendReportTime(Date startTime, Date endTime);
}
