package com.rainingsince.admin.permission.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rainingsince.admin.permission.entity.Permission;
import com.rainingsince.admin.permission.error.PermissionError;
import com.rainingsince.admin.permission.mapper.PermisssionMapper;
import com.rainingsince.admin.rolePermission.entity.RolePermission;
import com.rainingsince.admin.rolePermission.service.RolePermissionService;
import com.rainingsince.web.response.ResponseBuilder;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;

@Service
@Transactional
@AllArgsConstructor
public class PermissionService extends ServiceImpl<PermisssionMapper, Permission> {

    private RolePermissionService rolePermissionService;

    @Override
    public boolean save(Permission entity) {
        entity.setId(IdWorker.getIdStr());
        return super.save(entity);
    }

    public ResponseEntity saveNotExit(Permission entity) {
        if (isExit(entity)) return ResponseBuilder.error(PermissionError.PERMISSION_EXIT);
        return ResponseBuilder.success(save(entity));
    }

    public ResponseEntity updateNotExit(Permission entity) {
        if (isExit(entity)) return ResponseBuilder.error(PermissionError.PERMISSION_EXIT);
        return ResponseBuilder.success(updateById(entity));
    }


    public IPage<Permission> pages(Permission entity) {
        return super.baseMapper.selectPage(entity.toPage(),
                new QueryWrapper<>(entity).orderByDesc("create_date"));
    }

    public boolean isExit(Permission entity) {
        Permission one = super.getOne(new QueryWrapper<Permission>()
                .eq("code", entity.getCode()));
        return one != null && !one.getId().equals(entity.getId());
    }

    @Override
    public boolean removeById(Serializable id) {
        rolePermissionService.remove(new QueryWrapper<RolePermission>()
                .eq("permission_id", id));
        return super.removeById(id);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        rolePermissionService.remove(new QueryWrapper<RolePermission>()
                .in("permission_id", idList));
        return super.removeByIds(idList);
    }
}
