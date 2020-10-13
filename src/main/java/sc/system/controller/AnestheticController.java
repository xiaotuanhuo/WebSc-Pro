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
import sc.system.model.WebScAnesthetic;
import sc.system.service.AnestheticService;

/**
 * 麻醉管理
 * @author aisino
 *
 */
@Controller
@RequestMapping("/anesthetic")
public class AnestheticController {
	@Resource
	private AnestheticService anestheticService;
	
	@GetMapping("/index")
	public String index() {
		return "anesthetic/anesthetic-list";
	}
	
	@GetMapping
	public String add(Model model) {
		return "anesthetic/anesthetic-add";
	}
	
	@GetMapping("/{anestheticId}")
	public String update(@PathVariable("anestheticId") String anestheticId, Model model) {
		WebScAnesthetic anesthetic = anestheticService.getAnestheticById(anestheticId);
		model.addAttribute("anesthetic", anesthetic);
		return "anesthetic/anesthetic-add";
	}
	
	@OperationLog("获取麻醉列表")
	@GetMapping("/list")
	@ResponseBody
	public PageResultBean<WebScAnesthetic> getPageList(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "limit", defaultValue = "10") int limit, WebScAnesthetic query) {
		List<WebScAnesthetic> anesthetics = anestheticService.getPageList(page, limit, query);
		PageInfo<WebScAnesthetic> anestheticInfo = new PageInfo<>(anesthetics);
		return new PageResultBean<>(anestheticInfo.getTotal(), anestheticInfo.getList());
	}
	
	@OperationLog("新增麻醉名称")
	@PostMapping
	@ResponseBody
	public ResultBean add(WebScAnesthetic anesthetic) {
		anestheticService.add(anesthetic);
		return ResultBean.success();
	}
	
	@OperationLog("修改麻醉名称")
	@PutMapping
	@ResponseBody
	public ResultBean update(WebScAnesthetic anesthetic) {
		anestheticService.update(anesthetic);
		return ResultBean.success();
	}
	
	@OperationLog("删除麻醉名称")
	@DeleteMapping("/{anestheticId}")
	@ResponseBody
	public ResultBean delete(@PathVariable("anestheticId") String anestheticId) {
		anestheticService.delete(anestheticId);
		return ResultBean.success();
	}
}
