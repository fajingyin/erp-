package cn.itcast.erp.dao;

import java.io.Serializable;
import java.util.List;

public interface IBaseDao<T> {

    /**
     * 获取所有部门信息
     * @return
     */
    List<T> getList(T t1, T t2, Object obj);

    /**
     * 计算符合条件的总记录数
     * @param t1
     * @param t2
     * @param obj
     * @return
     */
    Long getCount(T t1, T t2, Object obj);

    /**
     * 分页查询
     * @param t1
     * @param t2
     * @param obj
     * @param startRow
     * @param maxResults
     * @return
     */
    List<T> getListByPage(T t1, T t2, Object obj, int startRow, int maxResults);

    /**
     * 新增
     * @param t
     */
    void add(T t);

    /**
     * 删除
     * @param uuid
     */
    void delete(Long uuid);

    /**
     * 数据回显
     * @param uuid
     * @return
     */
    T get(Serializable uuid);

    /**
     * 更新
     * @param t
     */
    void update(T t);
}
