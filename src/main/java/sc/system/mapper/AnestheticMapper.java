package sc.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import sc.system.model.WebScAnesthetic;

@Mapper
public interface AnestheticMapper {
	@Select(value = "SELECT * FROM WSC_ANESTHETIC")
	List<WebScAnesthetic> getWebScAnesthetics();
	
	@Select("SELECT * FROM WSC_ANESTHETIC WHERE anesthetic_id = #{anestheticId} limit 1")
	WebScAnesthetic selectAnestheticById(@Param("anestheticId") String anestheticId);
	
	@Select("SELECT * FROM WSC_ANESTHETIC WHERE anesthetic_name = #{anestheticName} limit 1")
	WebScAnesthetic selectAnesthetic(@Param("anestheticName") String anestheticName);
	
	public List<WebScAnesthetic> getWebScAnestheticList(String anestheticName);
	
	/**
	 * 分页条件查询
	 * @param query
	 * @return
	 */
	List<WebScAnesthetic> selectOnPage(@Param("anesthetic") WebScAnesthetic query);
	
	int insert(WebScAnesthetic record);
	
	int updateByPrimaryKey(WebScAnesthetic record);
	
	int deleteByPrimaryKey(String anestheticId);
	
	/**
     * 统计已经有几个此麻醉名称, 用来检测是否重复.
     */
    int countByName(@Param("anestheticName") String anestheticName);

    /**
     * 统计已经有几个此麻醉名称, 用来检测是否重复 (不包含某当前id).
     */
    int countByNameNotIncludeId(@Param("anestheticName") String anestheticName, @Param("anestheticId") String anestheticId);
}
