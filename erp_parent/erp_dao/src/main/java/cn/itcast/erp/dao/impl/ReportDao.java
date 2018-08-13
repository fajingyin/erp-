package cn.itcast.erp.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.aspectj.weaver.ast.And;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import cn.itcast.erp.dao.IReportDao;

@Repository("reportDao")
@SuppressWarnings("unchecked")
public class ReportDao extends HibernateDaoSupport implements IReportDao {

    @Resource(name="sessionFactory")
    public void setSf(SessionFactory sf) {
        super.setSessionFactory(sf);
    }


    @Override
    public List<Map<String,Object>> orderReport(Date startDate, Date endDate) {
        String hql = "select new Map(gt.name as name,sum(od.money) as y) from Goodstype gt,Goods g,Orderdetail od, Orders o " +
                "where gt=g.goodstype and g.uuid=od.goodsuuid " +
                "and o=od.orders and o.type='2' ";
        List<Date> params = new ArrayList<Date>();
        if(null != startDate) {
            hql += "and o.createtime>=? ";
            params.add(startDate);
        }
        if(null != endDate) {
            hql += "and o.createtime<=? ";
            params.add(endDate);
        }
        hql+="group by gt.name";
        return (List<Map<String,Object>>)this.getHibernateTemplate().find(hql, params.toArray());
    }


    @Override
    public Map<String, Object> trendReport(int month, int year) {
        //select extract (month from sysdate) from dual;
        String hql="select new Map(month(o.createtime) as name,sum(od.money) as y) from Orders o, Orderdetail od " +
                "where o=od.orders and o.type='2' " +
                "and year(o.createtime)=? and month(o.createtime)=? " +
                "group by month(o.createtime)";
        List<?> list = this.getHibernateTemplate().find(hql, year,month);
        if(null != list && list.size() > 0) {
            return (Map<String, Object>)list.get(0);
        }
        return null;
    }

    
    
    /**
     * 销售退货统计
     */
	@Override
	public List<Map<String, Object>> salesReturn(Date startDate, Date endDate) {
		String hql = "select new Map(gt.name as name,sum(rod.money) as y,sum(rod.num) as n,gt.uuid as goodstypeuuid) from Goodstype gt,Goods g,Returnorderdetail rod, Returnorders ro " +
                "where gt=g.goodstype and g.uuid=rod.goodsuuid " +
                "and ro=rod.returnorders and ro.type='2' ";
		List<Date> params = new ArrayList<>();
		if (null != startDate) {
			hql += "and ro.createtime >=?";
			params.add(startDate);
		}
		if (null != endDate) {
			hql += "and ro.createtime <=?";
			params.add(endDate);
		}
		hql += "group by gt.name,gt.uuid";
		return (List<Map<String, Object>>) this.getHibernateTemplate().find(hql, params.toArray());
	}
	
	/**
     * 销售退货统计明细
     */
	@Override
	public List<Map<String, Object>> salesReturn2(Long goodstypeuuid, Date startDate, Date endDate) {
		String hql = "select new Map(g.name as name, sum(rod.money) as y,sum(rod.num) as n,g.uuid as uuid) "
				+ "from Goods g,Returnorderdetail rod, Returnorders ro " +
                "where g.uuid=rod.goodsuuid " +
                "and ro=rod.returnorders and ro.type='2' and  g.goodstype.uuid=? ";
		List<Object> params = new ArrayList<>();
		params.add(goodstypeuuid);
		if (null != startDate) {
			hql += "and ro.createtime >=?";
			params.add(startDate);
		}
		if (null != endDate) {
			hql += "and ro.createtime <=?";
			params.add(endDate);
		}
		hql += "group by g.name, g.uuid";
		return (List<Map<String, Object>>) this.getHibernateTemplate().find(hql, params.toArray());
	}
	

	@Override
	public Map<String, Object> returnorders_trendReport(int month, int year) {
		   //select extract (month from sysdate) from dual;
        String hql="select new Map(month(o.createtime) || '月' as name ,sum(od.money) as y,count(1) as num) from Returnorders o, Returnorderdetail od " +
                "where o=od.returnorders and o.type='2' " +
                "and year(o.createtime)=? and month(o.createtime)=? " +
                "group by month(o.createtime)";
        List<?> list = this.getHibernateTemplate().find(hql, year,month);
        if(null != list && list.size() > 0) {
            return (Map<String, Object>)list.get(0);
        }
        return null;
	}

	@Override
	public Map<String, Object> returnorders_trendReport2(int month, int year) {
		   //select extract (month from sysdate) from dual;
        String hql="select new Map(month(o.createtime) as name,count(1) as y) from Returnorders o, Returnorderdetail od " +
                "where o=od.returnorders and o.type='2' " +
                "and year(o.createtime)=? and month(o.createtime)=? " +
                "group by month(o.createtime)";
        List<?> list = this.getHibernateTemplate().find(hql, year,month);
        if(null != list && list.size() > 0) {
            return (Map<String, Object>)list.get(0);
        }
        return null;
	}


	@Override
	public List<?> returnorderstrendReportTime(Date startTime, Date endTime) {
		String hql=" select new Map(rod.goodsname as name,sum(rod.money) as y) "
				+ "from Returnorderdetail rod,Returnorders ro " + 
				" where rod.returnorders=ro " + 
				" and ro.createtime between ? and ? " + 
				"  group by goodsname";
		  return  this.getHibernateTemplate().find(hql, startTime,endTime);
		  
	        
	}
	
	
	
	
}
