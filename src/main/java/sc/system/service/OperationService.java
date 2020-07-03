package sc.system.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import sc.system.mapper.OperationMapper;
import sc.system.model.WebScOperation;

@Service
public class OperationService {
	private static final Logger logger = LoggerFactory.getLogger(OperationService.class);
	
	@Resource
	private OperationMapper operationMapper;
	
	public List<WebScOperation> getAll() {
		return operationMapper.selectAll();
	}
}
