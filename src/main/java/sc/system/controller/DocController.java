package sc.system.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import sc.common.annotation.OperationLog;
import sc.common.util.PageResultBean;
import sc.common.util.ResultBean;
import sc.common.util.ShiroUtil;
import sc.common.util.UUID19;
import sc.common.validate.groups.Create;
import sc.system.model.StateCount;
import sc.system.model.WebScAnesthetic;
import sc.system.model.WebScDoc;
import sc.system.model.WebScOperative;
import sc.system.model.WebScOrganization;
import sc.system.model.WebScUser;
import sc.system.model.WebScUser_Distribution;
import sc.system.service.DocService;
import sc.system.service.DocTmpService;
import sc.system.service.QaTeamService;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.jxcell.View;

@Controller
@RequestMapping("/doc")
public class DocController {
	private static final Logger log = LoggerFactory.getLogger(DocController.class);
	private static String dirPath = "/home/photo";
	
	@Resource
	DataController data;
	
	@Resource
	DocService docService;
	
	@Resource
	DocTmpService docTmpService;
	
	@Resource
	QaTeamService qaTeamService;
	
	
	@GetMapping("/index")
    public String index(Model model) {
		//当前用户信息
		WebScUser user = ShiroUtil.getCurrentUser();
		
		//用户角色
		String roleId = user.getRoleId();
		
		model.addAttribute("role", roleId);
		
		//单子数量
		WebScDoc docQuery = new WebScDoc();	
		docQuery.setRoleId(roleId);
		//更具不同角色,查询内容不同
		if(roleId.equals("1")){
			//系统管理员, 查询所有单据
		}else if(roleId.equals("2") || roleId.equals("9")){
			//医疗机构人员，查询本机构发布订单
			docQuery.setApplyUserId(String.valueOf(user.getUserId()));
			//省，市，区   查询范围
			docQuery.setProvince(user.getProvince());
			docQuery.setCity(user.getCity());
			docQuery.setArea(user.getArea());
		}else if(roleId.equals("8")){
			//区域订单录入员,查询本人发布订单
			docQuery.setApplyUserId(String.valueOf(user.getUserId()));
			//省，市，区   查询范围
			docQuery.setProvince(user.getProvince());
			docQuery.setCity(user.getCity());
		}else if(roleId.equals("3")){
			//卫监局人员
			//省，市，区   查询范围
			docQuery.setProvince(user.getProvince());
			docQuery.setCity(user.getCity());
			docQuery.setArea(user.getArea());
		}else if(roleId.equals("4")){
			//区域管理人员
			//省，市，区   查询范围
			docQuery.setProvince(user.getProvince());
			docQuery.setCity(user.getCity());
			docQuery.setArea(user.getArea());
		}else if(roleId.equals("5")){
			//医生，查询主治医生
			docQuery.setQaUserId(String.valueOf(user.getUserId()));
			//省，市，区   查询范围
			docQuery.setProvince(user.getProvince());
			docQuery.setCity(user.getCity());
		}else if(roleId.equals("6")){
			//护士
			//省，市，区   查询范围
			docQuery.setProvince(user.getProvince());
			docQuery.setCity(user.getCity());
			docQuery.setDocumentState("'5'");
			docQuery.setIsHistroy("1");
		}
				
		StateCount sc = docService.getStateCount(docQuery);
		
		model.addAttribute("sc", sc);
		
		//获取医疗机构
		List<WebScOrganization> orgls = data.getWebScOrganizationList();
		model.addAttribute("orgls", orgls);
		
        return "doc/doc-list";
    }
	
	@GetMapping("/docImport")
    public String docImport() {
        return "doc/doc-import";
    }
	
