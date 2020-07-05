package sc.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import sc.system.model.WebScBureau;

public interface BureauMapper {
	int deleteByPrimaryKey(String bureauId);
	
	int insert(WebScBureau record);
	
	int insertSelective(WebScBureau record);
	
	WebScBureau selectByPrimaryKey(String bureauId);
	
	/**
     * 查询当前节点及其子节点
     * @param bureauId
     * @return
     */
	List<WebScBureau> selectAllTree(@Param("province_code") String privince, @Param("city_code") String city);
	
	int updateByPrimaryKeySelective(WebScBureau record);
	
	int updateByPrimaryKey(WebScBureau record);
}