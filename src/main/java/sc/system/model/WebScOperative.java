package sc.system.model;

import java.io.Serializable;

public class WebScOperative implements Serializable {
	private static final long serialVersionUID = -491259598471802474L;
	
	private String operativeId;
	private String operativeName;
	private Integer urgenttime;
	private Integer pid;		// 所属手术大类
	private String typeName;	// 手术大类名称
	
	public String getOperativeId() {
		return operativeId;
	}
	
	public void setOperativeId(String operativeId) {
		this.operativeId = operativeId;
	}
	
	public String getOperativeName() {
		return operativeName;
	}
	
	public void setOperativeName(String operativeName) {
		this.operativeName = operativeName;
	}
	
	public Integer getUrgenttime() {
		return urgenttime;
	}
	
	public void setUrgenttime(Integer urgenttime) {
		this.urgenttime = urgenttime;
	}
	
	public Integer getPid() {
		return pid;
	}
	
	public void setPid(Integer pid) {
		this.pid = pid;
	}
	
	public String getTypeName() {
		return typeName;
	}
	
	public void setpTypeName(String typeName) {
		this.typeName = typeName;
	}
	
}
