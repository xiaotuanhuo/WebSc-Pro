package sc.system.mapper;

import java.util.List;
import java.util.Map;

import sc.system.model.WebScCalendar;
import sc.system.model.WebScCalendarAid;

public interface WebScCalendarMapper {
	
	/**
	 * 根据可变条件查询医生备休情况
	 * @param paraMap
	 * @return
	 */
	List<WebScCalendarAid> selectWebScCalendarAidsByConditions(Map<String, Object> paraMap);
	
    int insert(WebScCalendar record);

    int insertSelective(WebScCalendar record);
}