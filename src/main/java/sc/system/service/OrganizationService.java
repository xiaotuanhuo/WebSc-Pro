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
import org.springframework.transaction.annotation.Transactional;

import sc.common.constants.RoleEnum;
import sc.common.util.UUID19;
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
	
	public List<WebScOrganization> getRoot() {
		return organizationMapper.selectRoot();
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
		return addTree(wso);
	}
	
	public int updateByPrimaryKey(WebScOrganization wso) {
		return organizationMapper.updateByPrimaryKey(wso);
	}
	
	/**
	 * 自上而下添加节点
	 * 非叶子节点id命名规则：医疗机构根节点 + 当前行政区划code
	 * 非叶子节点名称规则：当前行政区划名称_医疗机构根节点名称
	 * @param wso
	 */
	@Transactional
	private int addTree(WebScOrganization wso) {
		String parentId = wso.getRootId();
		String province = wso.getProvince();
		String city = wso.getCity();
		String area = wso.getArea();
		WebScOrganization districtWso = new WebScOrganization();	// 逐级查询时的节点
		WebScOrganization provinceWso = new WebScOrganization();	// 省级节点
		WebScOrganization cityWso = new WebScOrganization();		// 市级节点
		WebScOrganization areaWso = new WebScOrganization();		// 区/县级节点
		WebScOrganization leafWso = new WebScOrganization();		// 叶子节点
		// 1、查询省节点是否已被添加，如果未被添加，则从省至叶子节点逐级添加
		//	    如果已添加，则将其id作为父id查询市节点是否被添加
		districtWso = organizationMapper.selectByParentIdAndDistrict(parentId, province, null, null);
		if (districtWso == null) {
			provinceWso.setOrgId(wso.getRootId() + province);
			provinceWso.setOrgPid(parentId);
			provinceWso.setOrgName(district.getDistrictMap().get(province).getName() + "_" + wso.getOrgName());
			provinceWso.setProvince(province);
			provinceWso.setLeaf(0);
			
			cityWso.setOrgId(wso.getRootId() + city);
			cityWso.setOrgPid(provinceWso.getOrgId());
			cityWso.setOrgName(district.getDistrictMap().get(city).getName() + "_" + wso.getOrgName());
			cityWso.setProvince(province);
			cityWso.setCity(city);
			cityWso.setLeaf(0);
			
			areaWso.setOrgId(wso.getRootId() + area);
			areaWso.setOrgPid(cityWso.getOrgId());
			areaWso.setOrgName(district.getDistrictMap().get(area).getName() + "_" + wso.getOrgName());
			areaWso.setProvince(province);
			areaWso.setCity(city);
			areaWso.setArea(area);
			areaWso.setLeaf(0);
			
			leafWso.setOrgId(UUID19.uuid());
			leafWso.setOrgPid(areaWso.getOrgId());
			leafWso.setRootId(wso.getRootId());
			leafWso.setOrgName(wso.getOrgName());
			leafWso.setProvince(province);
			leafWso.setCity(city);
			leafWso.setArea(area);
			leafWso.setLeaf(1);
			leafWso.setCredentials(wso.getCredentials());
			
			organizationMapper.insert(provinceWso);
			organizationMapper.insert(cityWso);
			organizationMapper.insert(areaWso);
			organizationMapper.insert(leafWso);
		} else {
			// 2、查询市节点是否已被添加，如果未被添加，则从市至叶子节点逐级添加
			//	    如果已添加，则将其id作为父id查询市节点是否被添加
			parentId = districtWso.getOrgId();
			districtWso = organizationMapper.selectByParentIdAndDistrict(parentId, province, city, null);
			if (districtWso == null) {
				cityWso.setOrgId(wso.getRootId() + city);
				cityWso.setOrgPid(parentId);
				cityWso.setOrgName(district.getDistrictMap().get(city).getName() + "_" + wso.getOrgName());
				cityWso.setProvince(province);
				cityWso.setCity(city);
				cityWso.setLeaf(0);
				
				areaWso.setOrgId(wso.getRootId() + area);
				areaWso.setOrgPid(cityWso.getOrgId());
				areaWso.setOrgName(district.getDistrictMap().get(area).getName() + "_" + wso.getOrgName());
				areaWso.setProvince(province);
				areaWso.setCity(city);
				areaWso.setArea(area);
				areaWso.setLeaf(0);
				
				leafWso.setOrgId(UUID19.uuid());
				leafWso.setOrgPid(areaWso.getOrgId());
				leafWso.setRootId(wso.getRootId());
				leafWso.setOrgName(wso.getOrgName());
				leafWso.setProvince(province);
				leafWso.setCity(city);
				leafWso.setArea(area);
				leafWso.setLeaf(1);
				leafWso.setCredentials(wso.getCredentials());
				
				organizationMapper.insert(cityWso);
				organizationMapper.insert(areaWso);
				organizationMapper.insert(leafWso);
			} else {
				// 3、查询区/县节点是否已被添加，如果未被添加，则从区/县至叶子节点逐级添加
				//	    如果已添加，则将其id作为父id添加叶子节点
				parentId = districtWso.getOrgId();
				districtWso = organizationMapper.selectByParentIdAndDistrict(parentId, province, city, area);
				if (districtWso == null) {
					areaWso.setOrgId(wso.getRootId() + area);
					areaWso.setOrgPid(parentId);
					areaWso.setOrgName(district.getDistrictMap().get(area).getName() + "_" + wso.getOrgName());
					areaWso.setProvince(province);
					areaWso.setCity(city);
					areaWso.setArea(area);
					areaWso.setLeaf(0);
					
					leafWso.setOrgId(UUID19.uuid());
					leafWso.setOrgPid(areaWso.getOrgId());
					leafWso.setRootId(wso.getRootId());
					leafWso.setOrgName(wso.getOrgName());
					leafWso.setProvince(province);
					leafWso.setCity(city);
					leafWso.setArea(area);
					leafWso.setLeaf(1);
					leafWso.setCredentials(wso.getCredentials());
					
					organizationMapper.insert(areaWso);
					organizationMapper.insert(leafWso);
				} else {
					leafWso.setOrgId(UUID19.uuid());
					leafWso.setOrgPid(districtWso.getOrgId());
					leafWso.setRootId(wso.getRootId());
					leafWso.setOrgName(wso.getOrgName());
					leafWso.setProvince(province);
					leafWso.setCity(city);
					leafWso.setArea(area);
					leafWso.setLeaf(1);
					leafWso.setCredentials(wso.getCredentials());
					
					organizationMapper.insert(leafWso);
				}
			}
		}
		return 1;
	}
}
