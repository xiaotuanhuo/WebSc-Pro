package sc.system.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.poifs.crypt.dsig.DSigRelation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sc.common.constants.DistType;
import sc.common.constants.RoleEnum;
import sc.common.exception.DuplicateNameException;
import sc.common.util.ShiroUtil;
import sc.common.util.UUID19;
import sc.system.mapper.OrganizationMapper;
import sc.system.mapper.UserMapper;
import sc.system.model.WebScOrganization;
import sc.system.model.WebScUser;
import sc.system.model.vo.DistTree;
import sc.system.model.vo.District;

@Service
public class OrganizationService {
	private static final Logger log = LoggerFactory.getLogger(OrganizationService.class);
	
	@Resource
	private OrganizationMapper organizationMapper;
	
	@Resource
	private UserMapper userMapper;
	
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
				wsoList = organizationMapper.selectAll(false, false, null, user, wso);
				break;
			case WJJGLY:
				wsoList = organizationMapper.selectAll(true, false, null, user, wso);
				break;
			case YLJGGLY:
				// 判断其roolTypeId是否是叶子节点
				// 是：利用该id查询
				// 否：利用根节点+区划查询
				WebScOrganization organization = organizationMapper.selectByPrimaryKey(user.getRoleTypeId());
				String rootId = organization.getLeaf() == 1 ? null : organization.getRootId();
				wsoList = organizationMapper.selectAll(false, true, rootId, user, wso);
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
	
	
//	public List<WebScOrganization> getList(WebScOrganization wso, String organizationId, String distType) {
//		Subject subject = SecurityUtils.getSubject();
//		WebScUser user = (WebScUser) subject.getPrincipal();
//		List<WebScOrganization> wsoList = new ArrayList<>();
//		String rootId = null;
//		String userProvince = user.getProvince();
//		String userCity = user.getCity();
//		String userArea = user.getArea();
//		if (organizationId != null) {
//			WebScOrganization organization = organizationMapper.selectByPrimaryKey(organizationId);
//			rootId = organization.getRootId();
//			DistType dt = DistType.valueOf(Integer.parseInt(distType));
//			switch (dt) {
//				case PROVINCE:
//					userProvince = organization.getProvince();
//					break;
//				case CITY:
//					userCity = organization.getCity();
//					break;
//				case COUNTY:
//					userArea = organization.getArea();
//					break;
//				default:
//					break;
//			}
//		}
//		switch (RoleEnum.valueOf(Integer.parseInt(user.getRoleId()))) {
//			case XTGLY:
//			case CJGLY:
//			case QYGLY:
//				wsoList = organizationMapper.selectListByAuthData(rootId, false, userProvince, userCity, userArea, wso);
//				break;
//			case WJJGLY:
//				wsoList = organizationMapper.selectListByAuthData(rootId, true, userProvince, userCity, userArea, wso);
//				break;
//			case YLJGGLY:
//				// 查询当前用户的医疗机构根节点
//				WebScOrganization userOrganization = organizationMapper.selectByPrimaryKey(user.getRoleTypeId());
//				wsoList = organizationMapper.selectListByAuthData(userOrganization.getRootId(), false, userProvince,
//					userCity, userArea, wso);
//				break;
//			default:
//				break;
//		}
//		
//		if (wsoList.size() != 0) {
//			for (WebScOrganization webScOrganization : wsoList) {
//				webScOrganization.setProvinceName(district.getDistrictMap().get(webScOrganization.getProvince()).getName());
//				webScOrganization.setCityName(district.getDistrictMap().get(webScOrganization.getCity()).getName());
//				webScOrganization.setAreaName(district.getDistrictMap().get(webScOrganization.getArea()).getName());
//			}
//		}
//		return wsoList;
//	}
	
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
	
