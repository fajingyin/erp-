package cn.itcast.erp.biz.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.erp.biz.IReturnorderdetailBiz;
import cn.itcast.erp.dao.IReturnorderdetailDao;
import cn.itcast.erp.dao.IReturnordersDao;
import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.dao.IStoreoperDao;
import cn.itcast.erp.entity.Returnorderdetail;
import cn.itcast.erp.entity.Returnorders;
import cn.itcast.erp.entity.Storedetail;
import cn.itcast.erp.entity.Storeoper;
import cn.itcast.erp.exception.ErpException;

/**
 * 退货订单明细业务逻辑类
 *
 */
@Service("returnorderdetailBiz")
public class ReturnorderdetailBiz extends BaseBiz<Returnorderdetail> implements IReturnorderdetailBiz {

    private IReturnorderdetailDao returnorderdetailDao;
    
    @Autowired
    private IStoredetailDao storedetailDao;

    @Autowired
    private IStoreoperDao storeoperDao;
    
    @Autowired
    private IReturnordersDao returnordersDao;
    
    @Resource(name="returnorderdetailDao")
    public void setReturnorderdetailDao(IReturnorderdetailDao returnorderdetailDao) {
        this.returnorderdetailDao = returnorderdetailDao;
        super.setBaseDao(this.returnorderdetailDao);
    }
    
    /**
     * 退货订单出库
     * @author user4_3 闻彬 benkey
     */
	@Override
	@Transactional
    public void doOutStore(Long id, Long storeuuid, Long empuuid) {
    	//登录判断在Action前端做
    	//1.更新商品明细
    	//判断订单是否已出库
    	Returnorderdetail returnorderdetail = returnorderdetailDao.get(id);
    	if(Returnorderdetail.RETURN_STATE_OUT.equals(returnorderdetail.getState())) {
    		throw new ErpException("该明细已出库!");
    	}
    	returnorderdetail.setEnder(empuuid);
    	returnorderdetail.setEndtime(new Date());
    	returnorderdetail.setState(Returnorderdetail.RETURN_STATE_OUT);
    	returnorderdetail.setStoreuuid(storeuuid);
    	
    	//2.更新仓库库存
    	//目标仓库有则加值 没有就新建
    	//匹配仓库和商品数量 二者缺一不可
    	//先构建一个库存明细
    	Storedetail storedetail = new Storedetail();
    	storedetail.setGoodsuuid(returnorderdetail.getGoodsuuid());
    	storedetail.setStoreuuid(storeuuid);
    	//主要取list.get(0)
    	List<Storedetail> list = storedetailDao.getList(storedetail, null, null);
    	//有值
    	if(null!=list && list.size()>0) {
    		storedetail = list.get(0);
    		//判断库存是否足够
    		if(storedetail.getNum() >= returnorderdetail.getNum()) {
    			storedetail.setNum(storedetail.getNum() - returnorderdetail.getNum());
    		}
    		else {
    			throw new ErpException("库存不足!");
    		}
//		    		storedetail.update(updateStoreDetail);
    		//持久化自动维护updateStoreDetail
    	}else {
    		//否则返回库存不足
    		throw new ErpException("库存不足!");
    	}
    	
    	//3.记录操作记录
    	Storeoper operLog = new Storeoper();
    	operLog.setEmpuuid(empuuid);
    	operLog.setGoodsuuid(returnorderdetail.getGoodsuuid());
    	operLog.setNum(returnorderdetail.getNum());
    	operLog.setOpertime(returnorderdetail.getEndtime());
    	operLog.setStoreuuid(storeuuid);
    	operLog.setType(Storeoper.TYPE_OUT);
    	storeoperDao.add(operLog);
    	
    	//4.所有明细已出库,修改订单状态为已出库
    	//构建查询条件
    	//取出明细
    	Returnorderdetail queryParam = new Returnorderdetail();
    	Returnorders returnorders = returnorderdetail.getReturnorders();
    	queryParam.setReturnorders(returnorders);
    	queryParam.setState(Returnorderdetail.RETURN_STATE_NOT_OUT);
    	//如果没有未出库的,就证明全部已经入库了,修改主订单状态
    	Long count = returnorderdetailDao.getCount(queryParam, null, null);
    	if (count == 0) {
    		returnorders.setState(Returnorders.RETURN_STATE_END);
    		returnorders.setEnder(empuuid);
    		returnorders.setEndtime(returnorderdetail.getEndtime());
		}
    	
    }

