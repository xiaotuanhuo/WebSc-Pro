package sc.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import sc.system.model.WebScOperative;

@Mapper
public interface OperativeMapper {
	@Select(value = "SELECT * FROM WSC_OPERATIVE")
	List<WebScOperative> getWebScOperatives();
	
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
	
	/**
     * 统计已经有几个此手术名称, 用来检测是否重复.
     */
    int countByName(@Param("operativeName") String operativeName);

    /**
     * 统计已经有几个此手术名称, 用来检测是否重复 (不包含某当前id).
     */
    int countByNameNotIncludeId(@Param("operativeName") String operativeName, @Param("operativeId") String operativeId);
}