	public List<DistTree> getDistTree() {
		List<WebScOrganization> organizations = new ArrayList<WebScOrganization>();
		Subject subject = SecurityUtils.getSubject();
		WebScUser user = (WebScUser) subject.getPrincipal();
		List<DistTree> dists = new ArrayList<DistTree>();
		switch (RoleEnum.valueOf(Integer.parseInt(user.getRoleId()))) {
			case XTGLY:
			case CJGLY:
				organizations = organizationMapper.selectAllTree(null, user.getProvince(), user.getCity(), user.getArea());
//				superDist.setId("99");
//				superDist.setParentId("233");	// 虚拟的根节点的父节点
//				superDist.setName("医疗机构");
//				dists.add(superDist);
				dists = convert(organizations, dists);
				break;
			case QYGLY:
				organizations = organizationMapper.selectAllTree(null, user.getProvince(), user.getCity(), user.getArea());
				if (organizations.size() > 0) {
					for (WebScOrganization organization : organizations) {
						DistTree cityDist = new DistTree();		// 市级医疗机构
						DistTree provinceDist = new DistTree();	// 省级医疗机构
						DistTree superDist = new DistTree();	// 医疗机构的根节点
						if (organization.getCity() != null) {
							cityDist.setId(organization.getOrgId());
							cityDist.setParentId(organization.getOrgPid());
							cityDist.setName(district.getDistrictMap().get(organization.getCity()).getName());
							cityDist.setInstitution(organization.getOrgId());
							cityDist.setDistType(DistType.CITY.getCode());
							
							provinceDist.setId(organization.getOrgPid());
							provinceDist.setParentId(organization.getRootId());
							provinceDist.setName(district.getDistrictMap().get(organization.getProvince()).getName());
							provinceDist.setInstitution(organization.getOrgId());
							provinceDist.setDistType(DistType.CITY.getCode());
							
							WebScOrganization rootOrganization = organizationMapper.selectByPrimaryKey(organization.getRootId());
							superDist.setId(rootOrganization.getOrgId());
							superDist.setParentId(rootOrganization.getOrgPid());
							superDist.setName(rootOrganization.getOrgName());
							superDist.setInstitution(organization.getOrgId());
							superDist.setDistType(DistType.CITY.getCode());
							
							dists.add(superDist);
							dists.add(provinceDist);
							dists.add(cityDist);
							dists = convert(organization.getChildren(), dists);
						} else if (organization.getProvince() != null) {
							provinceDist.setId(organization.getOrgId());
							provinceDist.setParentId(organization.getOrgPid());
							provinceDist.setName(district.getDistrictMap().get(organization.getProvince()).getName());
							provinceDist.setInstitution(organization.getOrgId());
							provinceDist.setDistType(DistType.PROVINCE.getCode());
							
							WebScOrganization rootOrganization = organizationMapper.selectByPrimaryKey(organization.getRootId());
							superDist.setId(rootOrganization.getOrgId());
							superDist.setParentId(rootOrganization.getOrgPid());
							superDist.setName(rootOrganization.getOrgName());
							superDist.setInstitution(organization.getOrgId());
							superDist.setDistType(DistType.PROVINCE.getCode());
							
							dists.add(superDist);
							dists.add(provinceDist);
							dists = convert(organization.getChildren(), dists);
						}
					}
				}
				break;
			case WJJGLY:
				organizations = organizationMapper.selectAllTree(null, user.getProvince(), user.getCity(), user.getArea());
				organizations = getTreeOfCredentials(organizations);
				if (organizations.size() > 0) {
					for (WebScOrganization organization : organizations) {
						DistTree areaDist = new DistTree();		// 区/县级医疗机构
						DistTree cityDist = new DistTree();		// 市级医疗机构
						DistTree provinceDist = new DistTree();	// 省级医疗机构
						DistTree superDist = new DistTree();	// 医疗机构的根节点
						if (organization.getArea() != null) {
							areaDist.setId(organization.getArea());
							areaDist.setParentId(organization.getOrgPid());
							areaDist.setName(district.getDistrictMap().get(organization.getArea()).getName());
							areaDist.setInstitution(organization.getOrgId());
							areaDist.setDistType(DistType.COUNTY.getCode());
							
							WebScOrganization cityOrganization = organizationMapper.selectByPrimaryKey(organization.getOrgPid());
							cityDist.setId(cityOrganization.getOrgId());
							cityDist.setParentId(cityOrganization.getOrgPid());
							cityDist.setName(district.getDistrictMap().get(cityOrganization.getCity()).getName());
							cityDist.setInstitution(organization.getOrgId());
							cityDist.setDistType(DistType.COUNTY.getCode());
							
							provinceDist.setId(cityOrganization.getOrgPid());
							provinceDist.setParentId(cityOrganization.getRootId());
							provinceDist.setName(district.getDistrictMap().get(cityOrganization.getProvince()).getName());
							provinceDist.setInstitution(organization.getOrgId());
							provinceDist.setDistType(DistType.COUNTY.getCode());
							
							WebScOrganization rootOrganization = organizationMapper.selectByPrimaryKey(cityOrganization.getRootId());
							superDist.setId(rootOrganization.getOrgId());
							superDist.setParentId(rootOrganization.getOrgPid());
							superDist.setName(rootOrganization.getOrgName());
							superDist.setInstitution(organization.getOrgId());
							superDist.setDistType(DistType.COUNTY.getCode());
							
							dists.add(superDist);
							dists.add(provinceDist);
							dists.add(cityDist);
							dists.add(areaDist);
						} else if (organization.getCity() != null) {
							cityDist.setId(organization.getOrgId());
							cityDist.setParentId(organization.getOrgPid());
							cityDist.setName(district.getDistrictMap().get(organization.getCity()).getName());
							cityDist.setInstitution(organization.getOrgId());
							cityDist.setDistType(DistType.CITY.getCode());
							
							provinceDist.setId(organization.getOrgPid());
							provinceDist.setParentId(organization.getRootId());
							provinceDist.setName(district.getDistrictMap().get(organization.getProvince()).getName());
							provinceDist.setInstitution(organization.getOrgId());
							provinceDist.setDistType(DistType.CITY.getCode());
							
							WebScOrganization rootOrganization = organizationMapper.selectByPrimaryKey(organization.getRootId());
							superDist.setId(rootOrganization.getOrgId());
							superDist.setParentId(rootOrganization.getOrgPid());
							superDist.setInstitution(organization.getOrgId());
							superDist.setName(rootOrganization.getOrgName());
							superDist.setDistType(DistType.CITY.getCode());
							
							dists.add(superDist);
							dists.add(provinceDist);
							dists.add(cityDist);
							dists = convert(organization.getChildren(), dists);
						} else if (organization.getProvince() != null) {
							provinceDist.setId(organization.getOrgId());
							provinceDist.setParentId(organization.getOrgPid());
							provinceDist.setName(district.getDistrictMap().get(organization.getProvince()).getName());
							provinceDist.setInstitution(organization.getOrgId());
							provinceDist.setDistType(DistType.PROVINCE.getCode());
							
							WebScOrganization rootOrganization = organizationMapper.selectByPrimaryKey(organization.getRootId());
							superDist.setId(rootOrganization.getOrgId());
							superDist.setParentId(rootOrganization.getOrgPid());
							superDist.setName(rootOrganization.getOrgName());
							superDist.setInstitution(organization.getOrgId());
							superDist.setDistType(DistType.PROVINCE.getCode());
							
							dists.add(superDist);
							dists.add(provinceDist);
							dists = convert(organization.getChildren(), dists);
						} else {
							dists = convert(organizations, dists);
							break;
						}
					}
				}
				break;
			case YLJGGLY:
				organizations = organizationMapper.selectAllTree(user.getRoleTypeId(), user.getProvince(), user.getCity(), user.getArea());
				if (organizations.size() > 0) {
					for (WebScOrganization organization : organizations) {
						DistTree cityDist = new DistTree();		// 市级医疗机构
						DistTree provinceDist = new DistTree();	// 省级医疗机构
						DistTree superDist = new DistTree();	// 医疗机构的根节点
						if (user.getArea() != null) {
							WebScOrganization cityOrganization = organizationMapper.selectByPrimaryKey(organization.getOrgPid());
							cityDist.setId(cityOrganization.getOrgId());
							cityDist.setParentId(cityOrganization.getOrgPid());
							cityDist.setName(district.getDistrictMap().get(cityOrganization.getCity()).getName());
							cityDist.setInstitution(organization.getOrgId());
							cityDist.setDistType(DistType.COUNTY.getCode());
							
							provinceDist.setId(cityOrganization.getOrgPid());
							provinceDist.setParentId(cityOrganization.getRootId());
							provinceDist.setName(district.getDistrictMap().get(cityOrganization.getProvince()).getName());
							provinceDist.setInstitution(organization.getOrgId());
							provinceDist.setInstitution(organization.getOrgId());
							provinceDist.setDistType(DistType.COUNTY.getCode());
							
							WebScOrganization rootOrganization = organizationMapper.selectByPrimaryKey(cityOrganization.getRootId());
							superDist.setId(rootOrganization.getOrgId());
							superDist.setParentId(rootOrganization.getOrgPid());
							superDist.setName(rootOrganization.getOrgName());
							superDist.setInstitution(organization.getOrgId());
							superDist.setDistType(DistType.COUNTY.getCode());
							
							dists.add(superDist);
							dists.add(provinceDist);
							dists.add(cityDist);
						} else if (user.getCity() != null) {
							provinceDist.setId(organization.getOrgPid());
							provinceDist.setParentId(organization.getRootId());
							provinceDist.setName(district.getDistrictMap().get(organization.getProvince()).getName());
							provinceDist.setInstitution(organization.getOrgId());
							provinceDist.setDistType(DistType.CITY.getCode());
							
							WebScOrganization rootOrganization = organizationMapper.selectByPrimaryKey(organization.getRootId());
							superDist.setId(rootOrganization.getOrgId());
							superDist.setParentId(rootOrganization.getOrgPid());
							superDist.setName(rootOrganization.getOrgName());
							superDist.setInstitution(organization.getOrgId());
							superDist.setDistType(DistType.CITY.getCode());
							
							dists.add(superDist);
							dists.add(provinceDist);
						} else if (user.getProvince() != null) {
							WebScOrganization rootOrganization = organizationMapper.selectByPrimaryKey(organization.getRootId());
							superDist.setId(rootOrganization.getOrgId());
							superDist.setParentId(rootOrganization.getOrgPid());
							superDist.setName(rootOrganization.getOrgName());
							superDist.setInstitution(organization.getOrgId());
							superDist.setDistType(DistType.PROVINCE.getCode());
							
							dists.add(superDist);
						}
						dists = convert(organizations, dists);
						break;
					}
				}
				break;
			default:
				break;
		}
		return dists;
	}
	
