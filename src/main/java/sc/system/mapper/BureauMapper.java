package sc.system.mapper;

import java.util.List;

import sc.system.model.WebScBureau;

public interface BureauMapper {
	int deleteByPrimaryKey(String bureauId);
	
	int insert(WebScBureau record);
	
	int insertSelective(WebScBureau record);
	
	WebScBureau selectByPrimaryKey(String bureauId);
	
	List<WebScBureau> selectAllTree();
	
	int updateByPrimaryKeySelective(WebScBureau record);
	
	int updateByPrimaryKey(WebScBureau record);
}