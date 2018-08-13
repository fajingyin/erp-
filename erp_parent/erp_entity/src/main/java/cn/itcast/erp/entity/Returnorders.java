package cn.itcast.erp.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


/**
 * 退货订单实体类
 */
@Entity
@Table(name="returnorders")
public class Returnorders {
	
	/**
	 * 销售退货明细订单的状态state 0 为未入库 
	 */
	public static final String RETURNORDERDETAIL_STATE_UNSTORE = "0";
	
	/**
	 * 销售退货明细订单的状态state 1 为已入库  
	 */
	public static final String RETURNORDERDETAIL_STATE_STORE = "1";
	
	/**
	 * 销售退货订单的状态state   0 未审核
	 */
	public static final String  RETURNORDERS_STATE_UNCHECK="0";
	/**
	 * 销售退货订单的状态state   1 为未入库  
	 */
	public static final String  RETURNORDERS_STATE_UNSTORE = "1";
	/**
	 * 销售退货订单的状态state     2为已入库
	 */
	public static final String  RETURNORDERS_STATE_STORE = "2";
	
	/**
     * 退单类型：采购退单
     */
    public static final String RETURN_TYPE_OUT = "1";

    /**
     * 退单类型：销售退单
     */
    public static final String RETURN_TYPE_IN = "2";

    /**
     * 采购退单状态：未审核
     */
    public static final String RETURN_STATE_CREATE = "0";

    /**
     * 采购退单状态：未入库
     */
    public static final String RETURN_STATE_CHECK = "1";

    /**
     * 采购退单状态：已入库
     */
    public static final String RETURN_STATE_END = "2";
	
    @Id
    @GeneratedValue(generator="returnordersKeyGenerator",strategy=GenerationType.SEQUENCE)
    @GenericGenerator(name="returnordersKeyGenerator",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",
                parameters= {@Parameter(name="sequence_name",value="returnorders_seq")}
            )
    private Long uuid;//编号
    private Long creater;//下单员
	private java.util.Date createtime;//生成日期
	@Transient
	private String createrName; //下单员名称
	
	private Long checker;//审核员
	private java.util.Date checktime;//检查日期
	@Transient
	private String checkerName;//审核员名称
	
	private Long ender;//库管员
	@Transient
	private String enderName;//入库员名称
	private java.util.Date endtime;//结束日期
	
	private String type;//订单类型
	private Long supplieruuid;//供应商及客户编号
	@Transient
	private String supplierName;//供应商名称
	private Double totalmoney;//合计金额
	private String state;//订单状态
	private Long waybillsn;//运单号
	private Long ordersuuid;//原订单编号
	
	//退货订单明细
	@OneToMany(targetEntity=Returnorderdetail.class,cascade={CascadeType.ALL},mappedBy="returnorders")
	private List<Returnorderdetail> returnorderdetails;

	public Long getOrdersuuid() {
		return ordersuuid;
	}

	public void setOrdersuuid(Long ordersuuid) {
		this.ordersuuid = ordersuuid;
	}

	public Long getUuid() {
		return uuid;
	}

	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}

	public Long getCreater() {
		return creater;
	}

	public void setCreater(Long creater) {
		this.creater = creater;
	}

	public java.util.Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(java.util.Date createtime) {
		this.createtime = createtime;
	}

	public String getCreaterName() {
		return createrName;
	}

	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}

	public Long getChecker() {
		return checker;
	}

	public void setChecker(Long checker) {
		this.checker = checker;
	}

	public java.util.Date getChecktime() {
		return checktime;
	}

	public void setChecktime(java.util.Date checktime) {
		this.checktime = checktime;
	}

	public String getCheckerName() {
		return checkerName;
	}

	public void setCheckerName(String checkerName) {
		this.checkerName = checkerName;
	}

	public Long getEnder() {
		return ender;
	}

	public void setEnder(Long ender) {
		this.ender = ender;
	}

	public String getEnderName() {
		return enderName;
	}

	public void setEnderName(String enderName) {
		this.enderName = enderName;
	}

	public java.util.Date getEndtime() {
		return endtime;
	}

	public void setEndtime(java.util.Date endtime) {
		this.endtime = endtime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getSupplieruuid() {
		return supplieruuid;
	}

	public void setSupplieruuid(Long supplieruuid) {
		this.supplieruuid = supplieruuid;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public Double getTotalmoney() {
		return totalmoney;
	}

	public void setTotalmoney(Double totalmoney) {
		this.totalmoney = totalmoney;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Long getWaybillsn() {
		return waybillsn;
	}

	public void setWaybillsn(Long waybillsn) {
		this.waybillsn = waybillsn;
	}

	public List<Returnorderdetail> getReturnorderdetails() {
		return returnorderdetails;
	}

	public void setReturnorderdetails(List<Returnorderdetail> returnorderdetails) {
		this.returnorderdetails = returnorderdetails;
	}
	
}
