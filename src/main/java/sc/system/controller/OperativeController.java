package sc.system.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;

import sc.common.annotation.OperationLog;
import sc.common.util.PageResultBean;
import sc.common.util.ResultBean;
import sc.system.model.WebScOperative;
import sc.system.service.OperativeService;

/**
 * 手术管理
 * @author aisino
 *
 */
@Controller
@RequestMapping("/operative")
public class OperativeController {
	@Resource
	private OperativeService operativeService;
	
	@GetMapping("/index")
	public String index() {
		return "operative/operative-list";
	}
	
	@GetMapping
	public String add(Model model) {
		return "operative/operative-add";
	}
	
	@GetMapping("/{operativeId}")
	public String update(@PathVariable("operativeId") String operativeId, Model model) {
		WebScOperative operative = operativeService.getOperativeById(operativeId);
		model.addAttribute("operative", operative);
		return "operative/operative-edit";
	}
	
	@OperationLog("获取手术列表")
	@GetMapping("/list")
	@ResponseBody
	public PageResultBean<WebScOperative> getPageList(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "limit", defaultValue = "10") int limit, WebScOperative query) {
		List<WebScOperative> operatives = operativeService.getPageList(page, limit, query);
		PageInfo<WebScOperative> operativeInfo = new PageInfo<>(operatives);
		return new PageResultBean<>(operativeInfo.getTotal(), operativeInfo.getList());
	}
	
	@OperationLog("新增手术")
	@PostMapping
	@ResponseBody
	public ResultBean add(WebScOperative operative) {
		operativeService.add(operative);
		return ResultBean.success();
	}
	
	@OperationLog("修改手术")
	@PutMapping
	@ResponseBody
	public ResultBean update(WebScOperative operative) {
		operativeService.update(operative);
		return ResultBean.success();
	}
	
	@OperationLog("删除手术")
	@DeleteMapping("/{operativeId}")
	@ResponseBody
	public ResultBean delete(@PathVariable("operativeId") String operativeId) {
		operativeService.delete(operativeId);
		return ResultBean.success();
	}
	
	@GetMapping("/types")
    @ResponseBody
    public ResultBean getTypes() {
		return ResultBean.success(operativeService.getAllType());
	}
}
