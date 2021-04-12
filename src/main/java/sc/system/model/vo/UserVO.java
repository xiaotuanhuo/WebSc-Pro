package sc.system.model.vo;

import sc.common.constants.RoleEnum;

/**
 * 用户管理查询辅助类
 * @author aisino
 *
 */
public class UserVO {

	private String userId;				// 当前用户id
	private String role;			// 当前用户角色
	private String adminRoleId;		// 系统管理员角色id
	private String superRoleId;		// 超级管理员角色id
	private String deptRoleId;		// 区域订单管理员
	private String bureauRoleId;	// 卫监局管理员角色id
	private String organizationRoleId;	// 医疗机构管理员角色id
	private String orgDocRoleId;		// 医疗机构订单录入员
	private String province;		// 当前用户所属省
	private String city;			// 当前用户所属市
	private String area;			// 当前用户所属区/县
	
	private String organizationId;	// 医疗机构管理员角色时的医疗机构id
	private String rootId;			// 上述医疗机构的根节点
	private int leaf;				// 上述医疗机构是否是叶子节点 1是 0否
	
	private String name;			// 用户姓名（前台查询条件）
	private String searchRole;		// 角色（前台查询条件）
	
	public UserVO() {
		this.adminRoleId = RoleEnum.XTGLY.getCode() + "";
		this.superRoleId = RoleEnum.CJGLY.getCode() + "";
		this.deptRoleId = RoleEnum.QYGLY.getCode() + "";
		this.bureauRoleId = RoleEnum.WJJGLY.getCode() + "";
		this.organizationRoleId = RoleEnum.YLJGGLY.getCode() + "";
		this.orgDocRoleId = RoleEnum.JGDDLRY.getCode() + "";
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public String getAdminRoleId() {
		return adminRoleId;
	}
	
	public void setAdminRoleId(String adminRoleId) {
		this.adminRoleId = adminRoleId;
	}
	
	public String getSuperRoleId() {
		return superRoleId;
	}
	
	public void setSuperRoleId(String superRoleId) {
		this.superRoleId = superRoleId;
	}
	
	public String getDeptRoleId() {
		return deptRoleId;
	}
	
	public void setDeptRoleId(String deptRoleId) {
		this.deptRoleId = deptRoleId;
	}
	
	public String getBureauRoleId() {
		return bureauRoleId;
	}
	
	public void setBureauRoleId(String bureauRoleId) {
		this.bureauRoleId = bureauRoleId;
	}
	
	public String getOrganizationRoleId() {
		return organizationRoleId;
	}
	
	public void setOrganizationRoleId(String organizationRoleId) {
		this.organizationRoleId = organizationRoleId;
	}
	
	public String getOrgDocRoleId() {
		return orgDocRoleId;
	}
	
	public void setOrgDocRoleId(String orgDocRoleId) {
		this.orgDocRoleId = orgDocRoleId;
	}
	
	public String getProvince() {
		return province;
	}
	
	public void setProvince(String province) {
		this.province = province;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getArea() {
		return area;
	}
	
	public void setArea(String area) {
		this.area = area;
	}
	
	public String getOrganizationId() {
		return organizationId;
	}
	
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	
	public String getRootId() {
		return rootId;
	}
	
	public void setRootId(String rootId) {
		this.rootId = rootId;
	}
	
	public int getLeaf() {
		return leaf;
	}
	
	public void setLeaf(int leaf) {
		this.leaf = leaf;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSearchRole() {
		return searchRole;
	}
	
	public void setSearchRole(String searchRole) {
		this.searchRole = searchRole;
	}
	
}
