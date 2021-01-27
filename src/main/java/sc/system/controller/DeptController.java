package sc.system.controller;

import sc.common.annotation.OperationLog;
import sc.common.util.ResultBean;
import sc.system.model.WebScDept;
import sc.system.service.DeptService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
		model.addAttribute("superDept", deptService.getSuperDept());
		return "dept/dept-add";
	}
	
	@GetMapping("/{id}")
	public String update(@PathVariable("id") String deptId, Model model) {
		model.addAttribute("dept", deptService.getByDeptId(deptId));
		return "dept/dept-edit";
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
	public ResultBean tree(@RequestParam(value = "deptId", required = false) String deptId) {
		return ResultBean.success(deptService.getTree(deptId));
	}
	
	@GetMapping("/dist/tree")
	@ResponseBody
	public ResultBean distTree() {
		return ResultBean.success(deptService.getDistTree());
	}
	
	/**
	 * 查询当前节点及子节点
	 * 仅city节点可选中
	 * @param parentId
	 * @return
	 */
	@GetMapping("/tree/city")
	@ResponseBody
	public ResultBean cityTree() {
		return ResultBean.success(deptService.getCityTree());
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
	
	@OperationLog("新增医疗集团")
	@PostMapping
	@ResponseBody
	public ResultBean add(WebScDept dept) {
		return ResultBean.success(deptService.add(dept));
	}
	
	@OperationLog("编辑医疗集团")
	@PutMapping
	@ResponseBody
	public ResultBean update(WebScDept dept) {
		return ResultBean.success(deptService.update(dept));
	}
	
	@OperationLog("锁定医疗集团")
	@PutMapping("/lock/{deptId}")
	@ResponseBody
	public ResultBean lock(@PathVariable("deptId") String deptId) {
		deptService.lock(deptId);
		return ResultBean.success();
	}
	
	@OperationLog("激活医疗集团")
	@PutMapping("/unlock/{deptId}")
	@ResponseBody
	public ResultBean unlock(@PathVariable("deptId") String deptId) {
		deptService.unlock(deptId);
		return ResultBean.success();
	}
}