	private List<DistTree> convert(List<WebScOrganization> organizations, List<DistTree> dists) {
		if (organizations.size() > 0) {
			for (WebScOrganization organization : organizations) {
				if (organization.getLeaf() != 1) {
					DistTree dist = new DistTree();
					if (organization.getArea() != null) {
						dist.setId(organization.getOrgId());
						dist.setParentId(organization.getOrgPid());
						dist.setName(district.getDistrictMap().get(organization.getArea()).getName());
						dist.setInstitution(organization.getOrgId());
						dist.setDistType(DistType.COUNTY.getCode());
						dists.add(dist);
					} else if (organization.getCity() != null) {
						dist.setId(organization.getOrgId());
						dist.setParentId(organization.getOrgPid());
						dist.setName(district.getDistrictMap().get(organization.getCity()).getName());
						dist.setInstitution(organization.getOrgId());
						dist.setDistType(DistType.CITY.getCode());
						dists.add(dist);
					} else if (organization.getProvince() != null) {
						dist.setId(organization.getOrgId());
						dist.setParentId(organization.getOrgPid());
						dist.setName(district.getDistrictMap().get(organization.getProvince()).getName());
						dist.setInstitution(organization.getOrgId());
						dist.setDistType(DistType.PROVINCE.getCode());
						dists.add(dist);
					} else {
						dist.setId(organization.getOrgId());
						dist.setParentId(organization.getOrgPid());
						dist.setName(organization.getOrgName());
						dist.setInstitution(organization.getOrgId());
						dist.setDistType(DistType.STATE.getCode());
						dists.add(dist);
					}
				}
				if (organization.getChildren().size() > 0) {
					convert(organization.getChildren(), dists);
				}
			}
		}
		return dists;
	}
	
