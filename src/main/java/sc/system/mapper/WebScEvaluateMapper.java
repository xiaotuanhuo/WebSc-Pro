package sc.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import sc.system.model.WebScEvaluate;

public interface WebScEvaluateMapper {
	int deleteByPrimaryKey(String documentId);
	int insert(WebScEvaluate record);
	int insertSelective(WebScEvaluate record);
	WebScEvaluate selectByPrimaryKey(String documentId);
	int updateByPrimaryKeySelective(WebScEvaluate record);
	int updateByPrimaryKey(WebScEvaluate record);
	
	String selectLabels(@Param("userId") String userId);
}