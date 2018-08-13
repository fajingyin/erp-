package cn.itcast.erp.biz.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.erp.biz.IReturnordersBiz;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IGoodsDao;
import cn.itcast.erp.dao.IOrderdetailDao;
import cn.itcast.erp.dao.IOrdersDao;
import cn.itcast.erp.dao.IReturnordersDao;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.GoodsNum;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.entity.Returnorderdetail;
import cn.itcast.erp.entity.Returnorders;
import cn.itcast.erp.exception.ErpException;

/**
 * 退货订单业务逻辑类
 *
 */
@Service("returnordersBiz")
public class ReturnordersBiz extends BaseBiz<Returnorders> implements IReturnordersBiz {

    private IReturnordersDao returnordersDao;
    
    @Autowired
    private IEmpDao empDao;
    @Autowired
    private ISupplierDao supplierDao;
    @Autowired
    private IOrdersDao ordersDao;
    @Autowired
    private IGoodsDao goodsDao;

    @Resource(name="returnordersDao")
    public void setReturnordersDao(IReturnordersDao returnordersDao) {
        this.returnordersDao = returnordersDao;
        super.setBaseDao(this.returnordersDao);
    }
    
    /* 
     * 刘正伟
     * 销售退货登记
     */
    @Override
    @Transactional
    public void add(Returnorders t) {
    	
    	 ArrayList <String> goodsnames = new ArrayList <> ();//将原订单中所有商品名存到集合中
         Map<String,Long> goodmap = new HashMap <> ();//将原订单中 以商品名为key 以该商品的金额为value 存入map汇总
         List<Orderdetail> orderDetails = ordersDao.get(t.getOrdersuuid()).getOrderDetails();
         
         for (Orderdetail od : orderDetails) {
             goodsnames.add (getGoodsName (od.getGoodsuuid ()));
             goodmap.put (getGoodsName (od.getGoodsuuid ()),od.getNum ());
         }
    	//设置下单时间
    	t.setCreatetime(new Date()); 	
    	double sum = 0;//计算totalmoney 总金额
    	String rodstate = "0";//未入库状态
    	//Subject subject = SecurityUtils.getSubject();
    	if (Returnorders.RETURN_TYPE_IN.equals(t.getType())) {
    		//如果是销售退货订单 则设置状态为未审核0
    		/*if (!subject.isPermitted("销售退货登记")) {
				throw new ErpException("你没有添加销售退货登记的权限");
			}*/
    		rodstate = Returnorders.RETURNORDERDETAIL_STATE_UNSTORE;//设置销售退货订单明细的状态为未入库
		}
    	//遍历销售退货订单明细  设置明细状态 和  returnorders
    	List<Returnorderdetail> returnorderdetails = t.getReturnorderdetails();
    	for (Returnorderdetail returnorderdetail : returnorderdetails) {
			sum += returnorderdetail.getMoney();
			returnorderdetail.setState(rodstate);
			returnorderdetail.setReturnorders(t);
			  //如果原订单中不存在该商品名
            if (!goodsnames.contains (getGoodsName (returnorderdetail.getGoodsuuid ()))){
                throw  new ErpException(getGoodsName (returnorderdetail.getGoodsuuid ())+"不存在于原订单");
            }else{
                //如果存在  则判断商品数量
                if (returnorderdetail.getNum ()>goodmap.get (getGoodsName (returnorderdetail.getGoodsuuid ()))){
                        throw new ErpException (getGoodsName (returnorderdetail.getGoodsuuid ())+"的数量大于原订单中的商品数量");
                }
            }
		}
    	t.setTotalmoney(sum);
    	returnordersDao.add(t);
    }
    
   
    /**
     * 刘正伟
     * 通过商品的uuid获取商品的名称
     * @param uuid
     * @return
     */
    private String  getGoodsName(Long uuid){
       return goodsDao.get (uuid).getName ();
    }

    
  //尹飞月
  	@Override
  	@Transactional
  	public void addReturn(Returnorders returnorders) {
  			returnorders.setCreatetime(new Date());
  	        
  	       
  	        // 循环所有明细进行累加
  	        double totalmoney = 0;
  	        //查询条件的设置
  	      Orders orders = ordersDao.get(returnorders.getOrdersuuid());
  	      List<Orderdetail> orderDetails = orders.getOrderDetails();
  	       List<Returnorderdetail> returnorderdetails=new ArrayList<>();
  	       List<Long> list=new ArrayList<>();
  	       for (Orderdetail orderdetail : orderDetails) {
			list.add(orderdetail.getGoodsuuid());
  	       }
  	        for(Returnorderdetail od:returnorders.getReturnorderdetails()) {
  	        	//List<GoodsNum> list = returnordersDao.selectCount(od.getGoodsuuid(),returnorders.getSupplieruuid());
  	        	for (Orderdetail orderdetail:orderDetails) {
					if(od.getGoodsuuid()==orderdetail.getGoodsuuid() && (od.getNum() >orderdetail.getNum())) {
						throw new ErpException("退货数量超过订单数量");
					}
					
					
				}
  	        	if(!list.contains(od.getGoodsuuid())) {
  	        		throw new ErpException("此产品未采购");
  	        	}
  	        
  	        	
  	        	 totalmoney += od.getMoney();
  		            // 明细的状态
  		            od.setState(Returnorderdetail.RETURN_STATE_NOT_OUT);
  		            // 设置明细与订单的关系, 在插入明细时，把订单的编号交给明细
  		            od.setReturnorders(returnorders);
  		            returnorderdetails.add(od);
  	        }
  	        returnorders.setTotalmoney(totalmoney);//      1.5 合计金额   通过明细累计
  	       returnorders.setReturnorderdetails(returnorderdetails);
  	       returnordersDao.add(returnorders);
  	}
  	
  
    @Override
    public List<Returnorders> getListByPage(Returnorders t1, Returnorders t2, Object obj, int startRow,
    		int maxResults) {
    	List<Returnorders> list = super.getListByPage(t1, t2, obj, startRow, maxResults);
    	//遍历获取的集合 ,设置响应的name
    	for (Returnorders returnorders : list) {
			returnorders.setCreaterName(getEmpName(returnorders.getCreater()));
			returnorders.setCheckerName(getEmpName(returnorders.getChecker()));
			returnorders.setEnderName(getEmpName(returnorders.getEnder()));
			returnorders.setSupplierName(supplierDao.get(returnorders.getSupplieruuid()).getName());
		} 	
    	return list;
    }
    
