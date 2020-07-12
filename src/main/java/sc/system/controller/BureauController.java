package sc.system.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sc.common.util.ResultBean;
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
	
	@GetMapping("/detail/{bureauId}")
	public String getDeptInfo(@PathVariable("bureauId") String bureauId, Model model) {
		model.addAttribute("bureau", bureauService.getBereauInfo(bureauId));
		return "bureau/bureau-detail";
	}
	
	/**
	 * 根据当前用户区划查询子节点
	 * @return
	 */
	@GetMapping("/tree")
	@ResponseBody
	public ResultBean tree() {
		return ResultBean.success(bureauService.getTree());
	}
}
