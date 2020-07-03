package sc.system.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sc.common.annotation.OperationLog;
import sc.common.util.PageResultBean;
import sc.common.util.ResultBean;
import sc.common.util.ShiroUtil;
import sc.common.util.UUID19;
import sc.common.validate.groups.Create;
import sc.system.model.WebScAnesthetic;
import sc.system.model.WebScDoc;
import sc.system.model.WebScOperative;
import sc.system.model.WebScUser;
import sc.system.service.DocService;

import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("/doc")
public class DocController {
	
	@Resource
	DataController data;
	
	@Resource
	DocService docService;
	
	
	@GetMapping("/index")
    public String index() {
        return "doc/doc-list";
    }
	
	@GetMapping("/release")
    public String release(Model model) {
		//获取手术名称
		List<WebScOperative> operativels = data.getWebScOperativeList();
		model.addAttribute("operativels", operativels);
		//获取麻醉方法
		List<WebScAnesthetic> anestheticls = data.getWebScAnestheticList();
		model.addAttribute("anestheticls", anestheticls);
        return "doc/doc-release";
    }
	
	@GetMapping("/distribution")
    public String distribution(@RequestParam(value = "documentId") String documentId,
		 	  Model model) {
		//获取订单数据
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		List<WebScDoc> docs = docService.selectWebScDocList(1, 1, doc);
		if(docs != null)
		model.addAttribute("doc", docs.get(0));
		//获取手术名称
		List<WebScOperative> operativels = data.getWebScOperativeList();
		model.addAttribute("operativels", operativels);
		//获取麻醉方法
		List<WebScAnesthetic> anestheticls = data.getWebScAnestheticList();
		model.addAttribute("anestheticls", anestheticls);
		
		return "doc/doc-distribution";
    }
	
	/**
	 * 
	 * @Title: getGridList   
	 * @Description: 获取订单列表
	 * @param: @param page
	 * @param: @param limit
	 * @param: @param state
	 * @param: @param docQuery
	 * @param: @return      
	 * @return: PageResultBean<WebScDoc>      
	 * @throws
	 */
	@OperationLog("获取订单列表")
    @GetMapping("/list")
    @ResponseBody
    public PageResultBean<WebScDoc> getGridList(@RequestParam(value = "page", defaultValue = "1") int page,
    											@RequestParam(value = "limit", defaultValue = "10") int limit,
    											@RequestParam(value = "state") String state,
    											WebScDoc docQuery) {
		//当前用户信息
		WebScUser user = ShiroUtil.getCurrentUser();
		//用户角色
		String roleId = user.getRoleId();
		docQuery.setRoleId(roleId);
		//更具不同角色,查询内容不同
		if(roleId.equals("1")){
			//系统管理员, 查询所有单据
		}else if(roleId.equals("2")){
			//医疗机构人员，查询本机构发布订单
			docQuery.setApplyUserId(String.valueOf(user.getUserId()));
		}else if(roleId.equals("3")){
			//卫监局人员，查询所有订单？？？？？？
			
		}else if(roleId.equals("4")){
			//医生，查询主治医生
			docQuery.setQaUserId(String.valueOf(user.getUserId()));
		}
		
		//省，市，区   查询范围
		docQuery.setProvince(user.getProvince());
		docQuery.setCity(user.getCity());
		docQuery.setArea(user.getArea());
		
		docQuery.setDocumentState(state);
		
		List<WebScDoc> docs = docService.selectWebScDocList(page, limit, docQuery);
        PageInfo<WebScDoc> docPageInfo = new PageInfo<>(docs);
        return new PageResultBean<>(docPageInfo.getTotal(), docPageInfo.getList());
    }
	
	/**
	 * 
	 * @Title: release   
	 * @Description: 订单发布
	 * @param: @param doc
	 * @param: @return      
	 * @return: ResultBean      
	 * @throws
	 */
	@OperationLog("发布订单")
    @PostMapping
    @ResponseBody
    public ResultBean release(@Validated(Create.class) WebScDoc doc) {
		try{
			//数据整理
			//当前用户信息
			WebScUser user = ShiroUtil.getCurrentUser();
			doc.setApplyUserId(String.valueOf(user.getUserId()));
			
			//流水号
			doc.setDocumentId(UUID19.uuid());
			//订单类型
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			
			if(!format.parse(doc.getOperateStartTime()).after(calendar.getTime())){
				doc.setDocumentType("1");
			}else{
				doc.setDocumentType("0");
			}
			
			//病人类型
			if(doc.getPatientAge() <= 16){
				doc.setPatienttypeId("1");
			}else if(doc.getPatientAge() > 16 && doc.getPatientAge() <= 60){
				doc.setPatienttypeId("2");
			}else{
				doc.setPatienttypeId("3");
			}
			
			//订单状态
			doc.setDocumentState("0");
			
			int iRet = docService.insert(doc);
			if(iRet > 0){
				return ResultBean.success();
			}else{
				return ResultBean.error("发布订单失败！");
			}
		}catch(Exception e){
			e.printStackTrace();
			return ResultBean.error("发布订单失败！");
		}
    }

	/**
	 * 
	 * @Title: examine   
	 * @Description: 跳转订单审核界面
	 * @param: @param documentId
	 * @param: @param model
	 * @param: @return      
	 * @return: String      
	 * @throws
	 */
	@GetMapping("/examine")
    public String examine(@RequestParam(value = "documentId") String documentId,
    				 	  Model model) {
		//获取订单数据
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		List<WebScDoc> docs = docService.selectWebScDocList(1, 1, doc);
		if(docs != null)
			model.addAttribute("doc", docs.get(0));
		//获取手术名称
		List<WebScOperative> operativels = data.getWebScOperativeList();
		model.addAttribute("operativels", operativels);
		//获取麻醉方法
		List<WebScAnesthetic> anestheticls = data.getWebScAnestheticList();
		model.addAttribute("anestheticls", anestheticls);
		model.addAttribute("type", "examine");
        return "doc/doc-info";
    }
	
	/**
	 * 
	 * @Title: examine   
	 * @Description: 订单审核
	 * @param: @param documentId
	 * @param: @param documentState
	 * @param: @return      
	 * @return: ResultBean      
	 * @throws
	 */
	@OperationLog("审核订单")
    @PostMapping("/examine")
    @ResponseBody
    public ResultBean examine(@RequestParam(value = "id") String documentId,
    						  @RequestParam(value = "state") String documentState) {
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		doc.setDocumentState(documentState);
        return ResultBean.success(docService.updateByPrimaryKey(doc));
    }

	@OperationLog("获取分配医生列表")
	@GetMapping("/distribution_drlist")
    @ResponseBody
    public PageResultBean<WebScUser> getDistributionDrGridList(@RequestParam(value = "id") String documentId) {
		//当前用户信息
		WebScUser user = ShiroUtil.getCurrentUser();
		//获取医生名单
		List<WebScUser> drList = docService.getDistributionDrGridList(documentId, user.getProvince());
		PageInfo<WebScUser> drPageInfo = new PageInfo<>(drList);
        return new PageResultBean<>(drPageInfo.getTotal(), drPageInfo.getList());
	}
	
}


