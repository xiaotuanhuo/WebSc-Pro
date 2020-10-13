package sc.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import sc.system.model.WebScOperative;

@Mapper
public interface OperativeMapper {
	@Select("SELECT * FROM WSC_OPERATIVE WHERE operative_id = #{operativeId} limit 1")
	WebScOperative selectOperativeById(@Param("operativeId") String operativeId);
	
	@Select("SELECT * FROM WSC_OPERATIVE WHERE operative_name = #{operativeName} limit 1")
	WebScOperative selectOperative(@Param("operativeName") String operativeName);
	
	List<WebScOperative> getWebScOperativeList(String operativeName);
	
	/**
	 * 分页条件查询
	 * @param query
	 * @return
	 */
	List<WebScOperative> selectOnPage(@Param("operative") WebScOperative query);
	
	int insert(WebScOperative record);
	
	int updateByPrimaryKey(WebScOperative record);
	
	int deleteByPrimaryKey(String operativeId);
}
