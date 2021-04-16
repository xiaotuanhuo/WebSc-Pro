package sc.system.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Record {
    private Integer recordId;

    private String userId;

    private String orgId;

    private String doctorName;

    private String doctorPhone;

    private String doctorCity;

    private String orgName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date recordDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    private Date createDate;
    
    private int expireDays;

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId == null ? null : orgId.trim();
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName == null ? null : doctorName.trim();
    }

    public String getDoctorPhone() {
        return doctorPhone;
    }

    public void setDoctorPhone(String doctorPhone) {
        this.doctorPhone = doctorPhone == null ? null : doctorPhone.trim();
    }

    public String getDoctorCity() {
        return doctorCity;
    }

    public void setDoctorCity(String doctorCity) {
        this.doctorCity = doctorCity == null ? null : doctorCity.trim();
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName == null ? null : orgName.trim();
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

	public int getExpireDays() {
		return expireDays;
	}

	public void setExpireDays(int expireDays) {
		this.expireDays = expireDays;
	}

	@Override
	public String toString() {
		return "Record [recordId=" + recordId + ", userId=" + userId + ", orgId=" + orgId + ", doctorName=" + doctorName
				+ ", doctorPhone=" + doctorPhone + ", doctorCity=" + doctorCity + ", orgName=" + orgName
				+ ", recordDate=" + recordDate + ", endDate=" + endDate + ", createDate=" + createDate + ", expireDays="
				+ expireDays + "]";
	}
}