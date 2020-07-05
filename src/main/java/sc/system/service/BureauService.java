package sc.system.service;

import java.util.List;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import sc.system.mapper.BureauMapper;
import sc.system.model.WebScBureau;
import sc.system.model.WebScUser;

@Service
public class BureauService {
	private static final Logger logger = LoggerFactory.getLogger(BureauService.class);
	
	@Resource
	private BureauMapper bureauMapper;
	
    public List<WebScBureau> selectAllTree() {
    	Subject subject = SecurityUtils.getSubject();
		WebScUser user = (WebScUser) subject.getPrincipal();
		return bureauMapper.selectAllTree(user.getProvince(), user.getCity());
    }
}
