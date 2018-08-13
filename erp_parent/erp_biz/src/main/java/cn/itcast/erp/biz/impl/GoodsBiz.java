package cn.itcast.erp.biz.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.struts2.components.If;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.erp.biz.IGoodsBiz;
import cn.itcast.erp.dao.IGoodsDao;
import cn.itcast.erp.dao.IGoodstypeDao;
import cn.itcast.erp.entity.Goods;
import cn.itcast.erp.entity.Goodstype;
import cn.itcast.erp.exception.ErpException;

/**
 * 商品业务逻辑类
 *
 */
@Service("goodsBiz")
@SuppressWarnings("unused")
public class GoodsBiz extends BaseBiz<Goods> implements IGoodsBiz {

    private IGoodsDao goodsDao;
    @Autowired
    private IGoodstypeDao goodstypeDao;

    @Resource(name="goodsDao")
    public void setGoodsDao(IGoodsDao goodsDao) {
        this.goodsDao = goodsDao;
        super.setBaseDao(this.goodsDao);
    }

	/* 数据导出
	 * 
	 */
	@Override
	@Transactional
	public void export(OutputStream os, Goods t1) throws IOException {
		//根据查询条件获取供应商
		List<Goods> list = super.getList(t1, null, null);
		//创建工作簿
		HSSFWorkbook wk = new HSSFWorkbook();
		HSSFSheet sheet = wk.createSheet("商品");
		// 写入表头
		HSSFRow row = sheet.createRow(0);
		// 定义好每一列的标题
		String[] headerNames = {"名称","产地","厂家","计量单位","进货价格","销售价格","商品类型"};
		// 指定每一列的宽度
		int[] columnWidths = {3000,3000,6000,2000,3000,3000,3000};
		HSSFCell cell = null;
		for(int i = 0; i < headerNames.length; i++){
	        cell = row.createCell(i);
	        cell.setCellValue(headerNames[i]);
	        // 设置每列的宽度
	        sheet.setColumnWidth(i, columnWidths[i]);
	    }
		// 写入内容
		int i = 1;
		for (Goods goods : list) {
			row = sheet.createRow(i);
			 //必须按照hderarNames的顺序来
			row.createCell(0).setCellValue(goods.getName());
			row.createCell(1).setCellValue(goods.getOrigin());
			row.createCell(2).setCellValue(goods.getProducer());
			row.createCell(3).setCellValue(goods.getUnit());
			row.createCell(4).setCellValue(goods.getInprice()+"");
			row.createCell(5).setCellValue(goods.getOutprice()+"");
			row.createCell(6).setCellValue(goods.getGoodstype().getName());
			i++;
		}
		try {
	    	// 写入到输出流中
	        wk.write(os);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }finally{
	        try {
	        	// 关闭工作簿
	            wk.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}

	/* 
	 * 数据导入
	 */
	@Override
	@Transactional
	public void doImport(InputStream is) throws IOException {
		HSSFWorkbook wb = null;
		try {
			wb= new HSSFWorkbook(is);
			HSSFSheet sheet = wb.getSheetAt(0);
			if(!"商品".equals(sheet.getSheetName())) {
				throw new ErpException("工作表名称不正确！！");
			}else{
				//读取数据
				//获取最后一行的行号
				int lastRow = sheet.getLastRowNum();
				Goods goods = null;
				for (int i = 1; i <= lastRow; i++) {
					goods = new Goods();
					goods.setName(sheet.getRow(i).getCell(0).getStringCellValue());
					List<Goods> list = goodsDao.getList(null, goods, null);
					if(list.size()>0) {
						goods = list.get(0);
					}
					goods.setOrigin(sheet.getRow(i).getCell(1).getStringCellValue());
					goods.setProducer(sheet.getRow(i).getCell(2).getStringCellValue());
					goods.setUnit(sheet.getRow(i).getCell(3).getStringCellValue());
					isString(sheet.getRow(i).getCell(4));
					isString(sheet.getRow(i).getCell(5));
					goods.setInprice(Double.valueOf(sheet.getRow(i).getCell(4).getStringCellValue()));
					goods.setOutprice(Double.valueOf(sheet.getRow(i).getCell(5).getStringCellValue()));
					Goodstype goodstype = getGoodstype(sheet.getRow(i).getCell(6).getStringCellValue());
					if (null == goodstype) {
						throw new ErpException("该商品类型不存在!!!");
					}
					goods.setGoodstype(goodstype);
					if(list.size() == 0) {
						goodsDao.add(goods);
					}
			}
		}
			}finally {
				if(null != wb) {
					try {
						wb.close();
					} catch (Exception e) {
					e.printStackTrace();
					}
				}
			}
	}
	
	/**
	 * 通过商品类型的名称 查找出该对象
	 * @param name
	 * @return
	 */
	private  Goodstype getGoodstype(String name) {
		Goodstype goodstype = new Goodstype();
		goodstype.setName(name);
		List<Goodstype> list = goodstypeDao.getList(goodstype, null, null);
		goodstype = list.size() > 0 ? list.get(0) : null;
		return goodstype;
	}
	
		
	
	
}
