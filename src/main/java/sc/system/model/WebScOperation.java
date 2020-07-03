package sc.system.model;

public class WebScOperation {
	private String operationtypeId;
	private String operationtypeName;
	
	public String getOperationtypeId() {
		return operationtypeId;
	}
	
	public void setOperationtypeId(String operationtypeId) {
		this.operationtypeId = operationtypeId == null ? null : operationtypeId.trim();
	}
	
	public String getOperationtypeName() {
		return operationtypeName;
	}
	
	public void setOperationtypeName(String operationtypeName) {
		this.operationtypeName = operationtypeName == null ? null : operationtypeName.trim();
	}
}