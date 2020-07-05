package sc.system.mapper;

import sc.common.annotation.DataAuth;
import sc.system.model.WebScRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {
	int deleteByPrimaryKey(Integer roleId);

	int insert(WebScRole role);

	WebScRole selectByPrimaryKey(Integer roleId);

	int updateByPrimaryKey(WebScRole role);

	List<WebScRole> selectAll();

	List<WebScRole> selectAllByQuery(WebScRole roleQuery);
	
	@DataAuth(authCode = "getRolesForAddEidtUser")
    List<WebScRole> selectRolesForAddEidtUser();
	
	/**
	 * 根据当前用户角色返回可操作角色列表
	 * @return
	 */
	List<WebScRole> selectRolesByUser(@Param("list") List<Integer> roleIds);

	int count();
}