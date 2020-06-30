package sc.system.service;

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
import sc.system.model.WebScCalendarAid;
import sc.system.model.WebScUser;

@Service
public class CalendarService {
	
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
			webScCalendarAid.setCalendarPeriod(
					DateUtils.getHour(webScCalendarAid.getStartTime())
					+"-"+
					DateUtils.getHour(webScCalendarAid.getEndTime()));
		}
		
		PageInfo<WebScCalendarAid> pageInfo = new PageInfo<>(webScCalendarAids);
		
		prb.setCount(Long.valueOf(pageInfo.getTotal()).intValue());
		prb.setData(webScCalendarAids);
		
		return prb;
	}

}
