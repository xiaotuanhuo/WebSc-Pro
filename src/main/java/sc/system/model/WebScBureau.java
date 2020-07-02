package sc.system.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WebScBureau {
	@JsonProperty("id")
	private String bureauId;
	
	@JsonProperty("name")
	private String bureauName;
	
	private String bureauPid;
	private String bureauAddress;
	private String bureauTel;
	private String leaderName;
	private String leaderTel;
	private String province;
	private String city;
	private String area;
	private Date createDate;
	private String status;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<WebScBureau> children;
    
	public String getBureauId() {
		return bureauId;
	}
	
	public void setBureauId(String bureauId) {
		this.bureauId = bureauId == null ? null : bureauId.trim();
	}
	
	public String getBureauPid() {
		return bureauPid;
	}
	
	public void setBureauPid(String bureauPid) {
		this.bureauPid = bureauPid == null ? null : bureauPid.trim();
	}
	
	public String getBureauName() {
		return bureauName;
	}
	
	public void setBureauName(String bureauName) {
		this.bureauName = bureauName == null ? null : bureauName.trim();
	}
	
	public String getBureauAddress() {
		return bureauAddress;
	}
	
	public void setBureauAddress(String bureauAddress) {
		this.bureauAddress = bureauAddress == null ? null : bureauAddress.trim();
	}
	
	public String getBureauTel() {
		return bureauTel;
	}
	
	public void setBureauTel(String bureauTel) {
		this.bureauTel = bureauTel == null ? null : bureauTel.trim();
	}
	
	public String getLeaderName() {
		return leaderName;
	}
	
	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName == null ? null : leaderName.trim();
	}
	
	public String getLeaderTel() {
		return leaderTel;
	}
	
	public void setLeaderTel(String leaderTel) {
		this.leaderTel = leaderTel == null ? null : leaderTel.trim();
	}
	
	public String getProvince() {
		return province;
	}
	
	public void setProvince(String province) {
		this.province = province == null ? null : province.trim();
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city == null ? null : city.trim();
	}
	
	public String getArea() {
		return area;
	}
	
	public void setArea(String area) {
		this.area = area == null ? null : area.trim();
	}
	
	public Date getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status == null ? null : status.trim();
	}
	
	public List<WebScBureau> getChildren() {
		return children;
	}
	
	public void setChildren(List<WebScBureau> children) {
		this.children = children;
	}
	
}