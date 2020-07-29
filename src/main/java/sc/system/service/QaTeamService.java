package sc.system.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sc.system.mapper.DocMapper;
import sc.system.mapper.QaTeamMapper;
import sc.system.model.WebScDoc;
import sc.system.model.WebScUser;

@Service
public class QaTeamService {
	private static final Logger log = LoggerFactory.getLogger(QaTeamService.class);
	
	@Resource
	private QaTeamMapper qaTeamMapper;
	
	@Resource
	private DocMapper docMapper;
	
	public List<WebScUser> getQaTeamInfo(String qaTeamId){
		return qaTeamMapper.getQaTeamInfo(qaTeamId);
	}
	
	public int insertQaUser(Map<String, String> insertMap){
		return qaTeamMapper.insertQaUser(insertMap);
	}
	
	public int deleteQaUser(Map<String, String> deleteMap){
		return qaTeamMapper.deleteQaUser(deleteMap);
	}
}
