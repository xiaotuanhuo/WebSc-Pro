package sc.system.controller;

import sc.common.annotation.OperationLog;
import sc.common.util.ResultBean;
import sc.system.model.WebScDept;
import sc.system.service.DeptService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 医疗集团控制器
 * 
 * @author aisino
 *
 */
@Controller
@RequestMapping("/dept")
public class DeptController {

	@Resource
	private DeptService deptService;
	
	/**
	 * 查询当前节点及子节点
	 * @param parentId
	 * @return
	 */
	@GetMapping("/tree/root")
	@ResponseBody
	public ResultBean rootTree() {
		return ResultBean.success(deptService.selectRootTree());
	}
	
	/**
	 * 查询当前节点及子节点
	 * @param parentId
	 * @return
	 */
	@GetMapping("/tree/{deptId}")
	@ResponseBody
	public ResultBean tree(@PathVariable("deptId") String deptId) {
		return ResultBean.success(deptService.selectTree(deptId));
	}
	
	/**
	 * 查询子节点
	 * @param parentId
	 * @return
	 */
	@GetMapping("/sub/tree/{parentId}")
	@ResponseBody
	public ResultBean subTree(@PathVariable("parentId") String parentId) {
		return ResultBean.success(deptService.selectSubTree(parentId));
	}

}
