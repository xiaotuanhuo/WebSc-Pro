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
import sc.common.exception.DuplicateNameException;
import sc.common.util.ShiroUtil;
import sc.common.util.UUID19;
import sc.system.mapper.BureauMapper;
import sc.system.mapper.UserMapper;
import sc.system.model.WebScBureau;
import sc.system.model.WebScUser;
import sc.system.model.vo.DistTree;
import sc.system.model.vo.District;

@Service
public class BureauService {
	private static final Logger logger = LoggerFactory.getLogger(BureauService.class);
	
	@Resource
	private BureauMapper bureauMapper;
	
	@Resource
	private UserMapper userMapper;
	
	@Autowired
	private District district;
	
	public WebScBureau getByBureauId(String bureauId) {
		return bureauMapper.selectByPrimaryKey(bureauId);
	}
	
	public String add(WebScBureau bureau) {
		if (bureau.getArea() != null && bureau.getArea().equals("")) {
			bureau.setArea(null);
		}
		if (bureau.getCity() != null && bureau.getCity().equals("")) {
			bureau.setCity(null);
		}
		if (bureau.getProvince() != null && bureau.getProvince().equals("")) {
			bureau.setProvince(null);
		}
		checkDistExist(bureau.getProvince(), bureau.getCity(), bureau.getArea());
		checkNameExist(null, bureau.getBureauName());
		bureau.setBureauId(UUID19.uuid());
		bureauMapper.insert(bureau);
		return bureau.getBureauId();
	}
	
	public boolean update(WebScBureau bureau) {
		if (bureau.getArea() != null && bureau.getArea().equals("")) {
			bureau.setArea(null);
		}
		if (bureau.getCity() != null && bureau.getCity().equals("")) {
			bureau.setCity(null);
		}
		if (bureau.getProvince() != null && bureau.getProvince().equals("")) {
			bureau.setProvince(null);
		}
		checkNameExist(bureau.getBureauId(), bureau.getBureauName());
		return bureauMapper.updateByPrimaryKey(bureau) == 1;
	}
	
	public void lock(String bureauId) {
		// 锁定卫监局时其子节点及关联用户也会锁定
		List<WebScBureau> bureaus = bureauMapper.selectTree(bureauId, null, null, null);
		updateStatus(bureaus);
	}
	
	public void unlock(String bureauId) {
		// 上级节点未激活时无法激活当前节点
		WebScBureau bureau = bureauMapper.selectByPrimaryKey(bureauId);
		WebScBureau superBureau = bureauMapper.selectByPrimaryKey(bureau.getBureauPid());
		if (superBureau != null && superBureau.getStatus().equals(ShiroUtil.LOCK)) {
			throw new DuplicateNameException("上级机构已锁定，无法激活");
		}
		bureau.setStatus(ShiroUtil.UNLOCK);
		bureauMapper.updateByPrimaryKey(bureau);
	}
	
	/**
	 * 
	 * @param bureauId
	 */
	private void updateStatus(List<WebScBureau> bureaus) {
		for (WebScBureau bureau : bureaus) {
			List<WebScUser> users = userMapper.selectByRoleTypeId(bureau.getBureauId());
			for (WebScUser user : users) {
				if (user.getStatus().equals(ShiroUtil.UNLOCK)) {
					user.setStatus(ShiroUtil.LOCK);
					userMapper.updateByPrimaryKey(user);
				}
			}
			if (bureau.getStatus().equals(ShiroUtil.UNLOCK)) {
				bureau.setStatus(ShiroUtil.LOCK);
				bureauMapper.updateByPrimaryKey(bureau);
			}
			updateStatus(bureau.getChildren());
		}
	}
	
	public List<WebScBureau> getTree() {
		Subject subject = SecurityUtils.getSubject();
		WebScUser user = (WebScUser) subject.getPrincipal();
		List<WebScBureau> bureaus = bureauMapper.selectTree(null, user.getProvince(), user.getCity(), user.getArea());
		return bureaus;
	}
	
	public List<WebScBureau> getUnleafTree() {
		Subject subject = SecurityUtils.getSubject();
		WebScUser user = (WebScUser) subject.getPrincipal();
		List<WebScBureau> bureaus = bureauMapper.selectUnleafTree(user.getProvince(), user.getCity(), user.getArea());
		// 区域管理员用户需要查询其所属区划的上级卫监局，用以新增、编辑卫监局时的上级卫监局
		switch (RoleEnum.valueOf(Integer.parseInt(user.getRoleId()))) {
			case QYGLY:
				WebScBureau bureau = bureauMapper.selectSuper(user.getProvince(), user.getCity(), user.getArea());
				if (bureau != null) {
					List<WebScBureau> bureausWithSuper = new ArrayList<WebScBureau>();
					bureau.setChildren(bureaus);
					bureausWithSuper.add(bureau);
					return bureausWithSuper;
				}
				break;
			default:
				break;
		}
		return bureaus;
	}
	
