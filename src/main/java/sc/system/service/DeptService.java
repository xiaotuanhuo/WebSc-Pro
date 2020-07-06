package sc.system.service;

import sc.system.mapper.DeptMapper;
import sc.system.model.WebScDept;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DeptService {
	private static final Logger log = LoggerFactory.getLogger(DeptService.class);

	@Resource
	private DeptMapper deptMapper;
	
	/**
	 * 查询医疗集团根节点
	 * @return
	 */
	public List<WebScDept> selectRootTree() {
		// 医疗集团根节点的父节点默认：0
		return deptMapper.selectByParentId("0");
	}
	
	public List<WebScDept> selectTree(String deptId) {
//		Subject subject = SecurityUtils.getSubject();
//		WebScUser user = (WebScUser) subject.getPrincipal();
		return deptMapper.selectTree(deptId);
	}
	
	public List<WebScDept> selectSubTree(String parentId) {
//		Subject subject = SecurityUtils.getSubject();
//		WebScUser user = (WebScUser) subject.getPrincipal();
		return deptMapper.selectSubTree(parentId);
	}

}
