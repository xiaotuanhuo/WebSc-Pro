package sc.common.constants;

/**
 * 订单导入模板标题
 * @author pp
 *
 */
public enum DocumentTitleEnum {
	patient_name("0", "患者姓名"),
	patient_age("1", "患者年龄"),
	patient_sex("2", "患者性别"),
	operative_name("3", "手术名称"),
	operate_date("4", "手术日期"),
	operate_time("5", "手术时间"),
	patient_num("6", "住院号"),
	patient_bednum("7", "病床号"),
	document_title("8", "诊断"),
	operate_user("9", "手术医生"),
	operate_aide("10", "手术助手"),
	anesthetic_name("11", "麻醉方法"),
	org_name("12", "医疗机构名称"),
	memo("13", "备注");
	
	private String value;
	private String txt;
	
	private DocumentTitleEnum(String value, String txt) {
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
	
	public static DocumentTitleEnum getvalueOf(String value){
		if(value!=null){
			for(DocumentTitleEnum documentTitleEnum:DocumentTitleEnum.values()){
				if(value.equals(documentTitleEnum.getValue())){
					return documentTitleEnum;
				}
			}
		}
		return null;
	}
	
	public static DocumentTitleEnum txtOf(String txt){
		if(txt!=null){
			for(DocumentTitleEnum documentTitleEnum:DocumentTitleEnum.values()){
				if(txt.equalsIgnoreCase(documentTitleEnum.getTxt())){
					return documentTitleEnum;
				}
			}
		}
		return null;
	}
	public static DocumentTitleEnum nameOf(String name){
		for(DocumentTitleEnum documentTitleEnum: DocumentTitleEnum.values()){
			if(documentTitleEnum.name().equals(name)){
				return documentTitleEnum;
			}
		}
		return null;
	}
}
