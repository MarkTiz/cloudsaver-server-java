package com.cjs.cloudsaver.service.common.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjs.cloudsaver.mapper.common.BaseModelMapper;
import com.cjs.cloudsaver.model.common.BaseEntity;
import com.cjs.cloudsaver.service.common.BaseService;

import java.util.List;


/**
 * 基础实现类，用于额外方法的扩展
 */
public class BaseServiceImpl<M extends BaseModelMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T> implements BaseService<T> {


    /**
     * 创建
     *
     * @param entity 实体
     */
    @Override
    public T create(T entity) {
        this.getBaseMapper().insert(entity);
        return entity;
    }

    /**
     * 更新
     *
     * @param entity 实体
     */
    @Override
    public T update(T entity) {
        this.getBaseMapper().updateByCode(entity);
        return entity;

    }

    /**
     * 删除
     *
     * @param entity 实体
     */
    @Override
    public int delete(T entity) {
        return this.getBaseMapper().deleteById(entity);
    }


    /**
     * 根据code判断是否存在
     *
     * @param id 编码
     */
    @Override
    public boolean existsById(Long id) {
        return this.getBaseMapper().existsById(id);
    }

    /**
     * 根据code查询数据
     *
     * @param id 编码
     * @return
     */
    @Override
    public T findById(Long id) {
        return this.getBaseMapper().selectById(id);
    }


    /**
     * 据code 更新数据
     *
     * @param entity
     * @return
     */
    @Override
    public boolean updateById(T entity) {
        return this.getBaseMapper().updateById(entity) > 0;
    }

    /**
     * 批量创建
     *
     * @param entities 实体列表
     * @param operator 操作人
     * @return 插入后的实体列表
     */
    @Override
    public int createBatch(List<T> entities, String operator) {
        int size = 0;
        for (T entity : entities) {
            size += this.getBaseMapper().insert(entity);
        }
        return size;
    }


    /**
     * 序列生成
     *
     * @return 序列
     */
    @Override
    public String generatorSequence() {
        return String.valueOf(IdWorker.getId());
    }
}
