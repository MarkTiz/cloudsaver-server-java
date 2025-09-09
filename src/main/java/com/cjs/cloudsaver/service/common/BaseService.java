package com.cjs.cloudsaver.service.common;


import com.baomidou.mybatisplus.extension.service.IService;
import com.cjs.cloudsaver.model.common.BaseEntity;

import java.util.List;


public interface BaseService<T extends BaseEntity> extends IService<T> {
    /**
     * 创建
     *
     * @param entity 实体
     * @return 实体
     */
    T create(T entity);

    /**
     * 更新
     *
     * @param entity 实体
     * @return 实体
     */
    T update(T entity);

    /**
     * 软删除
     *
     * @param entity 实体
     * @return 实体
     */
    int delete(T entity);


    /**
     * id
     *
     * @param id 编码
     */
    boolean existsById(Long id);

    /**
     * 根据id查询数据
     *
     * @param id 编码
     */
    T findById(Long id);


    /**
     * 据code 更新数据
     *
     * @param id
     * @return
     */
    boolean updateById(T id);

    /**
     * 批量创建
     *
     * @param entities 实体列表
     * @param operator 操作人
     * @return 插入后的实体列表
     */
    int createBatch(List<T> entities, String operator);


    /**
     * 生成序列号
     */
    String generatorSequence();


}
