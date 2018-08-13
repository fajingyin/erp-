package cn.itcast.erp.biz.impl;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.erp.biz.IBaseBiz;
import cn.itcast.erp.dao.IBaseDao;
import cn.itcast.erp.exception.ErpException;
/**
 * 通用业务逻辑实现类
 *
 * @param <T>
 */
public abstract class BaseBiz<T> implements IBaseBiz<T> {

    private IBaseDao<T> baseDao;

    public void setBaseDao(IBaseDao<T> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public List<T> getList(T t1, T t2, Object obj) {
        return baseDao.getList(t1,t2,obj);
    }

    @Override
    public Long getCount(T t1, T t2, Object obj) {
        return baseDao.getCount(t1, t2, obj);
    }

    @Override
    public List<T> getListByPage(T t1, T t2, Object obj, int startRow, int maxResults) {
        return baseDao.getListByPage(t1, t2, obj, startRow, maxResults);
    }

    @Override
    @Transactional
    public void add(T t) {
        baseDao.add(t);
    }

    @Override
    @Transactional
    public void delete(Long uuid) {
        baseDao.delete(uuid);
    }

    @Override
    public T get(Serializable uuid) {
        return baseDao.get(uuid);
    }

    @Override
    @Transactional
    public void update(T t) {
        baseDao.update(t);
    }
    
	public void isString(Cell cell ) {
		if(cell.getCellType()!=HSSFCell.CELL_TYPE_STRING) {
			throw new ErpException("价格类型必须为String类型");
		}
	}
    
    
}
