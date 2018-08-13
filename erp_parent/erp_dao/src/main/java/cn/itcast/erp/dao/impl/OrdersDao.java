package cn.itcast.erp.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import cn.itcast.erp.dao.IOrdersDao;
import cn.itcast.erp.entity.Orders;

/**
 * 订单数据访问类
 *
 */
@Repository("ordersDao")
public class OrdersDao extends BaseDao<Orders> implements IOrdersDao {

    /**
     * 构建查询条件
     * @param t1
     * @param t2
     * @param param
     * @return
     */
    @Override
    public DetachedCriteria getDetachedCriteria(Orders orders1,Orders orders2,Object param){
        DetachedCriteria dc=DetachedCriteria.forClass(Orders.class);
        if(orders1!=null && orders2==null){
        	 if(!StringUtils.isEmpty(orders1.getState())){
                 dc.add(Restrictions.eq("state", orders1.getState()));
             }
        	dc = addRestr( dc,orders1);
        }
        //or逻辑
        if(orders1!=null && orders2!=null){
        	if(orders1.getState()!=null &&orders2.getState()!=null) {
        	dc.add(Restrictions.or(Restrictions.eq("state", orders1.getState()), Restrictions.eq("state", orders2.getState())));
        	}
        	dc = addRestr( dc,orders1);
        }

        return dc;
    }
    
    
    private DetachedCriteria addRestr(DetachedCriteria dc,Orders orders1) {
    	if(!StringUtils.isEmpty(orders1.getType())){
            dc.add(Restrictions.eq("type", orders1.getType()));
        }
       
        // 下单员
        if(null != orders1.getCreater()) {
            dc.add(Restrictions.eq("creater", orders1.getCreater()));
        }
        if (null!=orders1.getSupplieruuid ()){
            dc.add(Restrictions.eq("supplieruuid",orders1.getSupplieruuid ()));
        }
        return dc;
    }
}