	@Override
	@Transactional
	public void doInStore(Long uuid, Long storeuuid, Long empuuid) {
		 // 1. 明细表orderdetail 明细编号, 明细的对象进入持久化
        Returnorderdetail od = returnorderdetailDao.get(uuid);
        // 1.5 状态判断，如果不是 未库入库的 终止
      
        if (! Returnorderdetail.RETURN_STATE_NOT_IN.equals(od.getState())) {
            throw new ErpException("亲，该明细已经入库了");
        }
        // 1.1 结束日期 系统时间
        od.setEndtime(new Date());
        // 1.2 库管员 登陆用户
        od.setEnder(empuuid);
        // 1.3 仓库编号 前端传过来 提供下拉列表
        od.setStoreuuid(storeuuid);
        // 1.4 状态 1: 已入库
        od.setState(Returnorderdetail.RETURN_STATE_IN);

        // 2. 库存表storedetail
        // 2.1 判断是否存在库存信息
        // 根据仓库编号，商品编号 查询库存表 list.size() > 0
        // 构建查询条件
        Storedetail sd = new Storedetail();
        // 查询条件
        sd.setGoodsuuid(od.getGoodsuuid());
        sd.setStoreuuid(storeuuid);
        List<Storedetail> list = storedetailDao.getList(sd, null, null);
        // 2.2 如果存在库存信息
        if (list.size() > 0) {
            // 数量累加 list.get(0) 库存信息 持久状
            sd = list.get(0);
            // 取出库存的数量 + 明细的数量
            sd.setNum(sd.getNum() + od.getNum());
        } else {
            // 2.3 不存在库存信息
            // 插入新的记录:
            // 仓库编号 前端传过来
            // 商品编号 明细有
            // 数量 明细的数量
            sd.setNum(od.getNum());
            storedetailDao.add(sd);
        }
        // 3. 日志记录storeoper
        // 插入记录
        Storeoper log = new Storeoper();
        // 3.1 操作员工编号 登陆用户
        log.setEmpuuid(empuuid);
        // 3.2 操作日期 系统时间, 让入库的时间保持一致
        log.setOpertime(od.getEndtime());
        // 3.3 仓库编号 前端传过来
        log.setStoreuuid(storeuuid);
        // 3.4 商品编号 明细有
        log.setGoodsuuid(od.getGoodsuuid());
        // 3.5 数量 明细的数量
        log.setNum(od.getNum());
        // 3.6 操作的类型 1:入库
        log.setType(Storeoper.TYPE_IN);
        storeoperDao.add(log);
        
        // 4. 订单表orders, 订单进入持久态
        Returnorders returnorders = od.getReturnorders();
        // 4.1 判断订单下的所有明细是否都完成入库
        // 构建查询条件
        Returnorderdetail queryParam = new Returnorderdetail();
        // 查询条件: 订单编号 明细获取, 明细的状态0
        queryParam.setReturnorders(returnorders);
        queryParam.setState(Returnorderdetail.RETURN_STATE_NOT_IN);
        // 查询订单下的未入库的明细的个数getCount
        Long count = returnorderdetailDao.getCount(queryParam, null, null);
        // 4.2 如果还有明细没有入库count > 0
        // 不需要操作
        // 4.3 如果不存在未入库的明细count = 0
        returnorders = returnordersDao.get(returnorders.getUuid());
        if (count == 0) {
            // 更新订单:
            // 4.3.1 入库日期： 系统时间
            returnorders.setEndtime(od.getEndtime());
            // 4.3.2 库管员 登陆用户
            returnorders.setEnder(empuuid);
            // 4.3.3 状态 3: 已入库
            returnorders.setState(Returnorders.RETURNORDERS_STATE_STORE);
        }
	}	
}
