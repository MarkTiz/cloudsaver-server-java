package com.cjs.cloudsaver.mapper.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjs.cloudsaver.model.common.BaseEntity;

import java.util.List;

public interface BaseModelMapper<T extends BaseEntity> extends BaseMapper<T> {

    /**
     * 根据code统计
     *
     * @return 查询到的实体
     */
    default boolean existsById(Long id) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        Long count = this.selectCount(queryWrapper);
        return null != count && count > 0;
    }

    /**
     * 根据code查询实体
     *
     * @return 查询到的实体
     */
    default T findByCode(String id) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        return this.selectOne(queryWrapper);
    }

    /**
     * 根据code更新实体
     *
     * @param entity 要更新的实体
     * @return 受影响的行数
     */
    default int updateByCode(T entity) {
        UpdateWrapper<T> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", entity.getId());
        return this.update(entity, updateWrapper);
    }

    /**
     * 根据code删除实体（硬删除）
     *
     * @return 受影响的行数
     */
    default int hardDeleteByCode(T entity) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", entity.getId());
        return this.delete(queryWrapper);
    }

    default int hardDeleteById(String id) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        return this.delete(queryWrapper);
    }

    /**
     * 根据一组实体批量删除实体（硬删除）
     *
     * @param entities 需要删除的实体列表
     * @return 受影响的总行数
     */
    default int batchHardDeleteByCode(List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return 0;
        }

        List<Long> codes = entities.stream()
                .map(T::getId)
                .toList();
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", codes);
        return this.delete(queryWrapper);
    }

}
