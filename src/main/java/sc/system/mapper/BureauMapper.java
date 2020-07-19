package sc.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import sc.system.model.WebScBureau;

public interface BureauMapper {
	int deleteByPrimaryKey(String bureauId);
	
	int insert(WebScBureau record);
	
	int insertSelective(WebScBureau record);
	
	WebScBureau selectByPrimaryKey(String bureauId);
	
	WebScBureau selectSuperBureau();
	
	/**
     * 查询当前节点及其子节点
     * @param bureauId
     * @return
     */
	List<WebScBureau> selectTree(@Param("bureau_id") String bureau_id, @Param("province_code") String privince,
			@Param("city_code") String city, @Param("area_code") String area);
	
	int updateByPrimaryKeySelective(WebScBureau record);
	
	int updateByPrimaryKey(WebScBureau record);
}