    public List<WebScBureau> getListTree(String bureauId) {
    	Subject subject = SecurityUtils.getSubject();
		WebScUser user = (WebScUser) subject.getPrincipal();
		List<WebScBureau> bureaus = bureauMapper.selectTree(bureauId, user.getProvince(), user.getCity(), user.getArea());
//		// 前端对表的操作由于插件的缘故，从后台生成操作代码
//		// 其中编辑和删除对系统管理员、超级管理员、区域管理员可见
//		boolean show = false;
//		switch (RoleEnum.valueOf(Integer.parseInt(user.getRoleId()))) {
//			case XTGLY:
//			case CJGLY:
//			case QYGLY:
//				show = true;
//				break;
//			default:
//				break;
//		}
		return getDistName(bureaus);
    }
    
    public WebScBureau getBereauInfo(String bureauId) {
    	WebScBureau bureau = bureauMapper.selectByPrimaryKey(bureauId);
    	if (bureau.getProvince() != null) {
			bureau.setProvinceName(district.getDistrictMap().get(bureau.getProvince()).getName());
		}
    	if (bureau.getCity() != null) {
			bureau.setCityName(district.getDistrictMap().get(bureau.getCity()).getName());
		}
    	if (bureau.getArea() != null) {
			bureau.setAreaName(district.getDistrictMap().get(bureau.getArea()).getName());
		}
    	return bureau;
    }
    
    /**
	 * 递归获取集团所属行政区划的区划名称
	 * @param depts
	 * @return
	 */
	private List<WebScBureau> getDistName(List<WebScBureau> bureaus) {
		if (bureaus.size() > 0) {
			for (WebScBureau bureau : bureaus) {
//				String htmlDetail = "<a lay-event=\"detail" + bureau.getBureauId() + "\">\r\n" + 
//						"		<i class=\"layui-icon layui-icon-form zadmin-oper-area zadmin-blue\" title=\"详情\"></i>\r\n" + 
//						"	</a>";
//				String htmlEdit = "<a lay-event=\"edit" + bureau.getBureauId() + "\">\r\n" + 
//						"		<i class=\"zadmin-icon zadmin-icon-edit-square zadmin-oper-area zadmin-blue\" title=\"编辑\"></i>\r\n" + 
//						"	</a>";
//				String htmlDel = "<a lay-event=\"del" + bureau.getBureauId() + "\">\r\n" + 
//						"		<i class=\"zadmin-icon zadmin-icon-delete zadmin-oper-area zadmin-red\" title=\"删除\"></i>\r\n" + 
//						"	</a>";
//				if (show) {
//					bureau.setOperator(htmlDetail + htmlEdit + htmlDel);
//				} else {
//					bureau.setOperator(htmlDetail);
//				}
				if (bureau.getProvince() != null) {
					bureau.setProvinceName(district.getDistrictMap().get(bureau.getProvince()).getName());
				}
				if (bureau.getCity() != null) {
					bureau.setCityName(district.getDistrictMap().get(bureau.getCity()).getName());
				}
				if (bureau.getArea() != null) {
					bureau.setAreaName(district.getDistrictMap().get(bureau.getArea()).getName());
				}
				if (bureau.getChildren().size() > 0) {
					getDistName(bureau.getChildren());
				}
			}
		}
		return bureaus;
	}
	
