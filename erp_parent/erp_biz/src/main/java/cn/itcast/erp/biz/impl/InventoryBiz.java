package cn.itcast.erp.biz.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.erp.biz.IInventoryBiz;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IGoodsDao;
import cn.itcast.erp.dao.IInventoryDao;
import cn.itcast.erp.dao.IStoreDao;
import cn.itcast.erp.dao.impl.EmpDao;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Inventory;
import cn.itcast.erp.exception.ErpException;

/**
 * 盘盈盘亏业务逻辑类
 *
 */
@SuppressWarnings("unused")
@Service("inventoryBiz")
public class InventoryBiz extends BaseBiz<Inventory> implements IInventoryBiz {
	
	//注解注入
	@Autowired
	private IGoodsDao goodsDao;
	@Autowired
	private IStoreDao storeDao;
	@Autowired
	private IEmpDao empDao;
	
    private IInventoryDao inventoryDao;

    @Resource(name="inventoryDao")
    public void setInventoryDao(IInventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
        super.setBaseDao(this.inventoryDao);
    }

	@Override
	@Transactional
	public void add(Inventory inventory, Long uuid) {
    	//设置登记时间
    	inventory.setCreatetime(new Date());
    	//设置登记人
    	inventory.setCreater(uuid);  	
    	//判断盈利或者盈亏
    	if(Inventory.INVENTORY_TYPE_GAIN.equals(inventory.getType())) {
    		inventory.setType(Inventory.INVENTORY_TYPE_GAIN);
    	}
    	inventory.setType(Inventory.INVENTORY_TYPE_LOSE);
    	inventory.setState(Inventory.STATE_NOCHECK);
		inventoryDao.add(inventory);
	} 
	
	@Override
	public List<Inventory> getListByPage(Inventory t1, Inventory t2, Object obj, int startRow, int maxResults) {
		// TODO Auto-generated method stub
		 List<Inventory> list = super.getListByPage(t1, t2, obj, startRow, maxResults);
		 for (Inventory inventory : list) {	
			if(null != inventory.getGoodsuuid()) {
				//显示的商品名称
				inventory.setGoodsName(goodsDao.get(inventory.getGoodsuuid()).getName());
			}
			if(null != inventory.getStoreuuid()) {
				//显示仓库名字
				inventory.setStoreName(storeDao.get(inventory.getStoreuuid()).getName());
			}
			if(null != inventory.getCreater()) {
				//显示登记人的名字
				inventory.setCreaterName(getEmpName(inventory.getCreater()));
			}
			if(null != inventory.getChecker()) {
				//设置审核人名称
				inventory.setCheckerName(getEmpName(inventory.getChecker()));
			}
		} 
		 return list;
	}
	
	/**
	 * 梁举
	 * 通过UUID去获取员工的名字
	 * @param uuid
	 * @return
	 */
	
	private String getEmpName(Long uuid){
		Emp emp = empDao.get(uuid);
		if (emp != null) {
			return empDao.get(uuid).getName(); 
		}		
		return null;	
	}
	
  
	@Override
	@Transactional
	public void doCheck(Long empuuid,Long uuid) {
		//获取盘盈盘亏实体类
		Inventory inventory = inventoryDao.get(uuid);
		//判断状态
		if(!Inventory.STATE_NOCHECK.equals(inventory.getState())){
			throw new ErpException("亲,你已经审核过了,请勿重复操作~");
		}
		//设置审核日期
		inventory.setChecktime(new Date());
		//设置状态
		inventory.setState(Inventory.STATE_CHECK);
		//设置审核人
		inventory.setChecker(empuuid);
	}

	/* 
	 * 更新数据
	@Override
	public void update(Inventory t, Long id) {
		Inventory inventory = inventoryDao.get(id);
		
		
	}*/
    
}
