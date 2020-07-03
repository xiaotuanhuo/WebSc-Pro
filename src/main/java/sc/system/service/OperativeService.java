package sc.system.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import sc.system.mapper.OperativeMapper;
import sc.system.model.WebScOperative;

@Service
public class OperativeService {
	@Resource
    private OperativeMapper operativeMapper;
	
	public List<WebScOperative> getWebScOperativeList(String operativeName){
		return operativeMapper.getWebScOperativeList(operativeName);
	}
}
