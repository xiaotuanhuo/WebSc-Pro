package sc.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import sc.system.model.WebScCalendar;
import sc.system.model.WebScCalendarAid;

public interface WebScCalendarMapper {
	
	@Select("SELECT * FROM WSC_CALENDAR WHERE user_id = #{doctorId}")
	List<WebScCalendar> selectCalendarsByDoctor(@Param("doctorId") String doctorId);
	
	/**
	 * 根据可变条件查询医生备休情况
	 * @param paraMap
	 * @return
	 */
	List<WebScCalendarAid> selectWebScCalendarAidsByConditions(Map<String, Object> paraMap);
	
    int insert(WebScCalendar record);

    int insertSelective(WebScCalendar record);
    
    int updateByPrimaryKey(WebScCalendar record);
    
    @Delete("DELETE FROM WSC_CALENDAR WHERE calendar_id = #{calendarId}")
    int deleteByPrimaryKey(@Param("calendarId") String calendarId);
}