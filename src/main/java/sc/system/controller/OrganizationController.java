package sc.system.controller;

import java.util.List;

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

import com.github.pagehelper.PageInfo;

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
	
	@GetMapping("/detail/{id}")
	public String getDeptInfo(@PathVariable("id") String id, Model model) {
		model.addAttribute("organization", organizationService.getByOrgid(id));
		return "organization/organization-detail";
	}
	
	@GetMapping("/{id}")
	public String update(@PathVariable("id") String id, Model model) {
		model.addAttribute("organization", organizationService.getByOrgid(id));
		return "organization/organization-edit";
	}
	
//	@OperationLog("获取医疗机构列表")
//	@GetMapping("/list")
//	@ResponseBody
//	public PageResultBean<WebScOrganization> getList(WebScOrganization wso,
//			@RequestParam(value = "organizationId", required = false) String organizationId,
//			@RequestParam(value = "distType", required = false) String distType) {
//		// 数据查询
//		List<WebScOrganization> wsoList = organizationService.getList(wso, organizationId, distType);
//		return new PageResultBean<>(wsoList.size(), wsoList);
//	}
	
	@OperationLog("获取医疗机构列表")
	@GetMapping("/list")
	@ResponseBody
	public PageResultBean<WebScOrganization> getList(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "limit", defaultValue = "10") int limit, WebScOrganization wso) {
		// 数据查询
		List<WebScOrganization> wsoList = organizationService.getList(page, limit, wso);
		PageInfo<WebScOrganization> organizationPageList = new PageInfo<>(wsoList);
		return new PageResultBean<>(organizationPageList.getTotal(), organizationPageList.getList());
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
	
	/**
	 * 根据用户角色及当前区划查询医疗机构树
	 * @param parentId
	 * @return
	 */
	@GetMapping("/tree/isLeaf")
	@ResponseBody
	public ResultBean treeOfLeaf() {
		return ResultBean.success(organizationService.selectTreeLeaf());
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
	
	@OperationLog("新增医疗机构根节点")
	@PostMapping("/root")
	@ResponseBody
	public ResultBean addRoot(WebScOrganization wso) {
		return ResultBean.success(organizationService.insertRoot(wso));
	}
	
	@OperationLog("编辑医疗集团")
	@PutMapping
	@ResponseBody
	public ResultBean update(WebScOrganization organization) {
		return ResultBean.success(organizationService.update(organization));
	}
	

	@OperationLog("锁定医疗机构")
	@PutMapping("/lock/{orgId}")
	@ResponseBody
	public ResultBean lock(@PathVariable("orgId") String orgId) {
		organizationService.lock(orgId);
		return ResultBean.success();
	}
	
	@OperationLog("激活医疗机构")
	@PutMapping("/unlock/{orgId}")
	@ResponseBody
	public ResultBean unlock(@PathVariable("orgId") String orgId) {
		organizationService.unlock(orgId);
		return ResultBean.success();
	}
}
