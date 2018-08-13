package cn.itcast.erp.biz;
import cn.itcast.erp.entity.Returnorderdetail;
/**
 * 退货订单明细业务逻辑层接口
 *
 */
public interface IReturnorderdetailBiz extends IBaseBiz<Returnorderdetail>{
	/**
     * 退货订单出库
     * @author user4_3 闻彬 benkey
     */
	void doOutStore(Long id, Long storeuuid, Long uuid);
	
	void doInStore(Long uuid,Long storeUuid,Long empUuid);

}

