package sc.common.constants;

public enum DayPeriodEnum {
	AM("0-12", "上午"),
	PM("12-23", "下午"),
	ALLDAY("0-23", "全天");
	
	private String value;
	private String txt;
	
	private DayPeriodEnum(String value, String txt) {
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
	
	public static DayPeriodEnum getvalueOf(String value){
		if(value!=null){
			for(DayPeriodEnum dayPeriodEnum:DayPeriodEnum.values()){
				if(value.equals(dayPeriodEnum.getValue())){
					return dayPeriodEnum;
				}
			}
		}
		return null;
	}
	
	public static DayPeriodEnum txtOf(String txt){
		if(txt!=null){
			for(DayPeriodEnum dayPeriodEnum:DayPeriodEnum.values()){
				if(txt.equalsIgnoreCase(dayPeriodEnum.getTxt())){
					return dayPeriodEnum;
				}
			}
		}
		return null;
	}
	public static DayPeriodEnum nameOf(String name){
		for(DayPeriodEnum dayPeriodEnum: DayPeriodEnum.values()){
			if(dayPeriodEnum.name().equals(name)){
				return dayPeriodEnum;
			}
		}
		return null;
	}
}
