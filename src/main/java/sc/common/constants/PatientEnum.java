package sc.common.constants;

/**
 * 病人类型
 * @author aisino
 *
 */
public enum PatientEnum {

	CHILD(1, "儿童"),
	YOUNG(2, "青年"),
	OLD(3, "老年");
	
	private int code;
	private String type;
	private PatientEnum(int code, String type) {
		this.code = code;
		this.type = type;
	}
	
	public static PatientEnum valueOf(int code) {
		for(PatientEnum pe : PatientEnum.values()){
			if(pe.code == code)
				return pe;
		}
		return null;
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
}
