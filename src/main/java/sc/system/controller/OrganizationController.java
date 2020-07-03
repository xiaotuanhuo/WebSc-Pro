package sc.system.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public PageResultBean<WebScOrganization> getList(WebScOrganization wso) {
    	//数据查询
    	List<WebScOrganization> wsoList = organizationService. getList(wso);
    	return new PageResultBean<>(wsoList.size(), wsoList);
    }
    
    @GetMapping("/tree")
    @ResponseBody
    public ResultBean tree() {
    	return ResultBean.success(organizationService.selectAllOrgTree());
    }
    
    @OperationLog("获取医疗机构根节点列表")
    @GetMapping("/addEditUser/list")
    @ResponseBody
    public ResultBean getOrgForAddEidtUser() {
    	return ResultBean.success(organizationService.getOrgForAddEidtUser());
    }
    
    @OperationLog("新增医疗机构")
    @PostMapping
    @ResponseBody
    public ResultBean add(WebScOrganization wso) {
    	return ResultBean.success(organizationService.insert(wso));
    }
}
