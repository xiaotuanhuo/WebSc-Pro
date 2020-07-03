package sc.system.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sc.system.mapper.OrganizationMapper;
import sc.system.model.WebScOrganization;
import sc.system.model.vo.District;

@Service
public class OrganizationService {
	private static final Logger log = LoggerFactory.getLogger(OrganizationService.class);
	
	@Resource
	private OrganizationMapper organizationMapper;
	
	@Autowired
	private District district;
	
	public List<WebScOrganization> getList(WebScOrganization wso) {
		List<WebScOrganization> wsoList = organizationMapper.getList(wso);
		if (wsoList.size() != 0) {
			for (WebScOrganization webScOrganization : wsoList) {
				webScOrganization.setProvinceName(district.getDistrictMap().get(webScOrganization.getProvince()).getName());
				webScOrganization.setCityName(district.getDistrictMap().get(webScOrganization.getCity()).getName());
				webScOrganization.setAreaName(district.getDistrictMap().get(webScOrganization.getArea()).getName());
			}
		}
		return wsoList;
	}
	
	public List<WebScOrganization> getOrgForAddEidtUser() {
        return organizationMapper.selectOrgForAddEidtUser();
    }
	
	public int insert(WebScOrganization wso) {
		return organizationMapper.insert(wso);
	}
	
	public int updateByPrimaryKey(WebScOrganization wso) {
		return organizationMapper.updateByPrimaryKey(wso);
	}
	
	public List<WebScOrganization> selectAllOrgTree() {
		return organizationMapper.selectAllTree();
	}
}