	public int insertRoot(WebScOrganization wso) {
		checkNameExist(null, wso.getOrgName());
		String id = UUID19.uuid();
		wso.setOrgId(id);
		wso.setOrgPid(wso.getOrgPid());
		wso.setLeaf(0);
		return organizationMapper.insert(wso);
	}
	
	public int insert(WebScOrganization wso) {
		checkNameExist(null, wso.getOrgName());
		return addTree(wso);
	}
	
	public boolean update(WebScOrganization organization) {
		return updateTree(organization);
//		return organizationMapper.updateByPrimaryKey(organization) == 1 ? true : false;
	}
	
	public void lock(String orgId) {
		udpateStatus(orgId);
	}
	
	public void unlock(String orgId) {
		WebScOrganization organization = organizationMapper.selectByPrimaryKey(orgId);
		organization.setStatus(ShiroUtil.UNLOCK);
		organizationMapper.updateByPrimaryKey(organization);
	}
	
	/**
	 * 锁定医疗机构及关联用户
	 * @param orgId
	 */
	@Transactional
	private void udpateStatus(String orgId) {
		List<WebScUser> users = userMapper.selectByRoleTypeId(orgId);
		for (WebScUser user : users) {
			if (user.getStatus().equals(ShiroUtil.UNLOCK)) {
				user.setStatus(ShiroUtil.LOCK);
				userMapper.updateByPrimaryKey(user);
			}
		}
		WebScOrganization organization = organizationMapper.selectByPrimaryKey(orgId);
		organization.setStatus(ShiroUtil.LOCK);
		organizationMapper.updateByPrimaryKey(organization);
	}
	
