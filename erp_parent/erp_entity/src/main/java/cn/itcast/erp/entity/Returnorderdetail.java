package cn.itcast.erp.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 退货订单明细实体类
 */
@Entity
@Table(name="returnorderdetail")
public class Returnorderdetail {
	
	/**
     * 采购退单明细的状态：未出库
     */
    public static final String RETURN_STATE_NOT_OUT = "0";

    /**
     * 采购退单明细的状态：已出库
     */
    public static final String RETURN_STATE_OUT = "1";
    
    /**
     * 销售退单明细的状态：未入库
     */
    public static final String RETURN_STATE_NOT_IN = "0";

    /**
     * 销售退单明细的状态：已入库
     */
    public static final String RETURN_STATE_IN = "1";
	
    @Id
    @GeneratedValue(generator="returnorderdetailKeyGenerator",strategy=GenerationType.SEQUENCE)
    @GenericGenerator(name="returnorderdetailKeyGenerator",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",
                parameters= {@Parameter(name="sequence_name",value="returnorderdetail_seq")}
            )
    private Long uuid;//编号
	private Long goodsuuid;//商品编号
	private String goodsname;//商品名称
	private Double price;//价格
	private Long num;//数量
	private Double money;//金额
	private java.util.Date endtime;//结束日期
	private Long ender;//库管员
	private Long storeuuid;//仓库编号
	private String state;//状态
	//private Long ordersuuid;//退货订单编号
	
	@ManyToOne(targetEntity=Returnorders.class)
	@JoinColumn(name="ORDERSUUID")
	@JSONField(serialize=false)
	private Returnorders returnorders;
	
	public Returnorders getReturnorders() {
		return returnorders;
	}
	public void setReturnorders(Returnorders returnorders) {
		this.returnorders = returnorders;
	}
	public Long getUuid() {		
		return uuid;
	}
	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}
	public Long getGoodsuuid() {		
		return goodsuuid;
	}
	public void setGoodsuuid(Long goodsuuid) {
		this.goodsuuid = goodsuuid;
	}
	public String getGoodsname() {		
		return goodsname;
	}
	public void setGoodsname(String goodsname) {
		this.goodsname = goodsname;
	}
	public Double getPrice() {		
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Long getNum() {		
		return num;
	}
	public void setNum(Long num) {
		this.num = num;
	}
	public Double getMoney() {		
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public java.util.Date getEndtime() {		
		return endtime;
	}
	public void setEndtime(java.util.Date endtime) {
		this.endtime = endtime;
	}
	public Long getEnder() {		
		return ender;
	}
	public void setEnder(Long ender) {
		this.ender = ender;
	}
	public Long getStoreuuid() {		
		return storeuuid;
	}
	public void setStoreuuid(Long storeuuid) {
		this.storeuuid = storeuuid;
	}
	public String getState() {		
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
/*	public Long getOrdersuuid() {		
		return ordersuuid;
	}
	public void setOrdersuuid(Long ordersuuid) {
		this.ordersuuid = ordersuuid;
	}*/

}
