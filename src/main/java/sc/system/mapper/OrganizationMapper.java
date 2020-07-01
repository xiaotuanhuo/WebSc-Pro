package sc.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import sc.common.annotation.DataAuth;
import sc.system.model.WebScOrganization;

@Mapper
public interface OrganizationMapper {

	@Select("SELECT * FROM WSC_ORGANIZATION WHERE org_name=#{orgName}")
	WebScOrganization selectWebScOrganization(@Param("orgName") String orgName);
	
	List<WebScOrganization> getList(WebScOrganization wso);
	
	/**
	  * 获取所有医疗机构节点
	  * @return
	  */
	List<WebScOrganization> selectAll();
	
	@DataAuth(authCode = "getOrgForAddEidtUser")
    List<WebScOrganization> selectOrgForAddEidtUser();
	
	int insert(WebScOrganization wso);
	
	int updateByPrimaryKey(WebScOrganization wso);
	
	int deleteByPrimaryKey(String id);
}