	public WebScOrganization getByOrgid(String id) {
		return organizationMapper.selectByPrimaryKey(id);
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
		String rootId = wso.getRootId();
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
			provinceWso.setOrgId(rootId + province);
			provinceWso.setOrgPid(rootId);
			provinceWso.setRootId(rootId);
			provinceWso.setOrgName(district.getDistrictMap().get(province).getName() + "_" + wso.getRootName());
			provinceWso.setProvince(province);
			provinceWso.setLeaf(0);
			
			cityWso.setOrgId(rootId + city);
			cityWso.setOrgPid(rootId + province);
			cityWso.setRootId(rootId);
			cityWso.setOrgName(district.getDistrictMap().get(city).getName() + "_" + wso.getRootName());
			cityWso.setProvince(province);
			cityWso.setCity(city);
			cityWso.setLeaf(0);
			
			areaWso.setOrgId(rootId + area);
			areaWso.setOrgPid(rootId + city);
			areaWso.setRootId(rootId);
			areaWso.setOrgName(district.getDistrictMap().get(area).getName() + "_" + wso.getRootName());
			areaWso.setProvince(province);
			areaWso.setCity(city);
			areaWso.setArea(area);
			areaWso.setLeaf(0);
			
			leafWso.setOrgId(UUID19.uuid());
			leafWso.setOrgPid(rootId + area);
			leafWso.setRootId(rootId);
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
				cityWso.setOrgId(rootId + city);
				cityWso.setOrgPid(parentId);
				cityWso.setRootId(rootId);
				cityWso.setOrgName(district.getDistrictMap().get(city).getName() + "_" + wso.getRootName());
				cityWso.setProvince(province);
				cityWso.setCity(city);
				cityWso.setLeaf(0);
				
				areaWso.setOrgId(rootId + area);
				areaWso.setOrgPid(rootId + city);
				areaWso.setRootId(rootId);
				areaWso.setOrgName(district.getDistrictMap().get(area).getName() + "_" + wso.getRootName());
				areaWso.setProvince(province);
				areaWso.setCity(city);
				areaWso.setArea(area);
				areaWso.setLeaf(0);
				
				leafWso.setOrgId(UUID19.uuid());
				leafWso.setOrgPid(rootId + area);
				leafWso.setRootId(rootId);
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
					areaWso.setOrgId(rootId + area);
					areaWso.setOrgPid(parentId);
					areaWso.setRootId(rootId);
					areaWso.setOrgName(district.getDistrictMap().get(area).getName() + "_" + wso.getRootName());
					areaWso.setProvince(province);
					areaWso.setCity(city);
					areaWso.setArea(area);
					areaWso.setLeaf(0);
					
					leafWso.setOrgId(UUID19.uuid());
					leafWso.setOrgPid(rootId + area);
					leafWso.setRootId(rootId);
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
					leafWso.setOrgPid(parentId);
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
	
	@Transactional
	private boolean updateTree(WebScOrganization wso) {
		String rootId = wso.getRootId();
		String parentId = rootId;
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
			provinceWso.setOrgId(rootId + province);
			provinceWso.setOrgPid(rootId);
			provinceWso.setRootId(rootId);
			provinceWso.setOrgName(district.getDistrictMap().get(province).getName() + "_" + wso.getRootName());
			provinceWso.setProvince(province);
			provinceWso.setLeaf(0);
			
			cityWso.setOrgId(rootId + city);
			cityWso.setOrgPid(rootId + province);
			cityWso.setRootId(rootId);
			cityWso.setOrgName(district.getDistrictMap().get(city).getName() + "_" + wso.getRootName());
			cityWso.setProvince(province);
			cityWso.setCity(city);
			cityWso.setLeaf(0);
			
			areaWso.setOrgId(rootId + area);
			areaWso.setOrgPid(rootId + city);
			areaWso.setRootId(rootId);
			areaWso.setOrgName(district.getDistrictMap().get(area).getName() + "_" + wso.getRootName());
			areaWso.setProvince(province);
			areaWso.setCity(city);
			areaWso.setArea(area);
			areaWso.setLeaf(0);
			
			wso.setOrgPid(rootId + area);
			
			// 新增
			organizationMapper.insert(provinceWso);
			organizationMapper.insert(cityWso);
			organizationMapper.insert(areaWso);
			// 更新
			return organizationMapper.updateByPrimaryKey(wso) == 1;
		} else {
			// 2、查询市节点是否已被添加，如果未被添加，则从市至叶子节点逐级添加
			//	    如果已添加，则将其id作为父id查询市节点是否被添加
			parentId = districtWso.getOrgId();
			districtWso = organizationMapper.selectByParentIdAndDistrict(parentId, province, city, null);
			if (districtWso == null) {
				cityWso.setOrgId(rootId + city);
				cityWso.setOrgPid(parentId);
				cityWso.setRootId(rootId);
				cityWso.setOrgName(district.getDistrictMap().get(city).getName() + "_" + wso.getRootName());
				cityWso.setProvince(province);
				cityWso.setCity(city);
				cityWso.setLeaf(0);
				
				areaWso.setOrgId(rootId + area);
				areaWso.setOrgPid(rootId + city);
				areaWso.setRootId(rootId);
				areaWso.setOrgName(district.getDistrictMap().get(area).getName() + "_" + wso.getRootName());
				areaWso.setProvince(province);
				areaWso.setCity(city);
				areaWso.setArea(area);
				areaWso.setLeaf(0);
				
				wso.setOrgPid(rootId + area);
				
				organizationMapper.insert(cityWso);
				organizationMapper.insert(areaWso);
				return organizationMapper.updateByPrimaryKey(wso) == 1;
			} else {
				// 3、查询区/县节点是否已被添加，如果未被添加，则从区/县至叶子节点逐级添加
				//	    如果已添加，则将其id作为父id添加叶子节点
				parentId = districtWso.getOrgId();
				districtWso = organizationMapper.selectByParentIdAndDistrict(parentId, province, city, area);
				if (districtWso == null) {
					areaWso.setOrgId(rootId + area);
					areaWso.setOrgPid(parentId);
					areaWso.setRootId(rootId);
					areaWso.setOrgName(district.getDistrictMap().get(area).getName() + "_" + wso.getRootName());
					areaWso.setProvince(province);
					areaWso.setCity(city);
					areaWso.setArea(area);
					areaWso.setLeaf(0);
					
					wso.setOrgPid(rootId + area);
					
					organizationMapper.insert(areaWso);
					return organizationMapper.updateByPrimaryKey(wso) == 1;
				} else {
					wso.setOrgPid(rootId + area);
					return organizationMapper.updateByPrimaryKey(wso) == 1;
				}
			}
		}
	}
	
	/**
	 * 新增时校验机构名称是否重复
	 * @param name
	 *            机构名称
	 */
	public void checkNameExist(String orgId, String name) {
		if (organizationMapper.countByName(orgId, name) > 0) {
			throw new DuplicateNameException("机构名称已存在");
		}
	}
}
