package cn.itcast.erp.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import cn.itcast.erp.dao.IInventoryDao;
import cn.itcast.erp.entity.Inventory;

/**
 * 盘盈盘亏数据访问类
 *
 */
@Repository("inventoryDao")
public class InventoryDao extends BaseDao<Inventory> implements IInventoryDao {

    /**
     * 构建查询条件
     * @param t1
     * @param t2
     * @param param
     * @return
     */
    @Override
    public DetachedCriteria getDetachedCriteria(Inventory inventory1,Inventory inventory2,Object param){
        DetachedCriteria dc=DetachedCriteria.forClass(Inventory.class);
        if(inventory1!=null){
            if(!StringUtils.isEmpty(inventory1.getType())){
                dc.add(Restrictions.eq("type", inventory1.getType()));
            }
            if(!StringUtils.isEmpty(inventory1.getState())){
                dc.add(Restrictions.eq("state", inventory1.getState()));
            }
            if(!StringUtils.isEmpty(inventory1.getRemark())){
                dc.add(Restrictions.like("remark", inventory1.getRemark(), MatchMode.ANYWHERE));
            }
            //登记开始日期 >=
            if (inventory1.getCreatetime() != null ) {
				dc.add(Restrictions.ge("createtime", inventory1.getCreatetime()));
			}
            //审核的开始日期
            if (inventory1.getChecktime() != null) {
				dc.add(Restrictions.ge("checktime", inventory1.getChecktime()));
			}
        }
        if (inventory2 != null ) {
        	//登记结束日期
        	if (inventory2.getCreatetime() != null) {
				dc.add(Restrictions.le("createtime", inventory2.getCreatetime()));
			}
        	//审核结束日期
			if (inventory2.getChecktime() != null) {
				dc.add(Restrictions.le("checktime", inventory2.getChecktime()));
			}
		}
        return dc;
    }
}
