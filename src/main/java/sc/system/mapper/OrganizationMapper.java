package sc.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import sc.system.model.WebScOrganization;

@Mapper
public interface OrganizationMapper {
	
	@Select("SELECT * FROM WSC_ORGANIZATION WHERE org_name=#{orgName}")
	WebScOrganization selectWebScOrganization(@Param("orgName") String orgName);
	
	List<WebScOrganization> getList(WebScOrganization wso);
	 
	int insert(WebScOrganization wso);
	 
	int updateByPrimaryKey(WebScOrganization wso);
	 
	int deleteByPrimaryKey(String id);
}
