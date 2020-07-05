package sc.system.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sc.common.constants.RoleEnum;
import sc.system.mapper.OrganizationMapper;
import sc.system.model.WebScOrganization;
import sc.system.model.WebScUser;
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
	
	public List<WebScOrganization> selectTree() {
		List<WebScOrganization> organizations = new ArrayList<WebScOrganization>();
		Subject subject = SecurityUtils.getSubject();
		WebScUser user = (WebScUser) subject.getPrincipal();
		switch (RoleEnum.valueOf(Integer.parseInt(user.getRoleId()))) {
			case CJGLY:
			case QYGLY:
				organizations = organizationMapper.selectAllTree(null, user.getProvince(), user.getCity());
				break;
			case YLJGGLY:
				organizations = organizationMapper.selectAllTree(user.getRoleTypeId(), user.getProvince(), user.getCity());
				break;
			default:
				break;
		}
		return organizations;
	}
	
	public int insert(WebScOrganization wso) {
		return organizationMapper.insert(wso);
	}
	
	public int updateByPrimaryKey(WebScOrganization wso) {
		return organizationMapper.updateByPrimaryKey(wso);
	}
}
