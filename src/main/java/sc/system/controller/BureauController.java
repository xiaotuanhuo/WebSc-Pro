package sc.system.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sc.common.annotation.OperationLog;
import sc.common.util.ResultBean;
import sc.system.model.WebScBureau;
import sc.system.service.BureauService;

/**
 * 卫监局
 * 
 * @author aisino
 *
 */
@Controller
@RequestMapping("/bureau")
public class BureauController {

	@Resource
	private BureauService bureauService;
	
	@GetMapping("/index")
	public String index() {
		return "bureau/bureau-list";
	}
	
	@GetMapping
	public String add(Model model) {
		return "bureau/bureau-add";
	}
	
	@GetMapping("/{id}")
	public String update(@PathVariable("id") String bureauId, Model model) {
		model.addAttribute("bureau", bureauService.getByBureauId(bureauId));
		return "bureau/bureau-edit";
	}
	
	@GetMapping("/detail/{bureauId}")
	public String getDeptInfo(@PathVariable("bureauId") String bureauId, Model model) {
		model.addAttribute("bureau", bureauService.getBereauInfo(bureauId));
		return "bureau/bureau-detail";
	}
	
	/**
	 * 根据当前用户行政区划查询节点及其子节点（树形）
	 * @return
	 */
	@GetMapping("/tree")
	@ResponseBody
	public ResultBean lsitTree() {
		return ResultBean.success(bureauService.getTree());
	}
	
	/**
	 * 根据当前用户区划查询子节点（列表）
	 * @return
	 */
	@GetMapping("/list/tree")
	@ResponseBody
	public ResultBean lsitTree(@RequestParam(value = "bureauId", required = false) String bureauId) {
		return ResultBean.success(bureauService.getListTree(bureauId));
	}
	
	/**
	 * 查询根节点及其下级行政区划
	 * @return
	 */
	@GetMapping("/dist/tree")
	@ResponseBody
	public ResultBean distTree() {
		return ResultBean.success(bureauService.getDistTree());
	}
	
	@OperationLog("新增卫监局")
	@PostMapping
	@ResponseBody
	public ResultBean add(WebScBureau bureau) {
		return ResultBean.success(bureauService.add(bureau));
	}
	
	@OperationLog("编辑卫监局")
	@PutMapping
	@ResponseBody
	public ResultBean update(WebScBureau bureau) {
		return ResultBean.success(bureauService.update(bureau));
	}
	
	@OperationLog("锁定卫监局")
	@PutMapping("/lock/{bureauId}")
	@ResponseBody
	public ResultBean lock(@PathVariable("bureauId") String bureauId) {
		bureauService.lock(bureauId);
		return ResultBean.success();
	}
	
	@OperationLog("激活卫监局")
	@PutMapping("/unlock/{bureauId}")
	@ResponseBody
	public ResultBean unlock(@PathVariable("bureauId") String bureauId) {
		bureauService.unlock(bureauId);
		return ResultBean.success();
	}
}
