package sc.system.service;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sc.system.mapper.DocTmpMapper;
import sc.system.model.WebScDoc;
import sc.system.model.vo.District;

@Service
public class DocTmpService {
	@Resource
	private DocTmpMapper docTmpMapper;
	
	@Autowired
	private District district;
	
	public int insert(WebScDoc doc) {
		int iRet = 0;
		
		if(docTmpMapper.count() > 0){
			iRet = docTmpMapper.update(doc);
		}else{
			iRet = docTmpMapper.insert(doc);
		}
		
        return iRet;
    }

}