	/**
	 * 根据当前用户获取带有卫监局根节点的行政区划（树）
	 * @return
	 */
	public List<DistTree> getDistTree() {
		Subject subject = SecurityUtils.getSubject();
		WebScUser user = (WebScUser) subject.getPrincipal();
		List<DistTree> dists = new ArrayList<DistTree>();
		WebScBureau superBureau = bureauMapper.selectSuperBureau();	// 获取卫监局根节点
		if (superBureau == null) {
			return dists;
		}
		DistTree superDist = new DistTree();
		DistTree provinceDist = new DistTree();
		DistTree cityDist = new DistTree();
		List<WebScBureau> bureaus = bureauMapper.selectTree(null, user.getProvince(), user.getCity(), user.getArea());
		switch (RoleEnum.valueOf(Integer.parseInt(user.getRoleId()))) {
			case XTGLY:
			case CJGLY:
				// 添加医疗机构根节点
				superDist.setId(superBureau.getBureauId());
				superDist.setParentId(superBureau.getBureauPid());
				superDist.setName(superBureau.getBureauName());
				superDist.setInstitution(null);
				dists.add(superDist);
				break;
			case QYGLY:
				// 区域管理员仅有省属和市属
				// 市属区域管理员添加以医疗机构根节点为父节点的省级节点
				if (bureaus.size() > 0) {
					if (user.getCity() != null) {
						provinceDist.setId(user.getProvince());
						provinceDist.setParentId(superBureau.getBureauId());
						provinceDist.setName(district.getDistrictMap().get(user.getProvince()).getName());
						provinceDist.setInstitution(bureaus.get(0).getBureauId());
						dists.add(provinceDist);
					}
					// 添加医疗机构根节点
					superDist.setId(superBureau.getBureauId());
					superDist.setParentId(superBureau.getBureauPid());
					superDist.setName(superBureau.getBureauName());
					superDist.setInstitution(bureaus.get(0).getBureauId());
					dists.add(superDist);
				} else {
					if (user.getCity() != null) {
						cityDist.setId(user.getCity());
						cityDist.setParentId(user.getProvince());
						cityDist.setName(district.getDistrictMap().get(user.getCity()).getName());
						cityDist.setInstitution(null);
						dists.add(cityDist);
					}
					provinceDist.setId(user.getProvince());
					provinceDist.setParentId(superBureau.getBureauId());
					provinceDist.setName(district.getDistrictMap().get(user.getProvince()).getName());
					provinceDist.setInstitution(null);
					dists.add(provinceDist);
					
					// 添加医疗机构根节点
					superDist.setId(superBureau.getBureauId());
					superDist.setParentId(superBureau.getBureauPid());
					superDist.setName(superBureau.getBureauName());
					superDist.setInstitution(null);
					dists.add(superDist);
				}
				break;
			case WJJGLY:
				if (user.getArea() != null) {
					cityDist.setId(user.getCity());
					cityDist.setParentId(user.getProvince());
					cityDist.setName(district.getDistrictMap().get(user.getCity()).getName());
					cityDist.setInstitution(user.getRoleTypeId());
					dists.add(cityDist);
					
					provinceDist.setId(user.getProvince());
					provinceDist.setParentId(superBureau.getBureauId());
					provinceDist.setName(district.getDistrictMap().get(user.getProvince()).getName());
					provinceDist.setInstitution(user.getRoleTypeId());
					dists.add(provinceDist);
				} else if (user.getCity() != null) {
					provinceDist.setId(user.getProvince());
					provinceDist.setParentId(superBureau.getBureauId());
					provinceDist.setName(district.getDistrictMap().get(user.getProvince()).getName());
					provinceDist.setInstitution(user.getRoleTypeId());
					dists.add(provinceDist);
				}
				// 添加医疗机构根节点
				superDist.setId(superBureau.getBureauId());
				superDist.setParentId(superBureau.getBureauPid());
				superDist.setName(superBureau.getBureauName());
				superDist.setInstitution(user.getRoleTypeId());
				dists.add(superDist);
				break;
			default:
				break;
		}
		dists = convert(bureaus, dists);
		return dists;
	}
	
	private List<DistTree> convert(List<WebScBureau> bureaus, List<DistTree> dists) {
		if (bureaus.size() > 0) {
			for (WebScBureau bureau : bureaus) {
				DistTree dist = new DistTree();
				if (bureau.getArea() != null) {
					dist.setId(bureau.getArea());
					dist.setParentId(bureau.getCity());
					dist.setName(district.getDistrictMap().get(bureau.getArea()).getName());
					dist.setInstitution(bureau.getBureauId());
					dists.add(dist);
				} else if (bureau.getCity() != null) {
					dist.setId(bureau.getCity());
					dist.setParentId(bureau.getProvince());
					dist.setName(district.getDistrictMap().get(bureau.getCity()).getName());
					dist.setInstitution(bureau.getBureauId());
					dists.add(dist);
				} else if(bureau.getProvince() != null) {
					dist.setId(bureau.getProvince());
					dist.setParentId(bureau.getBureauPid());
					dist.setName(district.getDistrictMap().get(bureau.getProvince()).getName());
					dist.setInstitution(bureau.getBureauId());
					dists.add(dist);
				}
				if (bureau.getChildren().size() > 0) {
					convert(bureau.getChildren(), dists);
				}
			}
		}
		return dists;
	}
	
	/**
	 * 校验当前区划下是否已存在卫监局
	 * @param province
	 * @param city
	 * @param area
	 */
	public void checkDistExist(String province, String city, String area) {
		if (bureauMapper.countByDist(province, city, area) > 0) {
			throw new DuplicateNameException("当前行政区划下已存在卫监局");
		}
	}
	
	public void checkNameExist(String bureauId, String bureauName) {
		if (bureauMapper.countByName(bureauId, bureauName) > 0) {
			throw new DuplicateNameException("卫监局名称已存在");
		}
	}
}
