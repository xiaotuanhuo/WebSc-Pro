package sc.system.controller;

import sc.common.annotation.OperationLog;
import sc.common.util.ResultBean;
import sc.system.model.WebScDept;
import sc.system.service.DeptService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.annotation.Resource;

/**
 * 医疗集团
 * 
 * @author aisino
 *
 */
@Controller
@RequestMapping("/dept")
public class DeptController {

	@Resource
	private DeptService deptService;
	
	@GetMapping("/index")
	public String index() {
		return "dept/dept-list";
	}
	
	@GetMapping
	public String add(Model model) {
		return "dept/dept-add";
	}
	
	@GetMapping("/detail/{deptId}")
	public String getDeptInfo(@PathVariable("deptId") String deptId, Model model) {
		model.addAttribute("dept", deptService.getDeptInfo(deptId));
		return "dept/dept-detail";
	}
	
	/**
	 * 查询父节点为0的根节点
	 * @param parentId
	 * @return
	 */
	@GetMapping("/tree/root")
	@ResponseBody
	public ResultBean rootTree() {
		return ResultBean.success(deptService.getRootTree());
	}
	
	/**
	 * 查询当前节点及子节点
	 * @param parentId
	 * @return
	 */
	@GetMapping("/tree")
	@ResponseBody
	public ResultBean tree() {
		return ResultBean.success(deptService.getTree());
	}
	
	/**
	 * 查询子节点
	 * @param parentId
	 * @return
	 */
	@GetMapping("/subTree/{parentId}")
	@ResponseBody
	public ResultBean subTree(@PathVariable("parentId") String parentId) {
		return ResultBean.success(deptService.getSubTree(parentId));
	}
	
	/**
	 * 以列表形式查询当前节点及其子孙节点
	 * @param parentId
	 * @return
	 */
	@GetMapping("/tree/list")
	@ResponseBody
	public ResultBean getTreeList() {
		return ResultBean.success(deptService.getTreeList());
	}
	
	/**
	 * 查询非叶子节点及其子节点
	 * @return
	 */
	@GetMapping("/unleafTree")
	@ResponseBody
	public ResultBean unleafTree() {
		return ResultBean.success(deptService.getUnleafTree());
	}
	
	@OperationLog("新增区域/维修点")
	@PostMapping
	@ResponseBody
	public ResultBean add(WebScDept dept) {
		return ResultBean.success(deptService.add(dept));
	}
}
