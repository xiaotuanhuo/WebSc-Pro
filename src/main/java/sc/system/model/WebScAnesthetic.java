package sc.system.model;

import java.io.Serializable;

public class WebScAnesthetic implements Serializable {

	private static final long serialVersionUID = 4063115647173007422L;
	
	private String anestheticId;
	private String anestheticName;
	
	public String getAnestheticId() {
		return anestheticId;
	}
	public void setAnestheticId(String anestheticId) {
		this.anestheticId = anestheticId;
	}
	public String getAnestheticName() {
		return anestheticName;
	}
	public void setAnestheticName(String anestheticName) {
		this.anestheticName = anestheticName;
	}
}
