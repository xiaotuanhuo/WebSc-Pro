package sc.common.constants;

public enum DistType {

	STATE(1, "xtgly"),		// 国
	PROVINCE(2, "yljggly"),	// 省
	CITY(3, "wjjgly"),		// 市
	COUNTY(4, "qygly");		// 区/县
	
	private int code;
	private String type;
	
	private DistType(int code, String type) {
		this.code = code;
		this.type = type;
	}
	
	public static DistType valueOf(int code) {
		for(DistType dt : DistType.values()){
			if(dt.code == code)
				return dt;
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
