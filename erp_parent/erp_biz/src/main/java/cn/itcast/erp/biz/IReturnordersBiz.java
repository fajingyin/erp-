package cn.itcast.erp.biz;
import cn.itcast.erp.entity.Returnorders;
/**
 * 退货订单业务逻辑层接口
 *
 */
public interface IReturnordersBiz extends IBaseBiz<Returnorders>{
void doCheck(Long id,Long loginUuid);
	
	/**
	 * 采购退货订单审核
	 */
	void doOutCheck(Long empuuid,Long uuid);
	
	void addReturn(Returnorders oReturnorders);
}

