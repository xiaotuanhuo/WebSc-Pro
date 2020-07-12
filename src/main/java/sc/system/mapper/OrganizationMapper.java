package sc.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import sc.system.model.WebScOrganization;

@Mapper
public interface OrganizationMapper {
	
	@Select("SELECT org_name FROM WSC_ORGANIZATION WHERE area LIKE CONCAT(#{cityPre},'%')")
	List<String> selectOrgsByQy(@Param("cityPre") String cityPre);

	@Select("SELECT * FROM WSC_ORGANIZATION WHERE org_name=#{orgName}")
	WebScOrganization selectWebScOrganization(@Param("orgName") String orgName);
	
	WebScOrganization selectByPrimaryKey(@Param("orgId") String orgId);
	
	/**
	 * 根据父节点及行政区划查询
	 * @param parentId
	 * @param privince
	 * @param city
	 * @return
	 */
	WebScOrganization selectByParentIdAndDistrict(@Param("org_pid") String parentId, @Param("province_code") String privince,
			@Param("city_code") String city, @Param("area_code") String area);
	
	List<WebScOrganization> getList(WebScOrganization wso);
	
	List<WebScOrganization> getOrganizations();
	
	/**
	 * 查询所有医疗结构根节点
	 * @return
	 */
	List<WebScOrganization> selectRoot();
	
	/**
	  * 获取所有医疗机构节点
	  * @return
	  */
	List<WebScOrganization> selectAll();
	
	/**
	 * 查询当前节点及其子节点（树）
	 * @param orgId
	 * @param privince
	 * @param city
	 * @return
	 */
	List<WebScOrganization> selectAllTree(@Param("org_id") String orgId, @Param("province_code") String privince,
			@Param("city_code") String city, @Param("area_code") String area);
	
	/**
	 * 根据当前用户的角色及区划和前端查询条件查询医疗机构列表
	 * @param rootId	// 医疗机构管理员所属医疗机构的根节点
	 * @param isBureau	// 是否是卫监局管理员角色
	 * @param privince	// 当前用户所属省
	 * @param city		// 当前用户所属市
	 * @param area		// 当前用户所属区/县
	 * @param wso		// 前端查询条件
	 * @return
	 */
	List<WebScOrganization> selectListByAuthData(@Param("root_id") String rootId, @Param("bureau") Boolean isBureau, @Param("user_province_code") String privince,
			@Param("user_city_code") String city, @Param("user_area_code") String area, @Param("wso") WebScOrganization wso);
	
	int insert(WebScOrganization wso);
	
	int updateByPrimaryKey(WebScOrganization wso);
	
	int deleteByPrimaryKey(String id);
}
