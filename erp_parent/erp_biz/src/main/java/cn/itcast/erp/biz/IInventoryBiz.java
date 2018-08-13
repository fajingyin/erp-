package cn.itcast.erp.biz;
import cn.itcast.erp.entity.Inventory;
/**
 * 盘盈盘亏业务逻辑层接口
 *
 */
public interface IInventoryBiz extends IBaseBiz<Inventory>{
	
	/**
	 * @param empuuid
	 * @param uuid
	 * 盘盈盘亏审核
	 */
	void doCheck(Long empuuid,Long uuid);
	
	/**
	 * @param inventory
	 * @param uuid
	 * 盘盈盘亏登记
	 */
	void add(Inventory inventory, Long uuid);
	
	
}

