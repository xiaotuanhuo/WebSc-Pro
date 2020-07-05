package sc.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import sc.common.annotation.DataAuth;
import sc.system.model.WebScDept;
import sc.system.model.WebScOrganization;

@Mapper
public interface OrganizationMapper {

	@Select("SELECT * FROM WSC_ORGANIZATION WHERE org_name=#{orgName}")
	WebScOrganization selectWebScOrganization(@Param("orgName") String orgName);
	
	WebScOrganization selectByPrimaryKey(@Param("orgId") String orgId);
	
	List<WebScOrganization> getList(WebScOrganization wso);
	
	/**
	  * 获取所有医疗机构节点
	  * @return
	  */
	List<WebScOrganization> selectAll();
	
	/**
	 * 查询当前节点及其子节点
	 * @param orgId
	 * @param privince
	 * @param city
	 * @return
	 */
	List<WebScOrganization> selectAllTree(@Param("org_id") String orgId, @Param("province_code") String privince,
			@Param("city_code") String city);
	
	int insert(WebScOrganization wso);
	
	int updateByPrimaryKey(WebScOrganization wso);
	
	int deleteByPrimaryKey(String id);
}