    /**
     * 通过UUID去查找并判断相应的员工是否为null
     * @param uuid
     * @return
     */
    public String getEmpName(Long uuid){
        if (null == uuid){
            return null;
        }
        return empDao.get (uuid).getName ();
    }

	@Override
	@Transactional
	 public void doCheck(Long id, Long loginUuid) {
        Returnorders returnorders = returnordersDao.get(id);
        if(!Returnorders.RETURN_STATE_CREATE.equals(returnorders.getState())) {
        	// 不是未确认状态
        	throw new ErpException("亲，该订单已经审核过了");
        }
        returnorders.setChecker(loginUuid);
        returnorders.setChecktime(new Date());
        returnorders.setState(Returnorders.RETURN_STATE_CHECK);// 更新状态为未入库
//        returnordersDao.update(returnorders);
    }
	
	
	/**
	 * 采购退货审核
	 * @author 李范
	 */
	@Override
	@Transactional
    public void doOutCheck(Long empuuid, Long uuid) {
    	//进入持久化状态
		Returnorders rd = returnordersDao.get(uuid);
		//判断订单审核状态
		if(Returnorders.RETURN_STATE_CHECK.equals(rd.getState())) {
			//已审核
			throw new ErpException("该退货订单已审核，不可重复审核");
		}
		//设置审核日期 审核人 审核状态
		rd.setChecktime(new Date());
		rd.setChecker(empuuid);
		rd.setState(Returnorders.RETURN_STATE_CHECK);
    	
    }

	
}
