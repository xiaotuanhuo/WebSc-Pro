package sc.system.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sc.common.annotation.OperationLog;
import sc.common.util.PageResultBean;
import sc.common.util.ResultBean;
import sc.system.model.WebScOrganization;
import sc.system.service.OrganizationService;

@Controller
@RequestMapping("/organization")
public class OrganizationController {

	@Resource
	private OrganizationService organizationService;
	
	@GetMapping("/index")
	public String index() {
		return "organization/organization-list";
	}
	
	@GetMapping
	public String add(Model model) {
		return "organization/organization-add";
	}
	
	@OperationLog("获取医疗机构列表")
	@GetMapping("/list")
	@ResponseBody
	public PageResultBean<WebScOrganization> getList(WebScOrganization wso,
			@RequestParam(value = "organizationId", required = false) String organizationId,
			@RequestParam(value = "distType", required = false) String distType) {
		// 数据查询
		List<WebScOrganization> wsoList = organizationService.getList(wso, organizationId, distType);
		return new PageResultBean<>(wsoList.size(), wsoList);
	}
	
	@OperationLog("获取所有医疗机构根节点")
    @GetMapping("/root")
    @ResponseBody
	public ResultBean getRoot() {
		return ResultBean.success(organizationService.getRoot());
	}
	
	/**
	 * 根据用户角色及当前区划查询医疗机构树
	 * @param parentId
	 * @return
	 */
	@GetMapping("/tree")
	@ResponseBody
	public ResultBean tree() {
		return ResultBean.success(organizationService.selectTree());
	}
	
	@GetMapping("/dist/tree")
	@ResponseBody
	public ResultBean distTree() {
		return ResultBean.success(organizationService.getDistTree());
	}
	
	@OperationLog("新增医疗机构")
	@PostMapping
	@ResponseBody
	public ResultBean add(WebScOrganization wso) {
		return ResultBean.success(organizationService.insert(wso));
	}
}
