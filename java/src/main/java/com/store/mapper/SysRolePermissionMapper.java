package com.store.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.store.entity.SysRolePermissionEntity;

@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermissionEntity> {
  @Select("SELECT permission_code FROM sys_role_permission WHERE role_id = #{roleId}")
  List<String> selectPermissionsByRoleId(Long roleId);
}
