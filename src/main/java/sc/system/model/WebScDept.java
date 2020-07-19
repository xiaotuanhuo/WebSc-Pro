package sc.system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

// 医疗集团实体bean
public class WebScDept implements Serializable {

	private static final long serialVersionUID = -194076170058276436L;
	
	// 医疗集团id
	@JsonProperty("id")
	private String deptId;
	
	// 医疗集团名称
	@JsonProperty("name")
	private String deptName;
	
	// 上级医疗集团id
	private String parentId;
	
	private String deptAddress;
	
	private String deptTel;
	
	private String leaderName;
	
	private String leaderTel;
	
	// 排序
	private Integer orderNum;
	
	// 创建时间
	@JsonIgnore
	private Date createTime;
	
	// 修改时间
	@JsonIgnore
	private Date modifyTime;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<WebScDept> children;
	
	private String province;
	private String provinceName;
	private String city;
	private String cityName;
	
	private String operator;
	private String distName;	// 当前医疗集团行政区划名称
	private boolean disabled = false;	// 用于前端当前节点是否不可选中，默认fasle：可选中

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	
	public String getDeptName() {
		return deptName;
	}
	
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	
	public String getParentId() {
		return parentId;
	}
	
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public String getDeptAddress() {
		return deptAddress;
	}
	
	public void setDeptAddress(String deptAddress) {
		this.deptAddress = deptAddress;
	}

	public String getDeptTel() {
		return deptTel;
	}
	
	public void setDeptTel(String deptTel) {
		this.deptTel = deptTel;
	}
	
	public String getLeaderName() {
		return leaderName;
	}
	
	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}
	
	public String getLeaderTel() {
		return leaderTel;
	}
	
	public void setLeaderTel(String leaderTel) {
		this.leaderTel = leaderTel;
	}
	
	public Integer getOrderNum() {
		return orderNum;
	}
	
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}
	
	public Date getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public Date getModifyTime() {
		return modifyTime;
	}
	
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	public List<WebScDept> getChildren() {
		return children;
	}
	
	public void setChildren(List<WebScDept> children) {
		this.children = children;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	@Override
	public String toString() {
		return "Dept{" + "deptId=" + deptId + ", deptName='" + deptName + '\'' + ", parentId=" + parentId
				+ ", orderNum=" + orderNum + ", createTime=" + createTime + ", modifyTime=" + modifyTime + ", children="
				+ children + ", province=" + province + ", city=" + city + '}';
	}
	
	public String getProvince() {
		return province;
	}
	
	public void setProvince(String province) {
		this.province = province;
	}
	
	public String getProvinceName() {
		return provinceName;
	}
	
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getCityName() {
		return cityName;
	}
	
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	public String getDistName() {
		return distName;
	}
	
	public void setDistName(String distName) {
		this.distName = distName;
	}
	
	public boolean isDisabled() {
		return disabled;
	}
	
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
}