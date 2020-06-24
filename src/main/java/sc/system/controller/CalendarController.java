package sc.system.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import sc.common.util.PageResultBean;
import sc.system.model.WebScCalendarAid;
import sc.system.service.CalendarService;

@Controller
@RequestMapping(value = "calendar")
public class CalendarController {
	private static final Logger logger = LoggerFactory.getLogger(CalendarController.class);
	
	@Resource
    private CalendarService calendarService;
	
	@GetMapping(value = "toAreaCalendarShow")
	public String toAreaCalendarShow() {
		return "calendar/area-calendar-show";
	}
	
	@PostMapping(value = "getDoctorCalendars")
	@ResponseBody
	public PageResultBean<WebScCalendarAid> getDoctorCalendars(
			@RequestBody Map<String, Object> paraMap) {
		PageResultBean<WebScCalendarAid> prb = new PageResultBean<WebScCalendarAid>();
		try {
			
			prb = calendarService.getDoctorCalendarsService(paraMap);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取医生报休日期失败", e.getMessage());
		}
		
		return prb;
	}
	
}
