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
	private String city;
	private String area;
	
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
	
	@Override
	public String toString() {
		return "Dept{" + "deptId=" + deptId + ", deptName='" + deptName + '\'' + ", parentId=" + parentId
				+ ", orderNum=" + orderNum + ", createTime=" + createTime + ", modifyTime=" + modifyTime + ", children="
				+ children + ", province=" + province + ", city=" + city + ", area=" + area + '}';
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
	
}