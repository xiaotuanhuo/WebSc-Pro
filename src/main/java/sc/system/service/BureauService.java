package sc.system.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import sc.system.mapper.BureauMapper;
import sc.system.model.WebScBureau;

@Service
public class BureauService {
	private static final Logger logger = LoggerFactory.getLogger(BureauService.class);
	
	@Resource
	private BureauMapper bureauMapper;
	
	/**
     * 查找所有的卫监局的树形结构
     */
    public List<WebScBureau> selectAllDeptTree() {
        return bureauMapper.selectAllTree();
    }
}
