package sc.system.service;

import sc.common.constants.DistType;
import sc.common.constants.RoleEnum;
import sc.common.exception.DuplicateDistrictException;
import sc.common.util.UUID19;
import sc.system.mapper.DeptMapper;
import sc.system.model.WebScDept;
import sc.system.model.WebScUser;
import sc.system.model.vo.DistTree;
import sc.system.model.vo.District;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.org.apache.xpath.internal.operations.And;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeptService {
	private static final Logger log = LoggerFactory.getLogger(DeptService.class);

	@Resource
	private DeptMapper deptMapper;
	
	@Autowired
	private District district;
	
	public String add(WebScDept dept) {
		checkDeptExistOnCreate(dept.getParentId(), dept.getProvince(), dept.getCity());
		dept.setDeptId(UUID19.uuid());
		deptMapper.insert(dept);
		return dept.getDeptId();
	}
	
	public WebScDept getDeptInfo(String deptId) {
		WebScDept dept = deptMapper.selectByPrimaryKey(deptId);
		if (dept.getProvince() != null) {
			dept.setProvinceName(district.getDistrictMap().get(dept.getProvince()).getName());
		}
		if (dept.getCity() != null) {
			dept.setCityName(district.getDistrictMap().get(dept.getCity()).getName());
		}
		return dept;
	}
	
	/**
	 * 查询医疗集团根节点
	 * @return
	 */
	public List<WebScDept> getRootTree() {
		// 医疗集团根节点的父节点默认：0
		return deptMapper.selectByParentId("0");
	}
	
	/**
	 * 查询当前用户医疗集团节点及其子孙节点（列表）
	 * @return
	 */
	public List<WebScDept> getTreeList() {
		List<WebScDept> depts = new ArrayList<WebScDept>();
		WebScDept dept = new WebScDept();
		Subject subject = SecurityUtils.getSubject();
		WebScUser user = (WebScUser) subject.getPrincipal();
		switch (RoleEnum.valueOf(Integer.parseInt(user.getRoleId()))) {
			case XTGLY:
				// 系统管理员查询所有
				List<WebScDept> rootDepts = deptMapper.selectByParentId("0");
				for (WebScDept rootDept : rootDepts) {
					depts.add(rootDept);
					depts = getSubList(depts, rootDept);
				}
				break;
			case CJGLY:
			case QYGLY:
				dept = deptMapper.selectByPrimaryKey(user.getRoleTypeId());
				depts.add(dept);
				depts = getSubList(depts, dept);
				break;
			default:
				break;
		}
		if (depts.size() > 0) {
			for (WebScDept webScDept : depts) {
				if (webScDept.getProvince() != null) {
					webScDept.setProvinceName(district.getDistrictMap().get(webScDept.getProvince()).getName());
				}
				if (webScDept.getCity() != null) {
					webScDept.setCityName(district.getDistrictMap().get(webScDept.getCity()).getName());
				}
			}
		}
		return depts;
	}
	
	/**
	 * 根据当前医疗集团id递归查询子孙节点（列表）
	 * @param depts
	 * @param dept
	 * @return
	 */
	private List<WebScDept> getSubList(List<WebScDept> depts, WebScDept dept) {
		List<WebScDept> subDepts = deptMapper.selectByParentId(dept.getDeptId());
		if (subDepts.size() > 0) {
			for (WebScDept subDept : subDepts) {
				depts.add(subDept);
				getSubList(depts, subDept);
			}
		}
		return depts;
	}
	
	/**
	 * 查询当前节点及其子孙节点（树形）
	 * @param deptId
	 * @return
	 */
	public List<WebScDept> getTree(String deptId) {
		Subject subject = SecurityUtils.getSubject();
		WebScUser user = (WebScUser) subject.getPrincipal();
//		List<WebScDept> depts = deptMapper.selectTree(user.getRoleTypeId());
		
		List<WebScDept> depts = deptMapper.selectTree(deptId == null ? user.getRoleTypeId() : deptId);
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
		return getDistName(depts, show);
	}
	
	/**
	 * 查询当前节点及其子孙节点（树形）
	 * 用于处理仅city节点可选中
	 * @param deptId
	 * @return
	 */
	public List<WebScDept> getCityTree() {
		Subject subject = SecurityUtils.getSubject();
		WebScUser user = (WebScUser) subject.getPrincipal();
		List<WebScDept> depts = deptMapper.selectTree(user.getRoleTypeId());
		return getCityTree(depts);
	}
	
	private List<WebScDept> getCityTree(List<WebScDept> depts) {
		if (depts.size() > 0) {
			for (WebScDept dept : depts) {
				if (dept.getCity() == null || dept.getCity().equals("")) {
					dept.setDisabled(true);
				}
				if (dept.getChildren().size() > 0) {
					getCityTree(dept.getChildren());
				}
			}
		}
		return depts;
	}
	
	/**
	 * 查询当前节点的子孙节点（树形）
	 * @param parentId
	 * @return
	 */

	public List<WebScDept> getSubTree(String parentId) {
//		Subject subject = SecurityUtils.getSubject();
//		WebScUser user = (WebScUser) subject.getPrincipal();
		return deptMapper.selectSubTree(parentId);
	}
	
	public List<WebScDept> getUnleafTree() {
		Subject subject = SecurityUtils.getSubject();
		WebScUser user = (WebScUser) subject.getPrincipal();
		return deptMapper.selectUnleafTree(user.getRoleTypeId());
	}
	
	/**
	 * 校验当前父节点下是否已存在集团
	 * @param parentId
	 */
	public void checkDeptExistOnCreate(String parentId, String province, String city) {
		WebScDept dept = deptMapper.selectByParentIdAndDistrict(parentId, province, city);
		if (dept != null) {
			throw new DuplicateDistrictException();
		}
	}
	
	/**
	 * 递归获取集团所属行政区划的区划名称
	 * @param depts
	 * @return
	 */
	private List<WebScDept> getDistName(List<WebScDept> depts, boolean show) {
		if (depts.size() > 0) {
			for (WebScDept dept : depts) {
				String htmlDetail = "<a lay-event=\"detail" + dept.getDeptId() + "\">\r\n" + 
						"		<i class=\"layui-icon layui-icon-form zadmin-oper-area zadmin-blue\" title=\"详情\"></i>\r\n" + 
						"	</a>";
				String htmlEdit = "<a lay-event=\"edit" + dept.getDeptId() + "\">\r\n" + 
						"		<i class=\"zadmin-icon zadmin-icon-edit-square zadmin-oper-area zadmin-blue\" title=\"编辑\"></i>\r\n" + 
						"	</a>";
				String htmlDel = "<a lay-event=\"del" + dept.getDeptId() + "\">\r\n" + 
						"		<i class=\"zadmin-icon zadmin-icon-delete zadmin-oper-area zadmin-red\" title=\"删除\"></i>\r\n" + 
						"	</a>";
				if (show) {
					dept.setOperator(htmlDetail + htmlEdit + htmlDel);
				} else {
					dept.setOperator(htmlDetail);
				}
				if (dept.getProvince() != null) {
					dept.setProvinceName(district.getDistrictMap().get(dept.getProvince()).getName());
				}
				if (dept.getCity() != null) {
					dept.setCityName(district.getDistrictMap().get(dept.getCity()).getName());
				}
				if (dept.getChildren().size() > 0) {
					getDistName(dept.getChildren(), show);
				}
			}
		}
		return depts;
	}
	
	/**
	 * 根据当前用户获取带有医疗集团根节点的行政区划（树）
	 * @return
	 */
	public List<DistTree> getDistTree() {
		Subject subject = SecurityUtils.getSubject();
		WebScUser user = (WebScUser) subject.getPrincipal();
		List<DistTree> dists = new ArrayList<DistTree>();
		WebScDept superDept = deptMapper.selectSuperDept();	// 医疗机构根节点
		if (superDept == null) {
			return dists;
		}
		List<WebScDept> depts = deptMapper.selectTree(user.getRoleTypeId());
		if (depts.size() > 0) {
			DistTree superDist = new DistTree();
			DistTree provinceDist = new DistTree();
			switch (RoleEnum.valueOf(Integer.parseInt(user.getRoleId()))) {
				// 系统管理员 超级管理员添加医疗机构根节点
				case XTGLY:
					superDist.setId(superDept.getDeptId());
					superDist.setParentId(superDept.getParentId());
					superDist.setName(superDept.getDeptName());
					superDist.setInstitution(null);
					dists.add(superDist);
					break;
				case CJGLY:
					superDist.setId(superDept.getDeptId());
					superDist.setParentId(superDept.getParentId());
					superDist.setName(superDept.getDeptName());
					superDist.setInstitution(user.getRoleTypeId());
					dists.add(superDist);
					break;
				case QYGLY:
					// 区域管理员仅有省属和市属
					// 市属区域管理员添加以医疗机构根节点为父节点的省级节点
					if (user.getCity() != null) {
						provinceDist.setId(user.getProvince());
						provinceDist.setParentId(superDept.getDeptId());
						provinceDist.setName(district.getDistrictMap().get(user.getProvince()).getName());
						provinceDist.setInstitution(user.getRoleTypeId());
						dists.add(provinceDist);
					}
					// 添加医疗机构根节点
					superDist.setId(superDept.getDeptId());
					superDist.setParentId(superDept.getParentId());
					superDist.setName(superDept.getDeptName());
					superDist.setInstitution(user.getRoleTypeId());
					dists.add(superDist);
					break;
				default:
					break;
			}
			dists = convert(depts, dists);
		}
		return dists;
	}
	
	private List<DistTree> convert(List<WebScDept> depts, List<DistTree> dists) {
		if (depts.size() > 0) {
			for (WebScDept dept : depts) {
				DistTree dist = new DistTree();
				if (dept.getCity() != null) {
					dist.setId(dept.getCity());
					dist.setParentId(dept.getProvince());
					dist.setName(district.getDistrictMap().get(dept.getCity()).getName());
					dist.setInstitution(dept.getDeptId());
					dists.add(dist);
				} else if (dept.getProvince() != null) {
					dist.setId(dept.getProvince());
					dist.setParentId(dept.getParentId());
					dist.setName(district.getDistrictMap().get(dept.getProvince()).getName());
					dist.setInstitution(dept.getDeptId());
					dists.add(dist);
				}
				if (dept.getChildren().size() > 0) {
					convert(dept.getChildren(), dists);
				}
			}
		}
		return dists;
	}
}
