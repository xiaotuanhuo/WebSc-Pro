package sc.system.controller;

import java.util.ArrayList;
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
    	
//    	model.addAttribute("orgParentIdList", ls); 
        return "organization/organization-add";
    }
    
    @OperationLog("获取医疗机构列表")
    @GetMapping("/list")
    @ResponseBody
    public PageResultBean<WebScOrganization> getList(WebScOrganization wso) {
    	//数据查询
    	List<WebScOrganization> ls = organizationService. getList(wso);
    	
//    	List<WebScOrganization> ls = new ArrayList<WebScOrganization>();
//    	WebScOrganization o1 = new WebScOrganization("1","0","上医集团");
//    	WebScOrganization o2 = new WebScOrganization("101","1","上海");
//    	WebScOrganization o3 = new WebScOrganization("102","1","杭州");
//    	WebScOrganization o4 = new WebScOrganization("10101","101","市区");
//    	WebScOrganization o5 = new WebScOrganization("1010101","10101","黄浦区");
//    	WebScOrganization o6 = new WebScOrganization("101010101","1010101","上海第一医院");
//    	WebScOrganization o7 = new WebScOrganization("101010102","1010101","上海第一医院");
//    	WebScOrganization o8 = new WebScOrganization("101010103","1010101","上海第一医院");
//    	WebScOrganization o9 = new WebScOrganization("101010104","1010101","上海第一医院");
//    	WebScOrganization o10 = new WebScOrganization("101010105","1010101","上海第一医院");
//    	ls.add(o1);ls.add(o2);ls.add(o3);ls.add(o4);
//    	ls.add(o5);ls.add(o6);
//    	ls.add(o7);ls.add(o8);
//    	ls.add(o9);ls.add(o10);
    	return new PageResultBean<>(ls.size(), ls);
    }
    
    @OperationLog("新增医疗机构")
    @PostMapping
    @ResponseBody
    public ResultBean add(WebScOrganization wso) {
    	return ResultBean.success(organizationService.insert(wso));
    }
}
