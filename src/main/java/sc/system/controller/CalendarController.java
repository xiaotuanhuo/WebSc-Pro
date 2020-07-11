package sc.system.controller;

import java.util.List;
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
import sc.common.util.ResultBean;
import sc.system.model.WebScCalendarAid;
import sc.system.model.vo.CalendarEventVO;
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
	
	@GetMapping(value = "toDoctorCalendar")
	public String toDoctorCalendar() {
		return "calendar/doctor-calendar";
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
			logger.error("获取医生报休日期失败，"+e.getMessage());
		}
		
		return prb;
	}
	
	@GetMapping(value = "getDoctorCalendarEvents")
	@ResponseBody
	public ResultBean getDoctorCalendarEvents() {
		
		ResultBean resultBean = null;
		try {
			List<CalendarEventVO> calendarEventVOs = calendarService.getDoctorCalendarEventsService();
			
			resultBean = ResultBean.success(calendarEventVOs);
		} catch (Exception e) {
			resultBean = ResultBean.error("获取备休日记录失败，"+e.getMessage());
			logger.error("获取备休日记录失败，"+e.getMessage());
		}
		
		return resultBean;
	}
	
	@PostMapping(value = "addCalendar")
	@ResponseBody
	public ResultBean addCalendar(@RequestBody CalendarEventVO calendarEventVO) {
		ResultBean resultBean = null;
		try {
			
			resultBean = ResultBean.success(calendarService.addCalendarService(calendarEventVO));
			
		} catch (Exception e) {
			resultBean = ResultBean.error("添加备休日失败，"+e.getMessage());
			logger.error("添加备休日失败，"+e.getMessage());
		}
		
		return resultBean;
	}
	
	@PostMapping(value = "updCalendar")
	@ResponseBody
	public ResultBean updCalendar(@RequestBody CalendarEventVO calendarEventVO) {
		ResultBean resultBean = null;
		try {
			
			resultBean = ResultBean.success(calendarService.updCalendarService(calendarEventVO));
			
		} catch (Exception e) {
			resultBean = ResultBean.error("修改备休日记录失败，"+e.getMessage());
			logger.error("修改备休日记录失败，"+e.getMessage());
		}
		
		return resultBean;
	}
	
	@PostMapping(value = "delCalendar")
	@ResponseBody
	public ResultBean delCalendar(@RequestBody Map<String, String> paraMap) {
		ResultBean resultBean = null;
		try {
			
			resultBean = ResultBean.success(
					calendarService.delCalendarService(paraMap.get("calendarId")));
			
		} catch (Exception e) {
			resultBean = ResultBean.error("修改备休日记录失败，"+e.getMessage());
			logger.error("修改备休日记录失败，"+e.getMessage());
		}
		
		return resultBean;
	}
	
}
