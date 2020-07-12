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
		Subject subject = SecurityUtils.getSubject();
		WebScUser user = (WebScUser) subject.getPrincipal();
		List<WebScOrganization> wsoList = new ArrayList<>();
		switch (RoleEnum.valueOf(Integer.parseInt(user.getRoleId()))) {
			case XTGLY:
			case CJGLY:
			case QYGLY:
				wsoList = organizationMapper.selectListByAuthData(null, false, user.getProvince(), user.getCity(), user.getArea(), wso);
				break;
			case WJJGLY:
				wsoList = organizationMapper.selectListByAuthData(null, true, user.getProvince(), user.getCity(), user.getArea(), wso);
				break;
			case YLJGGLY:
				// 查询当前用户的医疗机构根节点
				WebScOrganization userOrganization = organizationMapper.selectByPrimaryKey(user.getRoleTypeId());
				String rootId = userOrganization.getRootId();
				wsoList = organizationMapper.selectListByAuthData(rootId, false, user.getProvince(), user.getCity(), user.getArea(), wso);
				break;
			default:
				break;
		}
		
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
			case XTGLY:
			case CJGLY:
			case QYGLY:
				organizations = organizationMapper.selectAllTree(null, user.getProvince(), user.getCity(), user.getArea());
				break;
			case WJJGLY:
				organizations = organizationMapper.selectAllTree(null, user.getProvince(), user.getCity(), user.getArea());
				organizations = getTreeOfCredentials(organizations);
				break;
			case YLJGGLY:
				organizations = organizationMapper.selectAllTree(user.getRoleTypeId(), user.getProvince(), user.getCity(), user.getArea());
				break;
			default:
				break;
		}
		return organizations;
	}
	
	/**
	 * 删除没有麻醉资质的医疗机构
	 * @param orgs
	 * @return
	 */
	private static List<WebScOrganization> getTreeOfCredentials(List<WebScOrganization> orgs) {
		for (int i = 0; i < orgs.size(); i++) {
			if (orgs.get(i).getChildren().size() > 0) {
				// 递归调用
				getTreeOfCredentials(orgs.get(i).getChildren());
			}
			// 无麻醉资质的叶子节点移除
			// 当前节点的子节点全部移除时，移除该节点
			if ((orgs.get(i).getLeaf() == 1 && orgs.get(i).getCredentials().equals("0"))
					|| (orgs.get(i).getLeaf() == 0 && orgs.get(i).getChildren().size() == 0)) {
				orgs.remove(i);
				i--;	// 节点移除后
			}
		}
		return orgs;
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
			provinceWso.setRootId(wso.getRootId());
			provinceWso.setOrgName(district.getDistrictMap().get(province).getName() + "_" + wso.getRootName());
			provinceWso.setProvince(province);
			provinceWso.setLeaf(0);
			
			cityWso.setOrgId(wso.getRootId() + city);
			cityWso.setOrgPid(provinceWso.getOrgId());
			cityWso.setRootId(wso.getRootId());
			cityWso.setOrgName(district.getDistrictMap().get(city).getName() + "_" + wso.getRootName());
			cityWso.setProvince(province);
			cityWso.setCity(city);
			cityWso.setLeaf(0);
			
			areaWso.setOrgId(wso.getRootId() + area);
			areaWso.setOrgPid(cityWso.getOrgId());
			areaWso.setRootId(wso.getRootId());
			areaWso.setOrgName(district.getDistrictMap().get(area).getName() + "_" + wso.getRootName());
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
			leafWso.setOrgAddress(wso.getOrgAddress());
			leafWso.setOrgTel(wso.getOrgTel());
			leafWso.setLeaderName(wso.getLeaderName());
			leafWso.setLeaderTel(wso.getLeaderTel());
			
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
				cityWso.setRootId(wso.getRootId());
				cityWso.setOrgName(district.getDistrictMap().get(city).getName() + "_" + wso.getRootName());
				cityWso.setProvince(province);
				cityWso.setCity(city);
				cityWso.setLeaf(0);
				
				areaWso.setOrgId(wso.getRootId() + area);
				areaWso.setOrgPid(cityWso.getOrgId());
				areaWso.setRootId(wso.getRootId());
				areaWso.setOrgName(district.getDistrictMap().get(area).getName() + "_" + wso.getRootName());
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
				leafWso.setOrgAddress(wso.getOrgAddress());
				leafWso.setOrgTel(wso.getOrgTel());
				leafWso.setLeaderName(wso.getLeaderName());
				leafWso.setLeaderTel(wso.getLeaderTel());
				
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
					areaWso.setRootId(wso.getRootId());
					areaWso.setOrgName(district.getDistrictMap().get(area).getName() + "_" + wso.getRootName());
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
					leafWso.setOrgAddress(wso.getOrgAddress());
					leafWso.setOrgTel(wso.getOrgTel());
					leafWso.setLeaderName(wso.getLeaderName());
					leafWso.setLeaderTel(wso.getLeaderTel());
					
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
					leafWso.setOrgAddress(wso.getOrgAddress());
					leafWso.setOrgTel(wso.getOrgTel());
					leafWso.setLeaderName(wso.getLeaderName());
					leafWso.setLeaderTel(wso.getLeaderTel());
					
					organizationMapper.insert(leafWso);
				}
			}
		}
		return 1;
	}
}
