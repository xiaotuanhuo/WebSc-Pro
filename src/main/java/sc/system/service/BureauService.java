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
import sc.system.mapper.BureauMapper;
import sc.system.model.WebScBureau;
import sc.system.model.WebScUser;
import sc.system.model.vo.DistTree;
import sc.system.model.vo.District;

@Service
public class BureauService {
	private static final Logger logger = LoggerFactory.getLogger(BureauService.class);
	
	@Resource
	private BureauMapper bureauMapper;
	
	@Autowired
	private District district;
	
    public List<WebScBureau> getTree(String bureauId) {
    	Subject subject = SecurityUtils.getSubject();
		WebScUser user = (WebScUser) subject.getPrincipal();
		List<WebScBureau> bureaus = bureauMapper.selectTree(bureauId, user.getProvince(), user.getCity(), user.getArea());
		// 前端对表的操作由于插件的缘故，从后台生成操作代码
		// 其中编辑和删除对系统管理员、超级管理员、区域管理员可见
		boolean show = false;
		switch (RoleEnum.valueOf(Integer.parseInt(user.getRoleId()))) {
			case XTGLY:
			case CJGLY:
			case QYGLY:
				show = true;
				break;
			default:
				break;
		}
		bureaus = getDistName(bureaus, show);
		return getDistName(bureaus, show);
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
	private List<WebScBureau> getDistName(List<WebScBureau> bureaus, boolean show) {
		if (bureaus.size() > 0) {
			for (WebScBureau bureau : bureaus) {
				String htmlDetail = "<a lay-event=\"detail" + bureau.getBureauId() + "\">\r\n" + 
						"		<i class=\"layui-icon layui-icon-form zadmin-oper-area zadmin-blue\" title=\"详情\"></i>\r\n" + 
						"	</a>";
				String htmlEdit = "<a lay-event=\"edit" + bureau.getBureauId() + "\">\r\n" + 
						"		<i class=\"zadmin-icon zadmin-icon-edit-square zadmin-oper-area zadmin-blue\" title=\"编辑\"></i>\r\n" + 
						"	</a>";
				String htmlDel = "<a lay-event=\"del" + bureau.getBureauId() + "\">\r\n" + 
						"		<i class=\"zadmin-icon zadmin-icon-delete zadmin-oper-area zadmin-red\" title=\"删除\"></i>\r\n" + 
						"	</a>";
				if (show) {
					bureau.setOperator(htmlDetail + htmlEdit + htmlDel);
				} else {
					bureau.setOperator(htmlDetail);
				}
				if (bureau.getProvince() != null) {
					bureau.setProvinceName(district.getDistrictMap().get(bureau.getProvince()).getName());
				}
				if (bureau.getCity() != null) {
					bureau.setCityName(district.getDistrictMap().get(bureau.getCity()).getName());
				}
				if (bureau.getArea() != null) {
					bureau.setAreaName(district.getDistrictMap().get(bureau.getArea()).getName());
				}
				if (show) {
					bureau.setOperator(htmlDetail + htmlEdit + htmlDel);
				} else {
					bureau.setOperator(htmlDetail);
				}
				if (bureau.getChildren().size() > 0) {
					getDistName(bureau.getChildren(), show);
				}
			}
		}
		return bureaus;
	}
	
	/**
	 * 根据当前用户获取带有医疗集团根节点的行政区划（树）
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
				provinceDist.setInstitution(bureaus.get(0).getBureauId());
				dists.add(superDist);
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
					cityDist.setInstitution(user.getRoleTypeId());
					dists.add(provinceDist);
				} else if (user.getCity() != null) {
					provinceDist.setId(user.getProvince());
					provinceDist.setParentId(superBureau.getBureauId());
					provinceDist.setName(district.getDistrictMap().get(user.getProvince()).getName());
					cityDist.setInstitution(user.getRoleTypeId());
					dists.add(provinceDist);
				}
				// 添加医疗机构根节点
				superDist.setId(superBureau.getBureauId());
				superDist.setParentId(superBureau.getBureauPid());
				superDist.setName(superBureau.getBureauName());
				cityDist.setInstitution(user.getRoleTypeId());
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
}
