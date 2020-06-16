package sc.system.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import sc.system.mapper.OrganizationMapper;
import sc.system.model.WebScOrganization;

@Service
public class OrganizationService {
	@Resource
	private OrganizationMapper organizationMapper;
	 
	public List<WebScOrganization> getList(WebScOrganization wso){
		return organizationMapper.getList(wso);
	}
	
	public int insert(WebScOrganization wso){
		return organizationMapper.insert(wso);
	}
	
	public int updateByPrimaryKey(WebScOrganization wso){
		return organizationMapper.updateByPrimaryKey(wso);
	}
}
