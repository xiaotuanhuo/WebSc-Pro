package sc.system.mapper;

import java.util.List;

import sc.system.model.WebScLabel;
import sc.system.model.vo.Vote;

public interface WebScLabelMapper {
	int deleteByPrimaryKey(String labelId);

	int insert(WebScLabel record);

	int insertSelective(WebScLabel record);

	WebScLabel selectByPrimaryKey(String labelId);

	List<Vote> selectVote();
	
	int updateByPrimaryKeySelective(WebScLabel record);

	int updateByPrimaryKey(WebScLabel record);
	
}