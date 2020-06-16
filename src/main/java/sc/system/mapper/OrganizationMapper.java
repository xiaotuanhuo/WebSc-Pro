package sc.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import sc.system.model.WebScOrganization;

@Mapper
public interface OrganizationMapper {
	 List<WebScOrganization> getList(WebScOrganization wso);
	 
	 int insert(WebScOrganization wso);
	 
	 int updateByPrimaryKey(WebScOrganization wso);
	 
	 int deleteByPrimaryKey(String id);
}
