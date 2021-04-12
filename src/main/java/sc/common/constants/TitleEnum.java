package sc.common.constants;

/**
 * 职称枚举
 * @author aisino
 *
 */
public enum TitleEnum {
	ZYYS("11", "执业医师"),
	ZZYS("12", "主治医师"),
	FZRYS("13", "副主任医师"),
	ZRYS("14", "主任医师"),
	XHS("21", "护士"),
	DHS("22", "护师"),
	ZGHS("23", "主管护师"),
	FZRHS("24", "副主任护师"),
	ZRHS("25", "主任护师");
	
	private String value;
	private String desc;
	
	private TitleEnum(String value, String desc) {
		this.value = value;
		this.desc = desc;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public String toString() {
		return "{[value:"+value+"],[desc:"+desc+"]}";
	}
	
	public static TitleEnum getValueOf(String value){
		if(value!=null){
			for(TitleEnum te : TitleEnum.values()){
				if(value.equals(te.getValue())){
					return te;
				}
			}
		}
		return null;
	}
}
