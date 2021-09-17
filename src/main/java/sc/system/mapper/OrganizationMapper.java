package sc.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import sc.system.model.WebScOrganization;
import sc.system.model.WebScUser;

@Mapper
public interface OrganizationMapper {
	
	/**
	 * 修改机构的平均评分
	 * @param orgId
	 * @param orgEvaluate
	 * @return
	 */
	@Update("UPDATE WSC_ORGANIZATION SET avg_evaluate=${orgEvaluate} WHERE org_id=#{orgId}")
	int updOrgAvgEvaluate(@Param("orgId") String orgId, @Param("orgEvaluate") double orgEvaluate);
	
	@Select("SELECT org_name FROM WSC_ORGANIZATION WHERE leaf = 1 AND area LIKE CONCAT(#{cityPre},'%')")
	List<String> selectOrgsByQy(@Param("cityPre") String cityPre);

	@Select("SELECT * FROM WSC_ORGANIZATION WHERE leaf = 1 AND org_name=#{orgName}")
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
	 * 获取医疗结构根节点id
	 * @param orgId
	 * @return
	 */
	String selectRootById(@Param("org_id") String orgId);
	
	/**
	  * 获取所有医疗机构节点
	  * @return
	  */
//	List<WebScOrganization> selectAll();
	
	/**
	 * 获取所有医疗机构节点
	 * @param isBureau	是否是卫监局角色
	 * @param orgPid	父节点
	 * @param privince	用户省
	 * @param city		用户市
	 * @param area		用户区/县
	 * @param wso		前端查询条件
	 * @return
	 */
//	List<WebScOrganization> selectAll(@Param("bureau") boolean isBureau, @Param("orgPid") String orgPid, @Param("provinceCode") String privince,
//			@Param("cityCode") String city, @Param("areaCode") String area, @Param("wso") WebScOrganization wso);
	
	/**
	 * 获取所有医疗机构叶子节点
	 * @param isBureau	是否是卫监局角色
	 * @param isOrgRole	是否是医疗机构管理员角色
	 * @param rootId	医疗机构根节点（医疗机构管理员）
	 * @param user		当前用户
	 * @param wso		前端查询条件
	 * @return
	 */
	List<WebScOrganization> selectAll(@Param("bureau") boolean isBureau, @Param("orgRole") boolean isOrgRole, @Param("root_id") String rootId, @Param("user") WebScUser user, @Param("wso") WebScOrganization wso);
	
	/**
	 * 查询当前节点及其子节点（树）
	 * @param orgId
	 * @param privince
	 * @param city
	 * @param area
	 * @return
	 */
	List<WebScOrganization> selectAllTree(@Param("org_id") String orgId, @Param("province_code") String privince,
			@Param("city_code") String city, @Param("area_code") String area);
	
	/**
	 * 查询当前用户下的所有医疗结构叶子节点
	 * @param orgId
	 * @param privince
	 * @param city
	 * @param area
	 * @return
	 */
	List<WebScOrganization> selectAllLeaf(@Param("org_id") String orgId, @Param("province_code") String privince,
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
	List<WebScOrganization> selectListByAuthData2(@Param("root_id") String rootId, @Param("bureau") Boolean isBureau, @Param("user_province_code") String privince,
			@Param("user_city_code") String city, @Param("user_area_code") String area, @Param("wso") WebScOrganization wso);
	
	List<WebScOrganization> selectListByAuthData(@Param("root_id") String rootId, @Param("bureau") Boolean isBureau, @Param("user_province_code") String privince,
			@Param("user_city_code") String city, @Param("user_area_code") String area, @Param("wso") WebScOrganization wso);
	
	int insert(WebScOrganization wso);
	
	int updateByPrimaryKey(WebScOrganization wso);
	
	int deleteByPrimaryKey(String id);
	
	/**
	 * 统计已经有几个此机构名称, 检测是否重复.
	 * @param orgId 非null时表示编辑，统计机构名称数量时不包含该项
	 * @param name
	 * @return
	 */
    int countByName(@Param("org_id") String orgId, @Param("org_name") String name);
}
