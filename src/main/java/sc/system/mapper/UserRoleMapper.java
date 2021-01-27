package sc.system.mapper;

import sc.system.model.WebScUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleMapper {

    int insert(WebScUserRole userRole);

    /**
     * 插入多条 用户色-角色 关联关系
     */
    int insertList(@Param("userId") String userId, @Param("roleIds") Integer[] roleIds);

    /**
     * 清空用户所拥有的所有角色
     */
    int deleteUserRoleByUserId(@Param("userId") String userId);

    /**
     * 清空此角色与所有角色的关联关系
     */
    int deleteUserRoleByRoleId(@Param("roleId") Integer roleId);


    List<Integer> selectUserIdByRoleId(@Param("roleId") Integer roleId);

}