package cn.itcast.erp.dao;

import java.util.List;

import cn.itcast.erp.entity.GoodsNum;
import cn.itcast.erp.entity.Returnorders;

/**
 * 退货订单数据访问接口
 *
 */
public interface IReturnordersDao extends IBaseDao<Returnorders>{

	List<GoodsNum> selectCount(Long goodsuuid,Long supplieruuid);

}
