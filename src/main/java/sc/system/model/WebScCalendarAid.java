package sc.system.model;

public class WebScCalendarAid extends WebScCalendar {
	private String orgName;
	private String doctorName;
	private String doctorPhone;
	private String calendarDate; //备休日
	private String calendarPeriod; //备休时间段
	
	public WebScCalendarAid() {
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getCalendarDate() {
		return calendarDate;
	}

	public void setCalendarDate(String calendarDate) {
		this.calendarDate = calendarDate;
	}

	public String getCalendarPeriod() {
		return calendarPeriod;
	}

	public void setCalendarPeriod(String calendarPeriod) {
		this.calendarPeriod = calendarPeriod;
	}

	public String getDoctorPhone() {
		return doctorPhone;
	}

	public void setDoctorPhone(String doctorPhone) {
		this.doctorPhone = doctorPhone;
	}

	@Override
	public String toString() {
		return "WebScCalendarAid [orgName=" + orgName + ", doctorName=" + doctorName + ", doctorPhone=" + doctorPhone
				+ ", calendarDate=" + calendarDate + ", calendatPeriod=" + calendarPeriod + ", toString()="
				+ super.toString() + "]";
	}
	
}
