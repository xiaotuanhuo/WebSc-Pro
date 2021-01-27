package sc.system.model;

import java.util.Date;

public class WebScCalendar {
    private String calendarId;

    private String userId;

    private String title;

    private Date startTime;

    private Date endTime;

    private String memo;

    public String getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
    }

	@Override
	public String toString() {
		return "WebScCalendar [calendarId=" + calendarId + ", userId=" + userId + ", title=" + title + ", startTime="
				+ startTime + ", endTime=" + endTime + ", memo=" + memo + "]";
	}
    
}