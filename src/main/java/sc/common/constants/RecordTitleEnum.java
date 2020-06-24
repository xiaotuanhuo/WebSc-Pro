package sc.common.constants;

/**
 * 医生报备标题列
 * @author pp
 *
 */
public enum RecordTitleEnum {
	DOCTOR_NAME("0", "医生姓名"),
	DOCTOR_PHONE("1", "医生手机号"),
	ORG_NAME("2", "医疗机构名称"),
	RECORD_DATE("3", "备案日期"),
	END_DATE("4", "截止日期");
	
	private String value;
	private String txt;
	
	private RecordTitleEnum(String value, String txt) {
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
	
	public static RecordTitleEnum getvalueOf(String value){
		if(value!=null){
			for(RecordTitleEnum recordTitleEnum:RecordTitleEnum.values()){
				if(value.equals(recordTitleEnum.getValue())){
					return recordTitleEnum;
				}
			}
		}
		return null;
	}
	
	public static RecordTitleEnum txtOf(String txt){
		if(txt!=null){
			for(RecordTitleEnum recordTitleEnum:RecordTitleEnum.values()){
				if(txt.equalsIgnoreCase(recordTitleEnum.getTxt())){
					return recordTitleEnum;
				}
			}
		}
		return null;
	}
	public static RecordTitleEnum nameOf(String name){
		for(RecordTitleEnum recordTitleEnum: RecordTitleEnum.values()){
			if(recordTitleEnum.name().equals(name)){
				return recordTitleEnum;
			}
		}
		return null;
	}
}