	@GetMapping("/toImportDocsView")
    public String toImportDocsView() {
        return "doc/doc-import-view";
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
doc.setIsHistroy("1");
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
	
	@GetMapping("/ordertaking")
    public String ordertaking(@RequestParam(value = "documentId") String documentId,
		 	  								Model model) {
		//获取订单数据
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		doc.setIsHistroy("1");
		List<WebScDoc> docs = docService.selectWebScDocList(1, 1, doc);
		if(docs != null)
		model.addAttribute("doc", docs.get(0));
		//获取手术名称
		List<WebScOperative> operativels = data.getWebScOperativeList();
		model.addAttribute("operativels", operativels);
		//获取麻醉方法
		List<WebScAnesthetic> anestheticls = data.getWebScAnestheticList();
		model.addAttribute("anestheticls", anestheticls);
		
		return "doc/doc-ordertaking";
    }
	
	@GetMapping("/docInfo")
    public String docInfo(@RequestParam(value = "documentId") String documentId,
    				      @RequestParam(value = "state") String state,
		 	  			  Model model) throws Exception {
		//获取订单数据
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		doc.setIsHistroy("1");
		List<WebScDoc> docs = docService.selectWebScDocList(1, 1, doc);
		if(docs != null){
			WebScDoc realDoc = docs.get(0);
			if(realDoc.getSskssj() != null && realDoc.getSsjssj() != null){
				long min = getDistanceMin(realDoc.getSskssj(), realDoc.getSsjssj());
				realDoc.setSssc((int)min);
			}else{
				realDoc.setSssc(0);
			}
			model.addAttribute("doc", realDoc);
		}
			
		//获取手术名称
		List<WebScOperative> operativels = data.getWebScOperativeList();
		model.addAttribute("operativels", operativels);
		//获取麻醉方法
		List<WebScAnesthetic> anestheticls = data.getWebScAnestheticList();
		model.addAttribute("anestheticls", anestheticls);
		
		model.addAttribute("htmlstate", state);
		return "doc/doc-info";
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
    											@RequestParam(value = "date", required=false) String date,
    											@RequestParam(value = "beginDate", required=false) String beginDate,
    											@RequestParam(value = "endDate", required=false) String endDate,
    											@RequestParam(value = "qaUserName", required=false) String qaUserName,
    											@RequestParam(value = "patientName", required=false) String patientName,
    											@RequestParam(value = "patienttypeId", required=false) String patienttypeId,
    											@RequestParam(value = "asa", required=false) String asa,
    											@RequestParam(value = "anestheticId", required=false) String anestheticId,
    											@RequestParam(value = "orgId", required=false) String orgId,
    											WebScDoc docQuery) {
		//当前用户信息
		WebScUser user = ShiroUtil.getCurrentUser();
		//用户角色
		String roleId = user.getRoleId();
		docQuery.setRoleId(roleId);
		//更具不同角色,查询内容不同
		if(roleId.equals("1")){
			//系统管理员, 查询所有单据
		}else if(roleId.equals("2") || roleId.equals("9")){
			//医疗机构人员，查询本机构发布订单
			docQuery.setApplyUserId(String.valueOf(user.getUserId()));
			//省，市，区   查询范围
			docQuery.setProvince(user.getProvince());
			docQuery.setCity(user.getCity());
			docQuery.setArea(user.getArea());
		}else if(roleId.equals("8")){
			//区域订单录入员,查询本人发布订单
			docQuery.setApplyUserId(String.valueOf(user.getUserId()));
			//省，市，区   查询范围
			docQuery.setProvince(user.getProvince());
			docQuery.setCity(user.getCity());
		}else if(roleId.equals("3")){
			//卫监局人员
			//省，市，区   查询范围
			docQuery.setProvince(user.getProvince());
			docQuery.setCity(user.getCity());
			docQuery.setArea(user.getArea());
		}else if(roleId.equals("4")){
			//区域管理人员
			//省，市，区   查询范围
			docQuery.setProvince(user.getProvince());
			docQuery.setCity(user.getCity());
			docQuery.setArea(user.getArea());
			
		}else if(roleId.equals("5")){
			//医生，查询主治医生
			docQuery.setQaUserId(String.valueOf(user.getUserId()));
			//省，市，区   查询范围
			docQuery.setProvince(user.getProvince());
			docQuery.setCity(user.getCity());
		}else if(roleId.equals("6")){
			//护士
			//省，市，区   查询范围
			docQuery.setProvince(user.getProvince());
			docQuery.setCity(user.getCity());
			docQuery.setDocumentState("'5'");
			docQuery.setIsHistroy("1");
		}
		docQuery.setDocumentState(state);
		
		//查询条件
		if(date != null && !date.trim().equals("")){
			docQuery.setOperateStartTime(date + " 00:00:00");
			docQuery.setOperateEndTime(date + " 23:59:59");
		}
		if(beginDate != null && !beginDate.trim().equals("")){
			docQuery.setOperateStartTime(beginDate + " 00:00:00");
		}
		if(endDate != null && !endDate.trim().equals("")){
			docQuery.setOperateEndTime(endDate + " 23:59:59");
		}
		if(qaUserName != null && !qaUserName.trim().equals("")){
			docQuery.setQaUserName(qaUserName);
		}
		if(patientName != null && !patientName.trim().equals("")){
			docQuery.setPatientName(patientName);
		}
		
		if(orgId != null && !orgId.equals("")){
			String orgStr[] = orgId.split(",");
			String orgVal = "";
			for(String o : orgStr){
				orgVal = orgVal + "'" + o + "',";
			}
			if(!orgVal.equals("")){
				orgVal = orgVal.substring(0, orgVal.length() - 1);
			}
			log.info(orgVal);
			docQuery.setOrgId(orgVal);
		}
		
		if(state.contains("5")){
			docQuery.setIsHistroy("1");
		}
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
			
			//发布组织ID
			doc.setOrgId(user.getRoleTypeId());
			
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
		doc.setIsHistroy("1");
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
        return "doc/doc-examine";
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
    public PageResultBean<WebScUser_Distribution> getDistributionDrGridList(@RequestParam(value = "id") String documentId,
    																		@RequestParam(value = "qaName") String qaName) {
		//当前用户信息
		WebScUser user = ShiroUtil.getCurrentUser();
		//获取医生名单
		List<WebScUser_Distribution> drList = docService.getDistributionDrGridList(documentId, qaName, user);
		PageInfo<WebScUser_Distribution> drPageInfo = new PageInfo<>(drList);
        return new PageResultBean<>(drPageInfo.getTotal(), drPageInfo.getList());
	}
	
    
    /**
	 * 
	 * @Title: setdistribution   
	 * @Description: 确认分配
	 * @param: @param docId
	 * @param: @param userId
	 * @param: @return      
	 * @return: ResultBean      
	 * @throws
	 */
	@OperationLog("确认分配")
    @PostMapping("/setdistribution")
    @ResponseBody
    public ResultBean setdistribution(@RequestParam(value = "doc_id") String docId,
    						  		  @RequestParam(value = "user_id") String userId) {
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(docId);
		doc.setQaUserId(userId);
		doc.setDocumentState("2");
        return ResultBean.success(docService.updateByPrimaryKey(doc));
    }
	
	@OperationLog("取消分配")
    @PostMapping("/canneldistribution")
    @ResponseBody
    public ResultBean canneldistribution(@RequestParam(value = "id") String documentId) {
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		doc.setQaUserId("");
		doc.setDocumentState("1");
        return ResultBean.success(docService.updateByPrimaryKey(doc));
    }
	
	
	/**
	 * 
	 * @Title: setOrdertaking   
	 * @Description: 确认接单
	 * @param: @param documentId
	 * @param: @param documentState
	 * @param: @return      
	 * @return: ResultBean      
	 * @throws
	 */
	@OperationLog("确认接单")
    @PostMapping("/setOrdertaking")
    @ResponseBody
    public ResultBean setOrdertaking(@RequestParam(value = "id") String documentId) {
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		doc.setDocumentState("3");
        return ResultBean.success(docService.updateByPrimaryKey(doc));
    }
	
	@GetMapping("/surgicalrecords")
    public String surgicalrecords(@RequestParam(value = "documentId") String documentId,
    				 	  		  Model model) {
		//获取订单数据
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		doc.setIsHistroy("1");
		List<WebScDoc> docs = docService.selectWebScDocList(1, 1, doc);
		if(docs != null){
			WebScDoc d = docs.get(0);
			String photo = "";
			if(d.getPhoto_1() != null && !d.getPhoto_1().trim().equals("")){
				photo = photo + d.getPhoto_1() + ",";
			}
			if(d.getPhoto_2() != null && !d.getPhoto_2().trim().equals("")){
				photo = photo + d.getPhoto_2() + ",";
			}
			if(d.getPhoto_3() != null && !d.getPhoto_3().trim().equals("")){
				photo = photo + d.getPhoto_3() + ",";
			}
			if(!photo.equals(""))	photo = photo.substring(0, photo.length() - 1);
			d.setPhoto(photo);
			
			model.addAttribute("doc", d);
		}
			
		//获取手术名称
		List<WebScOperative> operativels = data.getWebScOperativeList();
		model.addAttribute("operativels", operativels);
		//获取麻醉方法
		List<WebScAnesthetic> anestheticls = data.getWebScAnestheticList();
		model.addAttribute("anestheticls", anestheticls);
		return "doc/doc-surgicalrecords";
	}
	
	/**
	 * 
	 * @Title: importFile   
	 * @Description: 手术详情时上传照片
	 * @param: @param file
	 * @param: @param documentId
	 * @param: @return      
	 * @return: String      
	 * @throws
	 */
	@RequestMapping("/upload")
    @ResponseBody
    public String importFile(MultipartFile file, @RequestParam(value = "documentId") String documentId) {
    	JSONObject object = new JSONObject();
        try {
        	//当前年月
        	Date d = new Date();  
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");  
            String dateNowStr = sdf.format(d);  
            
            File path = new File(dirPath  + File.separator + dateNowStr);
            if (!path.exists()) {
            	path.mkdir();
			}
            
        	String originalFilename = file.getOriginalFilename();
        	String filePath = File.separator + dateNowStr + File.separator + documentId + "_" + originalFilename;
        	
        	File dir = new File(dirPath + filePath);
        	//System.out.println("路径:" + dirPath + filePath);
        	if (dir.exists()) {
        		object.put("code", "fail");
    			object.put("message", "同名文件已存在！");
        	}else{
//        		boolean b = new File(dirPath).mkdirs();
    			file.transferTo(new File(new File(dirPath).getAbsoluteFile() + filePath));
    			
    			object.put("title", filePath);
    			object.put("code", "success");
    			object.put("message", "文件上传成功！");
        	}
		} catch (IOException e) {
			e.printStackTrace();
			object.put("code", "fail");
			object.put("message", "文件上传失败");
		}
        return object.toJSONString();
    }
	
	
	@RequestMapping("/getPhotoByFileName")
	public void getPhotoByFileName (@RequestParam(value = "documentId") String documentId, 
									@RequestParam(value = "FileName") String FileName, 
									final HttpServletResponse response) throws IOException{
		try { 
			File file = new File(dirPath + FileName);
			if(file != null){
				FileInputStream fis = new FileInputStream (file);
	            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
	            byte[] b = new byte[1000];  
	            int n;  
	            while ((n = fis.read(b)) != -1) {  
	                bos.write(b, 0, n);  
	            }  
	            fis.close();  
	            bos.close();
	            
	            byte[] data = bos.toByteArray();  
	            
	            response.setContentType("image/jpeg");  
	    	    response.setCharacterEncoding("UTF-8");  
	    	    OutputStream outputSream = response.getOutputStream();  
	    	    InputStream in = new ByteArrayInputStream(data);  
	    	    int len = 0;  
	    	    byte[] buf = new byte[1024];  
	    	    while ((len = in.read(buf, 0, 1024)) != -1) {  
	    	        outputSream.write(buf, 0, len);  
	    	    }  
	    	    outputSream.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/deletePhotoByFileName")
	@ResponseBody
	public void deletePhotoByFileName (@RequestParam(value = "documentId") String documentId, 
									@RequestParam(value = "FileName") String FileName){
		try {
			File file = new File(dirPath + "/" + documentId + "_" + FileName);
			file.delete();
		}catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	@OperationLog("保存草稿")
	@PostMapping("/save_surgicalrecordsDone")
    @ResponseBody
    public ResultBean save_surgicalrecordsDone(@Validated(Create.class) WebScDoc doc) {
		try{
			//数据整理
			//checkbox
			if(doc.getShblZw() != null)	
				doc.setShblZw("1");
			else
				doc.setShblZw("0");
			
			if(doc.getShblHbtt() != null)	
				doc.setShblHbtt("1");
			else
				doc.setShblHbtt("0");
			
			if(doc.getShblEx() != null)	
				doc.setShblEx("1");
			else
				doc.setShblEx("0");
			
			if(doc.getShblXy() != null)	
				doc.setShblXy("1");
			else
				doc.setShblXy("0");
			
			if(doc.getShblOt() != null)	
				doc.setShblOt("1");
			else
				doc.setShblOt("0");
			
			//照片
			doc.setPhoto_1("");
			doc.setPhoto_2("");
			doc.setPhoto_3("");
			String photo = doc.getPhoto();
			if(photo != null && !photo.equals("")){
				String[] photols = photo.split(",");
				for(int i = 0; i< photols.length; i++){
					if(i == 0){
						doc.setPhoto_1(photols[0]);
					}else if(i == 1){
						doc.setPhoto_2(photols[1]);
					}else if(i == 2){
						doc.setPhoto_3(photols[2]);
					}
				}
			}
			
			//草稿
			doc.setStatus("0");
			
			//医生备注
			if(doc.getQaMemo() != null && !doc.getQaMemo().equals("")){
				WebScDoc tmpDoc = new WebScDoc();
				tmpDoc.setQaMemo(doc.getQaMemo());
				tmpDoc.setDocumentId(doc.getDocumentId());
				docService.updateByPrimaryKey(tmpDoc);
			}
			
			int iRet = docTmpService.insert(doc);
			if(iRet > 0){
				return ResultBean.success();
			}else{
				return ResultBean.error("保存草稿失败！");
			}
		}catch(Exception e){
			e.printStackTrace();
			return ResultBean.error("保存草稿失败！");
		}
	}
	
	@OperationLog("保存草稿照片")
	@PostMapping("/save_surgicalrecordsPhoto")
    @ResponseBody
    public ResultBean save_surgicalrecordsPhoto(@Validated(Create.class) WebScDoc doc) {
		try{
			//照片
			doc.setPhoto_1("");
			doc.setPhoto_2("");
			doc.setPhoto_3("");
			String photo = doc.getPhoto();
			if(photo != null && !photo.equals("")){
				String[] photols = photo.split(",");
				for(int i = 0; i< photols.length; i++){
					if(i == 0){
						doc.setPhoto_1(photols[0]);
					}else if(i == 1){
						doc.setPhoto_2(photols[1]);
					}else if(i == 2){
						doc.setPhoto_3(photols[2]);
					}
				}
			}
			
			//草稿
			doc.setStatus("0");
			
			int iRet = docTmpService.update(doc);
			
			//医生备注
			WebScDoc tmpDoc = new WebScDoc();
			tmpDoc.setQaMemo(doc.getQaMemo() == null ? "" : doc.getQaMemo());
			tmpDoc.setDocumentId(doc.getDocumentId());
			docService.updateByPrimaryKey(tmpDoc);
			
			if(iRet > 0){
				return ResultBean.success();
			}else{
				return ResultBean.error("保存草稿失败！");
			}
		}catch(Exception e){
			e.printStackTrace();
			return ResultBean.error("保存草稿失败！");
		}
	}
	
	@OperationLog("提交订单")
	@PostMapping("/commit_surgicalrecords")
    @ResponseBody
    public ResultBean commit_surgicalrecords(@Validated(Create.class) WebScDoc doc) {
		try{
			System.out.println(doc.getShblEx());
			//数据整理
			//checkbox
			if(doc.getShblZw() != null)	
				doc.setShblZw("1");
			else
				doc.setShblZw("0");
			
			if(doc.getShblHbtt() != null)	
				doc.setShblHbtt("1");
			else
				doc.setShblHbtt("0");
			
			if(doc.getShblEx() != null)	
				doc.setShblEx("1");
			else
				doc.setShblEx("0");
			
			if(doc.getShblXy() != null)	
				doc.setShblXy("1");
			else
				doc.setShblXy("0");
			
			if(doc.getShblOt() != null)	
				doc.setShblOt("1");
			else
				doc.setShblOt("0");
			
			//照片
			doc.setPhoto_1("");
			doc.setPhoto_2("");
			doc.setPhoto_3("");
			String photo = doc.getPhoto();
			if(photo != null && !photo.trim().equals("")){
				String[] photols = photo.split(",");
				for(int i = 0; i< photols.length; i++){
					if(i == 0){
						doc.setPhoto_1(photols[0]);
					}else if(i == 1){
						doc.setPhoto_2(photols[1]);
					}else if(i == 2){
						doc.setPhoto_3(photols[2]);
					}
				}
			}else{
				return ResultBean.error("图片为空！");
			}
			
			//草稿
			doc.setStatus("1");
			
			//医生备注
			WebScDoc tmpDoc = new WebScDoc();
			tmpDoc.setQaMemo(doc.getQaMemo());
			tmpDoc.setDocumentId(doc.getDocumentId());
			tmpDoc.setDocumentState("4");
			docService.updateByPrimaryKey(tmpDoc);
			
			int iRet = docTmpService.insert(doc);
			if(iRet > 0){
				return ResultBean.success();
			}else{
				return ResultBean.error("提交失败！");
			}
		}catch(Exception e){
			e.printStackTrace();
			return ResultBean.error("提交失败！");
		}
	}
	
	/**
	 * 
	 * @Title: transferorder   
	 * @Description: TODO
	 * @param: @param documentId
	 * @param: @param model
	 * @param: @return      
	 * @return: String      
	 * @throws
	 */
	@GetMapping("/transferorder")
    public String transferorder(@RequestParam(value = "documentId") String documentId,
    				 	  		  Model model) {
		WebScDoc tmpD = new WebScDoc();
		//获取订单数据
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		doc.setIsHistroy("1");
		List<WebScDoc> docs = docService.selectWebScDocList(1, 1, doc);
		if(docs != null){
			//查询订单当日医院情况
			tmpD = docs.get(0);
		}
		model.addAttribute("doc", tmpD);
		
		//获取手术名称
		List<WebScOperative> operativels = data.getWebScOperativeList();
		model.addAttribute("operativels", operativels);
		//获取麻醉方法
		List<WebScAnesthetic> anestheticls = data.getWebScAnestheticList();
		model.addAttribute("anestheticls", anestheticls);
		model.addAttribute("type", "examine");
		return "doc/doc-transferorder";
	}
	
	@OperationLog("获取转单医生列表")
	@GetMapping("/transferuser")
    @ResponseBody
    public PageResultBean<WebScUser> transferuser(@RequestParam(value = "id") String documentId,
    											  @RequestParam(value = "name") String userName) {
		//当前用户信息
		WebScUser user = ShiroUtil.getCurrentUser();
				
    	List<WebScUser> userls = new ArrayList<WebScUser>();
		WebScDoc tmpD = new WebScDoc();
    	//获取订单数据
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		doc.setIsHistroy("1");
    	List<WebScDoc> docs = docService.selectWebScDocList(1, 1, doc);
		if(docs != null){
			//查询订单当日医院情况
			tmpD = docs.get(0);
			
			Map<String, String> searchMap = new HashMap<String, String>();
			searchMap.put("operateStartTime", tmpD.getOperateStartTime());
			searchMap.put("orgId", tmpD.getOrgId());
			searchMap.put("userName", userName);
			
			searchMap.put("NoUserId", user.getUserId() + "");
			
			userls = docService.getTransferUser(searchMap);
		}
		PageInfo<WebScUser> drPageInfo = new PageInfo<>(userls);
        return new PageResultBean<>(drPageInfo.getTotal(), drPageInfo.getList());
	}
	
	@OperationLog("确认转单")
    @PostMapping("/settransferorder")
    @ResponseBody
    public ResultBean settransferorder(@RequestParam(value = "doc_id") String docId,
    						  		  @RequestParam(value = "user_id") String userId,
    						  		  @RequestParam(value = "qa_user_id") String qaUserId) {
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(docId);
		doc.setQaUserId(userId);
		doc.setTransferUserId(qaUserId);
		doc.setDocumentState("3");
		doc.setOldDocumentState("3");
        return ResultBean.success(docService.updateByPrimaryKey(doc));
    }
	
	/**
	 * 
	 * @Title: remove   
	 * @Description: 跳转取消订单界面
	 * @param: @param documentId
	 * @param: @param model
	 * @param: @return      
	 * @return: String      
	 * @throws
	 */
	@GetMapping("/remove")
    public String remove(@RequestParam(value = "documentId") String documentId,
    				 	  		  Model model) {
		WebScDoc tmpD = new WebScDoc();
		//获取订单数据
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		doc.setIsHistroy("1");
		List<WebScDoc> docs = docService.selectWebScDocList(1, 1, doc);
		if(docs != null){
			//查询订单当日医院情况
			tmpD = docs.get(0);
		}
		model.addAttribute("doc", tmpD);
		
		//获取手术名称
		List<WebScOperative> operativels = data.getWebScOperativeList();
		model.addAttribute("operativels", operativels);
		//获取麻醉方法
		List<WebScAnesthetic> anestheticls = data.getWebScAnestheticList();
		model.addAttribute("anestheticls", anestheticls);
		return "doc/doc-remove";
	}
	
	@OperationLog("取消确认")
    @PostMapping("/setremove")
    @ResponseBody
    public ResultBean setremove(@RequestParam(value = "id") String documentId,
    							@RequestParam(value = "state") String documentState,
    							@RequestParam(value = "reason") String deleteReason) {
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		doc.setDocumentState("9");
		doc.setOldDocumentState(documentState);
		doc.setDeleteReason(deleteReason);
        return ResultBean.success(docService.updateByPrimaryKey(doc));
    }
	
	@OperationLog("确认撤销")
    @PostMapping("/completeRemove")
    @ResponseBody
    public ResultBean completeRemove(@RequestParam(value = "id") String documentId) {
		//当前用户信息
		WebScUser user = ShiroUtil.getCurrentUser();
		
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		doc.setDocumentState("6");
		doc.setAdminUserId(user.getUserId() + "");
        return ResultBean.success(docService.updateByPrimaryKey(doc));
    }
	
	@OperationLog("取消撤销")
    @PostMapping("/cannelRemove")
    @ResponseBody
    public ResultBean cannelRemove(@RequestParam(value = "id") String documentId,
    							   @RequestParam(value = "state") String documentState) {
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		doc.setDocumentState(documentState);
		doc.setOldDocumentState("");
		doc.setDeleteReason("");
        return ResultBean.success(docService.updateByPrimaryKey(doc));
    }
	
	@OperationLog("撤销接单")
    @PostMapping("/rtorder")
    @ResponseBody
    public ResultBean rtorder(@RequestParam(value = "id") String documentId,
    						  @RequestParam(value = "qaid") String transferUserId,
    						  @RequestParam(value = "state") String oldDocumentState) {
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		
		if(transferUserId != null && !transferUserId.equals("")){
			doc.setDocumentState(oldDocumentState);
			doc.setQaUserId(transferUserId);
			doc.setOldDocumentState("");
			doc.setTransferUserId("");
		}else{
			doc.setDocumentState("2");
		}
        return ResultBean.success(docService.updateByPrimaryKey(doc));
    }
	
	@GetMapping("/showTeam")
    public String showTeam(@RequestParam(value = "documentId") String documentId, Model model) {
		List<WebScUser> userls = qaTeamService.getQaTeamInfo(documentId);

		model.addAttribute("userls", userls);
		model.addAttribute("doc_id", documentId);
		
		return "doc/doc-qateam";
	}
	
	@OperationLog("获取团队人员列表")
	@GetMapping("/getQaUserInfo")
    @ResponseBody
    public PageResultBean<WebScUser> getQaUserInfo(@RequestParam(value = "id") String documentId,
    											  @RequestParam(value = "type") String roleId,
    											  @RequestParam(value = "name") String userName) {
		//当前用户信息
		WebScUser user = ShiroUtil.getCurrentUser();
				
    	List<WebScUser> userls = new ArrayList<WebScUser>();
    	//获取订单数据
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		doc.setIsHistroy("1");
    	List<WebScDoc> docs = docService.selectWebScDocList(1, 1, doc);
		if(docs != null){
			//查询订单当日医院情况
			WebScDoc tmpdoc = docs.get(0);
			
			Map<String, String> searchMap = new HashMap<String, String>();
			searchMap.put("userName", userName);
			searchMap.put("roleId", roleId);
			searchMap.put("province", tmpdoc.getOrgProvince());
			searchMap.put("NoUserId", user.getUserId() + "");
			
			userls = docService.getQaUserInfo(searchMap);
			
			if(userls != null && userls.size() > 0){
				List<WebScUser> ls = qaTeamService.getQaTeamInfo(tmpdoc.getQaTeamId());
				for(WebScUser u : ls){
					for(WebScUser tu : userls){
						if(tu.getUserId() == u.getUserId()){
							userls.remove(tu);
							break;
						}
					}
				}
			}
		}
		PageInfo<WebScUser> drPageInfo = new PageInfo<>(userls);
        return new PageResultBean<>(drPageInfo.getTotal(), drPageInfo.getList());
	}
	
	@PostMapping(value = "importDocs")
	@ResponseBody
	public ResultBean importDocs(@RequestParam("file") MultipartFile file) {
		ResultBean rBean = null;
		try {
			
			rBean = ResultBean.success(docService.importDocsService(file));
			
		} catch (Exception e) {
			log.error("订单批量导入失败，"+e.getMessage());
			rBean = ResultBean.error("订单批量导入失败，"+e.getMessage());
		}
		
		return rBean;
    }
	
	@PostMapping(value = "importDocsView")
	@ResponseBody
	public PageResultBean<WebScDoc> importDocsView(
			@RequestBody Map<String, Object> paraMap){
		
		PageResultBean<WebScDoc> prb = new PageResultBean<WebScDoc>();
		try {
			
			prb = docService.getImportDocsService(paraMap);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("获取导入订单记录失败，"+e.getMessage());
		}
		
		return prb;
	}
	
	@OperationLog("添加团队成员")
    @PostMapping("/addQaTeamUser")
    @ResponseBody
    public ResultBean addQaTeamUser(@RequestParam(value = "id") String documentId,
    						  		@RequestParam(value = "userId") String userId,
    						  		@RequestParam(value = "type") String roleId) {
		Map<String, String> insertMap = new HashMap<String, String>();
		insertMap.put("documentId", documentId);
		insertMap.put("userId", userId);
		insertMap.put("roleId", roleId);
		
        return ResultBean.success(qaTeamService.insertQaUser(insertMap));
    }
	
	@OperationLog("删除团队成员")
    @PostMapping("/delQaTeamUser")
    @ResponseBody
    public ResultBean delQaTeamUser(@RequestParam(value = "id") String documentId,
    						  		@RequestParam(value = "userId") String userId) {
		Map<String, String> deleteMap = new HashMap<String, String>();
		deleteMap.put("documentId", documentId);
		deleteMap.put("userId", userId);
		
        return ResultBean.success(qaTeamService.deleteQaUser(deleteMap));
    }
	
	@GetMapping("/drevaluation")
    public String drevaluation(@RequestParam(value = "documentId") String documentId, 
    						 @RequestParam(value = "type") String type, 
    						 Model model) {
		WebScDoc tmpdoc = new WebScDoc();
		
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		doc.setIsHistroy("1");
    	List<WebScDoc> docs = docService.selectWebScDocList(1, 1, doc);
		if(docs != null){
			//查询订单当日医院情况
			tmpdoc = docs.get(0);
		}
		
		model.addAttribute("type", type);
		model.addAttribute("doc", tmpdoc);
		
		return "doc/doc-evaluation";
	}
	
	@GetMapping("/jgevaluation")
    public String jgevaluation(@RequestParam(value = "documentId") String documentId, 
    						 @RequestParam(value = "type") String type, 
    						 Model model) {
		WebScDoc tmpdoc = new WebScDoc();
		
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		doc.setIsHistroy("1");
    	List<WebScDoc> docs = docService.selectWebScDocList(1, 1, doc);
		if(docs != null){
			//查询订单当日医院情况
			tmpdoc = docs.get(0);
		}
		
		model.addAttribute("type", type);
		model.addAttribute("doc", tmpdoc);
		
		return "doc/doc-evaluation";
	}
	
	@OperationLog("打分保存")
    @PostMapping("/save_evaluation")
    @ResponseBody
    public ResultBean save_evaluation(@RequestParam(value = "id") String documentId,
    						  		  @RequestParam(value = "evaluate") Float evaluate,
    						  		  @RequestParam(value = "memo") String memo,
    						  		  @RequestParam(value = "type") String type) {
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		if(type.equals("0")){
			doc.setHospitalEvaluate(evaluate);
			doc.setHospitalEvaluateMemo(memo);
		}else{
			doc.setDoctorEvaluate(evaluate);
			doc.setDoctorEvaluateMemo(memo);
		}
		
        return ResultBean.success(docService.updateByPrimaryKey(doc));
    }
	
	@OperationLog("完成订单")
    @PostMapping("/commit_completeorder")
    @ResponseBody
    public ResultBean commitCompleteOrder(@RequestParam(value = "id") String documentId) {
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		doc.setDocumentState("5");
        return ResultBean.success(docService.updateByPrimaryKey(doc));
    }
	
	@OperationLog("订单退回修改")
    @PostMapping("/return_completeorder")
    @ResponseBody
    public ResultBean returnCompleteOrder(@RequestParam(value = "id") String documentId,
    						  		      @RequestParam(value = "reason") String reason) {
		WebScDoc doc = new WebScDoc();
		doc.setDocumentId(documentId);
		doc.setAdminMemo(reason);
		doc.setDocumentState("3");
		doc.setStatus("0");
		
		docService.updateByPrimaryKey(doc);
		docTmpService.update(doc);
		
        return ResultBean.success(1);
    }
	
	@GetMapping("/history")
    public String history(Model model) {
		//获取医疗机构
		List<WebScOrganization> orgls = data.getWebScOrganizationList();
		model.addAttribute("orgls", orgls);
				
		return "doc/doc-history";
	}
	
	/**
	 * @Description: 历史订单列表导出
	 * @Title: getDocGridList
	 * @return 
	*/
	@RequestMapping("/exportHistoryDocList")
	@ResponseBody
    public void exportHistoryDocList(@RequestParam(value = "state") String state,
									 @RequestParam(value = "date", required=false) String date,
									 @RequestParam(value = "beginDate", required=false) String beginDate,
									 @RequestParam(value = "endDate", required=false) String endDate,
									 @RequestParam(value = "qaUserName", required=false) String qaUserName,
									 @RequestParam(value = "patientName", required=false) String patientName,
									 @RequestParam(value = "patienttypeId", required=false) String patienttypeId,
									 @RequestParam(value = "asa", required=false) String asa,
									 @RequestParam(value = "anestheticId", required=false) String anestheticId,
									 @RequestParam(value = "orgId", required=false) String orgId,
									 WebScDoc docQuery,
									 HttpServletRequest request, 
    								 HttpServletResponse response){ 
//		Map<String,Object> returnMap = new HashMap<String,Object>();
		response.setContentType("application/json;charset=UTF-8");
		
		try{
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
				//省，市，区   查询范围
				docQuery.setProvince(user.getProvince());
				docQuery.setCity(user.getCity());
				docQuery.setArea(user.getArea());
			}else if(roleId.equals("3")){
				//卫监局人员
				//省，市，区   查询范围
				docQuery.setProvince(user.getProvince());
				docQuery.setCity(user.getCity());
				docQuery.setArea(user.getArea());
			}else if(roleId.equals("4")){
				//区域管理人员
				//省，市，区   查询范围
				docQuery.setProvince(user.getProvince());
				docQuery.setCity(user.getCity());
				docQuery.setArea(user.getArea());
				
			}else if(roleId.equals("5")){
				//医生，查询主治医生
				docQuery.setQaUserId(String.valueOf(user.getUserId()));
				//省，市，区   查询范围
				docQuery.setProvince(user.getProvince());
				docQuery.setCity(user.getCity());
			}
			docQuery.setDocumentState(state);
			docQuery.setIsHistroy("1");
			//查询条件
			if(date != null && !date.trim().equals("")){
				docQuery.setOperateStartTime(date + " 00:00:00");
				docQuery.setOperateEndTime(date + " 23:59:59");
			}
			if(beginDate != null && !beginDate.trim().equals("")){
				docQuery.setOperateStartTime(beginDate + " 00:00:00");
			}
			if(endDate != null && !endDate.trim().equals("")){
				docQuery.setOperateEndTime(endDate + " 23:59:59");
			}
			if(qaUserName != null && !qaUserName.trim().equals("")){
				docQuery.setQaUserName(qaUserName);
			}
			if(patientName != null && !patientName.trim().equals("")){
				docQuery.setPatientName(patientName);
			}
			
			if(orgId != null && !orgId.equals("")){
				String orgStr[] = orgId.split(",");
				String orgVal = "";
				for(String o : orgStr){
					orgVal = orgVal + "'" + o + "',";
				}
				if(!orgVal.equals("")){
					orgVal = orgVal.substring(0, orgVal.length() - 1);
				}
				log.info(orgVal);
				docQuery.setOrgId(orgVal);
			}
			
			List<WebScDoc> docList = docService.selectWebScDocList(1, 10000, docQuery);
			
			//下载EXCEL
			View myView = new View();
			myView.readXLSX("/home/templet/HistoryRptTemplet.xlsx");
//			myView.readXLSX("D:/EXPORT_EXCEL/WebSc/HistoryRptTemplet.xlsx");
			int iRow = 1;
//			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			
			//数据转换
			for(WebScDoc wsd : docList){
				//数据处理
				if(wsd.getSskssj() != null && wsd.getSsjssj() != null){
					long min = getDistanceMin(wsd.getSskssj(), wsd.getSsjssj());
					wsd.setSssc((int)min);
				}else{
					wsd.setSssc(0);
				}
				
				//序号
				myView.setNumber(iRow, 0, iRow);
				//机构名称
				if(wsd.getOrgName() != null)
					myView.setText(iRow, 1, wsd.getOrgName());
				//日期
				if(wsd.getOperateStartTime() != null)
					myView.setText(iRow, 2, wsd.getOperateStartTime().substring(0, 10));
				//患者姓名
				if(wsd.getPatientName() != null)
					myView.setText(iRow, 3, wsd.getPatientName());
				//性别
				if(wsd.getPatientSex() != null){
					if(wsd.getPatientSex().equals("0")){
						myView.setText(iRow, 4, "女");
					}else{
						myView.setText(iRow, 4, "男");
					}
				}
				//年龄
				if(wsd.getPatientAge() != null)
					myView.setNumber(iRow, 5, wsd.getPatientAge());
				//诊断名称
				if(wsd.getDocumentTitle() != null)
					myView.setText(iRow, 6, wsd.getDocumentTitle());
				//手术名称
				if(wsd.getOperativeName() != null)
					myView.setText(iRow, 7, wsd.getOperativeName());
				//手术医生
				if(wsd.getOperateUser() != null)
					myView.setText(iRow, 8, wsd.getOperateUser());
				//开始时间
				if(wsd.getSskssj() != null)
					myView.setText(iRow, 9, wsd.getSskssj());
				//结束时间
				if(wsd.getSsjssj() != null)
					myView.setText(iRow, 10, wsd.getSsjssj());
				//时间差
				if(wsd.getSssc() != null)
					myView.setText(iRow, 11, wsd.getSsscVal());
				//麻醉方法
				if(wsd.getAnestheticName() != null)
					myView.setText(iRow, 12, wsd.getAnestheticName());
				//麻醉医生
				if(wsd.getQaUserName() != null)
					myView.setText(iRow, 13, wsd.getQaUserName());
				//镇痛治疗
				if(wsd.getShttzl() != null){
					if(wsd.getShttzl().equals("-1"))
						myView.setText(iRow, 14, "无");
					else if(wsd.getShttzl().equals("1"))
						myView.setText(iRow, 14, "口服药");
					else if(wsd.getShttzl().equals("2"))
						myView.setText(iRow, 14, "静脉推注");
					else if(wsd.getShttzl().equals("3"))
						myView.setText(iRow, 14, "静脉镇痛泵");
					else if(wsd.getShttzl().equals("4"))
						myView.setText(iRow, 14, "椎管内镇痛泵");
					else if(wsd.getShttzl().equals("5"))
						myView.setText(iRow, 14, "无镇痛");
				}
				//局麻效果
				if(wsd.getJmjs() != null){
					if(wsd.getJmjs().equals("0"))
						myView.setText(iRow, 15, "好");
					else if(wsd.getJmjs().equals("1"))
						myView.setText(iRow, 15, "一般");
					else if(wsd.getJmjs().equals("2"))
						myView.setText(iRow, 15, "差");
					else if(wsd.getJmjs().equals("3"))
						myView.setText(iRow, 15, "拒绝实施局麻");
				}
				//患者身高
				if(wsd.getHeight() != null)
					myView.setNumber(iRow, 16, wsd.getHeight());
				//患者体重
				if(wsd.getWeight() != null)
					myView.setNumber(iRow, 17, wsd.getWeight());
				//ASA分级
				if(wsd.getAsa() != null){
					if(wsd.getAsa().equals("0"))
						myView.setText(iRow, 18, "I 级");
					else if(wsd.getAsa().equals("1"))
						myView.setText(iRow, 18, "II 级");
					else if(wsd.getAsa().equals("2"))
						myView.setText(iRow, 18, "III 级");
					else if(wsd.getAsa().equals("3"))
						myView.setText(iRow, 18, "IV 级");
					else if(wsd.getAsa().equals("4"))
						myView.setText(iRow, 18, "V 级");
					else if(wsd.getAsa().equals("5"))
						myView.setText(iRow, 18, "E 级");
				}
				//系统病史
				if(wsd.getXtbs() != null)
					myView.setText(iRow, 19, wsd.getXtbs());
				//手术病史
				if(wsd.getShs() != null){
					if(wsd.getShs().equals("0"))
						myView.setText(iRow, 20, "无");
					else if(wsd.getShs().equals("1")){
						if(wsd.getShcs() != null){
							myView.setText(iRow, 20,wsd.getShcs() + " 次");
						}else{
							myView.setText(iRow, 20, "0 次");
						}
					}
				}
					
				//意外事件呼吸
				if(wsd.getYwsjHxxt() != null){
					if(wsd.getYwsjHxxt().equals("-1"))
						myView.setText(iRow, 21, "无");
					else if(wsd.getYwsjHxxt().equals("0"))
						myView.setText(iRow, 21, "频繁舌后坠（≥3次）");
					else if(wsd.getYwsjHxxt().equals("1"))
						myView.setText(iRow, 21, "喉痉挛");
					else if(wsd.getYwsjHxxt().equals("2"))
						myView.setText(iRow, 21, "返流");
					else if(wsd.getYwsjHxxt().equals("3"))
						myView.setText(iRow, 21, "误吸");
					else if(wsd.getYwsjHxxt().equals("4"))
						myView.setText(iRow, 21, "支气管痉挛");
					else if(wsd.getYwsjHxxt().equals("5"))
						myView.setText(iRow, 21, "计划外插管");
					else if(wsd.getYwsjHxxt().equals("6"))
						myView.setText(iRow, 21, "其他 ：" + wsd.getYwsjHxxtQt());
				}
				//意外事件循环
				if(wsd.getYwsjXhxt() != null){
					if(wsd.getYwsjXhxt().equals("-1"))
						myView.setText(iRow, 22, "无");
					else if(wsd.getYwsjXhxt().equals("0"))
						myView.setText(iRow, 22, "需纠正的低血压");
					else if(wsd.getYwsjXhxt().equals("1"))
						myView.setText(iRow, 22, "需纠正的高血压");
					else if(wsd.getYwsjXhxt().equals("2"))
						myView.setText(iRow, 22, "肺动脉栓塞");
					else if(wsd.getYwsjXhxt().equals("3"))
						myView.setText(iRow, 22, "心梗");
					else if(wsd.getYwsjXhxt().equals("4"))
						myView.setText(iRow, 22, "脑梗");
					else if(wsd.getYwsjXhxt().equals("5"))
						myView.setText(iRow, 22, "其他 ：" + wsd.getYwsjXhxtQt());
				}
				//意外事件过敏
				if(wsd.getYwsjGm() != null){
					if(wsd.getYwsjGm().equals("-1"))
						myView.setText(iRow, 23, "无");
					else if(wsd.getYwsjGm().equals("1"))
						myView.setText(iRow, 23, "no");
					else if(wsd.getYwsjGm().equals("2"))
						myView.setText(iRow, 23, "yes ：" + wsd.getYwsjGmAocre());
				}
				//术后不良反应
				String fy = "";
				if(wsd.getShblZw() != null && !wsd.getShblZw().trim().equals(""))
					fy = "谵妄";
				if(wsd.getShblHbtt() != null && !wsd.getShblHbtt().trim().equals("")){
					if(fy.trim().equals(""))
						fy = "喉部疼痛";
					else
						fy = ",喉部疼痛";
				}
				if(wsd.getShblEx() != null && !wsd.getShblEx().trim().equals("")){
					if(fy.trim().equals(""))
						fy = "恶心";
					else
						fy = ",恶心";
				}
				if(wsd.getShblXy() != null && !wsd.getShblXy().trim().equals("")){
					if(fy.trim().equals(""))
						fy = "眩晕";
					else
						fy = ",眩晕";
				}
				if(wsd.getShblOt() != null && !wsd.getShblOt().trim().equals("")){
					if(fy.trim().equals(""))
						fy = "呕吐";
					else
						fy = ",呕吐";
				}
				myView.setText(iRow, 24, fy);
				//机构评分
				if(wsd.getHospitalEvaluate() != null){
					if(wsd.getHospitalEvaluate() == 1)
						myView.setText(iRow, 25, "很差");
					else if(wsd.getHospitalEvaluate() == 2)
						myView.setText(iRow, 25, "差");
					else if(wsd.getHospitalEvaluate() == 3)
						myView.setText(iRow, 25, "还行");
					else if(wsd.getHospitalEvaluate() == 4)
						myView.setText(iRow, 25, "好");
					else if(wsd.getHospitalEvaluate() == 5)
						myView.setText(iRow, 25, "很好");
				}
				//机构评价
				if(wsd.getHospitalEvaluateMemo() != null)
					myView.setText(iRow, 26, wsd.getHospitalEvaluateMemo());
				//医生评分
				if(wsd.getDoctorEvaluate() != null){
					if(wsd.getDoctorEvaluate() == 1)
						myView.setText(iRow, 27, "很差");
					else if(wsd.getDoctorEvaluate() == 2)
						myView.setText(iRow, 27, "差");
					else if(wsd.getDoctorEvaluate() == 3)
						myView.setText(iRow, 27, "还行");
					else if(wsd.getDoctorEvaluate() == 4)
						myView.setText(iRow, 27, "好");
					else if(wsd.getDoctorEvaluate() == 5)
						myView.setText(iRow, 27, "很好");
				}
				//医生评价
				if(wsd.getDoctorEvaluateMemo() != null)
					myView.setText(iRow, 28, wsd.getDoctorEvaluateMemo());
				
				iRow++;
			}
			OutputStream out = response.getOutputStream();
			response.reset();
			response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String filename = "历史订单清单下载.xlsx";
            if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0)
            	filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");	//firefox浏览器
            else if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0)
            	filename = URLEncoder.encode(filename, "UTF-8");	//IE浏览器
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            
            myView.writeXLSX(out);
            out.flush();
            out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static long getDistanceMin(String str1, String str2) throws Exception{
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long days=0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            days = diff / (1000 * 60);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return days;
    }
}