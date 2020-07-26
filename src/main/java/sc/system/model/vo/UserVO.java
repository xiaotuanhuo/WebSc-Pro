package sc.system.model.vo;

import sc.common.constants.RoleEnum;

/**
 * 用户管理查询辅助类
 * @author aisino
 *
 */
public class UserVO {

	private int userId;				// 当前用户id
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
	
	private String name;			// 用户姓名（前台查询条件）
	private String phone;			// 手机号（前台查询条件）
	
	public UserVO() {
		this.adminRoleId = RoleEnum.XTGLY.getCode() + "";
		this.superRoleId = RoleEnum.CJGLY.getCode() + "";
		this.deptRoleId = RoleEnum.QYGLY.getCode() + "";
		this.bureauRoleId = RoleEnum.WJJGLY.getCode() + "";
		this.organizationRoleId = RoleEnum.YLJGGLY.getCode() + "";
		this.orgDocRoleId = RoleEnum.JGDDLRY.getCode() + "";
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
