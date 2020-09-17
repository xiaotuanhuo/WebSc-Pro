package sc.system.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import sc.common.util.DateUtils;
import sc.common.util.PageResultBean;
import sc.common.util.ShiroUtil;
import sc.common.util.StringUtil;
import sc.system.mapper.WebScCalendarMapper;
import sc.system.model.WebScCalendar;
import sc.system.model.WebScCalendarAid;
import sc.system.model.WebScUser;
import sc.system.model.vo.CalendarEventVO;

@Service
public class CalendarService {
	
	private final static String allDayPeriod = "00:00:00 - 23:59:59";
	
	@Resource
    private WebScCalendarMapper webScCalendarMapper;
	
	public PageResultBean<WebScCalendarAid> getDoctorCalendarsService(Map<String, Object> paraMap) {
		
		PageResultBean<WebScCalendarAid> prb = new PageResultBean<WebScCalendarAid>();
		
		PageHelper.startPage(paraMap.get("page")==null?1:(int)paraMap.get("page"), 
				paraMap.get("limit")==null?10:(int)paraMap.get("limit"));
		
		WebScUser user = ShiroUtil.getCurrentUser();
		if (!StringUtil.isEmpty(user.getCity())) {
			paraMap.put("cityPre", user.getCity());
		}else {
			paraMap.put("cityPre", user.getProvince());
		}
		List<WebScCalendarAid> webScCalendarAids = webScCalendarMapper.selectWebScCalendarAidsByConditions(paraMap);
		
		for (WebScCalendarAid webScCalendarAid : webScCalendarAids) {
			webScCalendarAid.setCalendarDate(DateUtils.parseDateToStr("yyyy-MM-dd", 
					webScCalendarAid.getStartTime()));
			
			String timePeriod = 
					DateUtils.getTime(webScCalendarAid.getStartTime())
					+" - "+
					DateUtils.getTime(webScCalendarAid.getEndTime());
			
			webScCalendarAid.setCalendarPeriod(timePeriod);
		}
		
		PageInfo<WebScCalendarAid> pageInfo = new PageInfo<>(webScCalendarAids);
		
		prb.setCount(Long.valueOf(pageInfo.getTotal()).intValue());
		prb.setData(webScCalendarAids);
		
		return prb;
	}
	
	public List<CalendarEventVO> getDoctorCalendarEventsService(){
		
		WebScUser user = ShiroUtil.getCurrentUser();
		List<WebScCalendar> calendars = webScCalendarMapper.selectCalendarsByDoctor(user.getUserId());
		
		List<CalendarEventVO> calendarEventVOs = new ArrayList<CalendarEventVO>();
		for (WebScCalendar webScCalendar : calendars) {
			CalendarEventVO calendarEventVO = new CalendarEventVO();
			calendarEventVO.setId(webScCalendar.getCalendarId()+"");
			calendarEventVO.setTitle(webScCalendar.getTitle());
			calendarEventVO.setStart(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", 
					webScCalendar.getStartTime()));
			calendarEventVO.setEnd(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", 
					webScCalendar.getEndTime()));
			
			String timePeriod = 
			DateUtils.getTime(webScCalendar.getStartTime())
			+" - "+
			DateUtils.getTime(webScCalendar.getEndTime());
			
			calendarEventVO.setAllDay(
					timePeriod.equals(allDayPeriod) ? true : false);
			
			calendarEventVOs.add(calendarEventVO);
		}
		
		return calendarEventVOs;
	}
	
	public String addCalendarService(CalendarEventVO calendarEventVO){
		
		WebScCalendar calendar = new WebScCalendar();
		calendar.setCalendarId(calendarEventVO.getId());
		calendar.setTitle(calendarEventVO.getTitle());
		calendar.setStartTime(DateUtils.parseDate(calendarEventVO.getStart()));
		calendar.setEndTime(DateUtils.parseDate(calendarEventVO.getEnd()));
		WebScUser user = ShiroUtil.getCurrentUser();
		calendar.setUserId(user.getUserId());
		
		webScCalendarMapper.insert(calendar);
		
		return "备休记录添加成功";
	}
	
	public String updCalendarService(CalendarEventVO calendarEventVO){
		
		WebScCalendar calendar = new WebScCalendar();
		calendar.setCalendarId(calendarEventVO.getId());
		calendar.setTitle(calendarEventVO.getTitle());
		calendar.setStartTime(DateUtils.parseDate(calendarEventVO.getStart()));
		calendar.setEndTime(DateUtils.parseDate(calendarEventVO.getEnd()));
		WebScUser user = ShiroUtil.getCurrentUser();
		calendar.setUserId(user.getUserId());
		
		webScCalendarMapper.updateByPrimaryKey(calendar);
		
		return "备休记录修改成功";
	}
	
	public String delCalendarService(String calendarId){
		
		webScCalendarMapper.deleteByPrimaryKey(calendarId);
		
		return "备休记录删除成功";
	}

}
