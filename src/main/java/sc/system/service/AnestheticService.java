package sc.system.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import sc.system.mapper.AnestheticMapper;
import sc.system.model.WebScAnesthetic;

@Service
public class AnestheticService {
	@Resource
    private AnestheticMapper anestheticMapper;
	
	public List<WebScAnesthetic> getWebScAnestheticList(String anestheticName){
		return anestheticMapper.getWebScAnestheticList(anestheticName);
	}
}
