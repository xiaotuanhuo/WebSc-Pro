package sc.system.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import sc.system.mapper.OrganizationMapper;
import sc.system.model.WebScOrganization;
import sc.system.model.vo.CDO;
import sc.system.model.vo.District;

@Service
public class FrameworkService {
	private static final Logger log = LoggerFactory.getLogger(FrameworkService.class);
	
	@Resource
	private District district;
	@Resource
	private OrganizationMapper organizationMapper;
	
	public List<CDO> selectCdoTreeAndRoot() {
    	List<CDO> rootList = new ArrayList<CDO>();
    	List<WebScOrganization> organizations = organizationMapper.selectAll();
    	
    	for (WebScOrganization webScOrganization : organizations) {
    		// 根节点
			if (webScOrganization.getOrgPid().equals("0")) {
				CDO cdo = listToTree(webScOrganization, organizations);
				cdo.setId(webScOrganization.getOrgId());
				cdo.setName(webScOrganization.getOrgName());
				cdo.setParentId("0");
				
				rootList.add(cdo);
			}
		}
    	return rootList;
    }
	
	/**
     * 组装医疗机构树形机构
     * @param organization		// XXXX医疗机构根节点
     * @param organizations		// 医疗机构集合
     * @return
     */
    private CDO listToTree(WebScOrganization organization, List<WebScOrganization> organizations) {
    	CDO cdo = new CDO();	// 树形XXXX医疗机构
    	for (WebScOrganization webScOrganization : organizations) {
			if (webScOrganization.getOrgPid().equals(organization.getOrgId())) {	// XXXX医疗机构下属机构
				if (cdo.getChildren() == null) {	// 未追加下属机构
					CDO leafcdo = new CDO();		// 下属机构
					CDO countycdo = new CDO();		// 区/县节点
					CDO citycdo = new CDO();		// 市节点
					CDO provincecdo = new CDO();	// 省节点
					// 将该下属机构作为区/县的叶子节点
					// 以省、市、区/县作为中间节点
					// XXXX医疗机构作为根节点的下属节点
					leafcdo.setId(webScOrganization.getOrgId());
					leafcdo.setName(webScOrganization.getOrgName());
					leafcdo.setParentId(webScOrganization.getArea());
					leafcdo.setRootOrgId(organization.getOrgId());
					
					countycdo.setId(webScOrganization.getArea());
					countycdo.setName(district.getDistrictMap().get(webScOrganization.getArea()).getName());
					countycdo.setParentId(webScOrganization.getCity());
					countycdo.setRootOrgId(organization.getOrgId());
					countycdo.setChildren(new ArrayList<CDO>());
					countycdo.getChildren().add(leafcdo);
					
					citycdo.setId(webScOrganization.getCity());
					citycdo.setName(district.getDistrictMap().get(webScOrganization.getCity()).getName());
					citycdo.setParentId(webScOrganization.getProvince());
					citycdo.setRootOrgId(organization.getOrgId());
					citycdo.setChildren(new ArrayList<CDO>());
					citycdo.getChildren().add(countycdo);
					
					provincecdo.setId(webScOrganization.getProvince());
					provincecdo.setName(district.getDistrictMap().get(webScOrganization.getProvince()).getName());
					provincecdo.setParentId(organization.getOrgId());
					provincecdo.setRootOrgId(organization.getOrgId());
					provincecdo.setChildren(new ArrayList<CDO>());
					provincecdo.getChildren().add(citycdo);
					
					cdo.setChildren(new ArrayList<CDO>());
					cdo.getChildren().add(provincecdo);
				} else {
					boolean isProvince = false;	// 省级区划是否添加的标志位
					for (CDO province : cdo.getChildren()) {			// 判断省级区划是否添加
						if (province.getId().equals(webScOrganization.getProvince())) {	// 省级区划已添加
							isProvince = true;
							boolean isCity = false;	// 市级区划是否添加的标志位
							for (CDO city : province.getChildren()) {		// 判断市级区划是否添加
								if (city.getId().equals(webScOrganization.getCity())) {	// 市级区划已添加
									isCity = true;
									boolean isCounty = false;	// 区/县级区划是否添加的标志位
									for (CDO county : city.getChildren()) {	// 判断区/县级区划是否添加
										if (county.getId().equals(webScOrganization.getArea())) {	// 区/县级区划已添加
											CDO leafcdo = new CDO();
											leafcdo.setId(webScOrganization.getOrgId());
											leafcdo.setName(webScOrganization.getOrgName());
											leafcdo.setParentId(webScOrganization.getArea());
											leafcdo.setRootOrgId(organization.getOrgId());
											county.getChildren().add(leafcdo);
											isCounty = true;
											break;
										}
									}
									if (!isCounty) {	// 区/县级区划未添加
										CDO leafcdo = new CDO();
										CDO countycdo = new CDO();
										
										leafcdo.setId(webScOrganization.getOrgId());
										leafcdo.setName(webScOrganization.getOrgName());
										leafcdo.setParentId(webScOrganization.getArea());
										leafcdo.setRootOrgId(organization.getOrgId());
										
										countycdo.setId(webScOrganization.getArea());
										countycdo.setName(district.getDistrictMap().get(webScOrganization.getArea()).getName());
										countycdo.setParentId(webScOrganization.getCity());
										countycdo.setRootOrgId(organization.getOrgId());
										countycdo.setChildren(new ArrayList<CDO>());
										countycdo.getChildren().add(leafcdo);
										
										city.getChildren().add(countycdo);
										
										isCounty = true;
									}
									break;
								}
							}
							if (!isCity) {	// 市级区划未添加
								CDO leafcdo = new CDO();
								CDO countycdo = new CDO();
								CDO citycdo = new CDO();
								
								leafcdo.setId(webScOrganization.getOrgId());
								leafcdo.setName(webScOrganization.getOrgName());
								leafcdo.setParentId(webScOrganization.getArea());
								leafcdo.setRootOrgId(organization.getOrgId());
								
								countycdo.setId(webScOrganization.getArea());
								countycdo.setName(district.getDistrictMap().get(webScOrganization.getArea()).getName());
								countycdo.setParentId(webScOrganization.getCity());
								countycdo.setRootOrgId(organization.getOrgId());
								countycdo.setChildren(new ArrayList<CDO>());
								countycdo.getChildren().add(leafcdo);
								
								citycdo.setId(webScOrganization.getCity());
								citycdo.setName(district.getDistrictMap().get(webScOrganization.getCity()).getName());
								citycdo.setParentId(webScOrganization.getProvince());
								citycdo.setRootOrgId(organization.getOrgId());
								citycdo.setChildren(new ArrayList<CDO>());
								citycdo.getChildren().add(countycdo);
								
								province.getChildren().add(citycdo);
								
								isCity = false;
							}
							break;
						}
					}
					if (!isProvince) {
						CDO leafcdo = new CDO();
						CDO countycdo = new CDO();
						CDO citycdo = new CDO();
						CDO provincecdo = new CDO();
						
						leafcdo.setId(webScOrganization.getOrgId());
						leafcdo.setName(webScOrganization.getOrgName());
						leafcdo.setParentId(webScOrganization.getArea());
						leafcdo.setRootOrgId(organization.getOrgId());
						
						countycdo.setId(webScOrganization.getArea());
						countycdo.setName(district.getDistrictMap().get(webScOrganization.getArea()).getName());
						countycdo.setParentId(webScOrganization.getCity());
						countycdo.setRootOrgId(organization.getOrgId());
						countycdo.setChildren(new ArrayList<CDO>());
						countycdo.getChildren().add(leafcdo);
						
						citycdo.setId(webScOrganization.getCity());
						citycdo.setName(district.getDistrictMap().get(webScOrganization.getCity()).getName());
						citycdo.setParentId(webScOrganization.getProvince());
						citycdo.setRootOrgId(organization.getOrgId());
						citycdo.setChildren(new ArrayList<CDO>());
						citycdo.getChildren().add(countycdo);
						
						provincecdo.setId(webScOrganization.getProvince());
						provincecdo.setName(district.getDistrictMap().get(webScOrganization.getProvince()).getName());
						provincecdo.setParentId(webScOrganization.getOrgId());
						provincecdo.setRootOrgId(organization.getOrgId());
						provincecdo.setChildren(new ArrayList<CDO>());
						provincecdo.getChildren().add(citycdo);
						
						cdo.getChildren().add(provincecdo);
					}
				}
			}
		}
    	return cdo;
    }
}
