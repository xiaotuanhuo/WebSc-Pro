package sc.common.constants;

/**
 * 性别
 * @author pp
 *
 */
public enum SexEnum {
	MALE("1", "男"),
	FEMALE("0", "女");
	
	private String value;
	private String txt;
	
	private SexEnum(String value, String txt) {
		this.value = value;
		this.txt = txt;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getTxt() {
		return txt;
	}
	
	public String toString() {
		return "{[value:"+value+"],[txt:"+txt+"]}";
	}
	
	public static SexEnum getvalueOf(String value){
		if(value!=null){
			for(SexEnum sexEnum:SexEnum.values()){
				if(value.equals(sexEnum.getValue())){
					return sexEnum;
				}
			}
		}
		return null;
	}
	
	public static SexEnum txtOf(String txt){
		if(txt!=null){
			for(SexEnum sexEnum:SexEnum.values()){
				if(txt.equalsIgnoreCase(sexEnum.getTxt())){
					return sexEnum;
				}
			}
		}
		return null;
	}
	public static SexEnum nameOf(String name){
		for(SexEnum sexEnum: SexEnum.values()){
			if(sexEnum.name().equals(name)){
				return sexEnum;
			}
		}
		return null;
	}
}
