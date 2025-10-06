package com.sf.zhimengjing.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.zhimengjing.common.model.vo.OptionVO;
import com.sf.zhimengjing.entity.admin.AdminRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Title: AdminRoleMapper
 * @Author 殇枫
 * @Package com.sf.zhimengjing.mapper
 * @description: 管理员角色数据访问层接口
 */
@Mapper
public interface AdminRoleMapper extends BaseMapper<AdminRole> {

    @Select("SELECT role_code AS value, role_name AS label FROM admin_roles WHERE is_system = 0")
    List<OptionVO> getRoleCodeOptions();
}
