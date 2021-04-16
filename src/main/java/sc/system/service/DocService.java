package sc.system.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import sc.common.constants.DocumentTitleEnum;
import sc.common.constants.SexEnum;
import sc.common.util.DateUtils;
import sc.common.util.ExcelUtil;
import sc.common.util.PageResultBean;
import sc.common.util.ShiroUtil;
import sc.common.util.StringUtil;
import sc.common.util.UUID19;
import sc.common.util.UnixtimeUtil;
import sc.system.mapper.AnestheticMapper;
import sc.system.mapper.DocMapper;
import sc.system.mapper.OperativeMapper;
import sc.system.mapper.OrganizationMapper;
import sc.system.model.StateCount;
import sc.system.mapper.UserMapper;
import sc.system.model.WebScAnesthetic;
import sc.system.model.WebScDoc;
import sc.system.model.WebScOperative;
import sc.system.model.WebScOrganization;
import sc.system.model.WebScUser;
import sc.system.model.WebScUser_Distribution;
import sc.system.model.vo.District;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class DocService {
	@Resource
	private DocMapper docMapper;
	@Resource
	private OperativeMapper operativeMapper;
	@Resource
	private AnestheticMapper anestheticMapper;
	@Resource
	private OrganizationMapper organizationMapper;
	@Resource
	private UserMapper userMapper;
	
	@Autowired
	private District district;
	
	public int insert(WebScDoc doc) {
        return docMapper.insert(doc);
    }

    public int deleteByPrimaryKey(String documentId) {
        return docMapper.deleteByPrimaryKey(documentId);
    }

    public int updateByPrimaryKey(WebScDoc doc) {
    	return docMapper.updateByPrimaryKey(doc);
    }

    public WebScDoc selectByPrimaryKey(String documentId) {
        return docMapper.selectByPrimaryKey(documentId);
    }
    
    public List<WebScDoc> selectWebScDocList(int page, int rows, WebScDoc doc){
    	PageHelper.startPage(page, rows);
    	List<WebScDoc> ls = docMapper.selectWebScDocList(doc);
    	for(WebScDoc d : ls){
    		if(d.getStatus() != null && !d.getStatus().equals("")){
    			if(d.getTmpPatientName() != null)
    				d.setPatientName(d.getTmpPatientName());
    			if(d.getTmpPatientAge() != null)
    				d.setPatientAge(d.getTmpPatientAge());
    			if(d.getTmpPatientSex() != null)
    				d.setPatientSex(d.getTmpPatientSex());
    			if(d.getTmpOperativeId() != null)
    				d.setOperativeId(d.getTmpOperativeId());
    			if(d.getTmpOperativeName() != null)
    				d.setOperativeName(d.getTmpOperativeName());
    			if(d.getTmpAnestheticId() != null)
    				d.setAnestheticId(d.getTmpAnestheticId());
    			if(d.getTmpAnestheticName() != null)
    				d.setAnestheticName(d.getTmpAnestheticName());

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
    		}
    	}
    	
    	return ls;
    }
    
    public StateCount getStateCount(WebScDoc doc){
    	StateCount sc = new StateCount();
    	List<WebScDoc> ls = docMapper.selectWebScDocList(doc);
    	
    	for(WebScDoc td : ls){
    		if(td.getDocumentState().equals("0")){
    			sc.setiCount_0(sc.getiCount_0() + 1);
    		}else if(td.getDocumentState().equals("1")){
    			sc.setiCount_1(sc.getiCount_1() + 1);
    			sc.setiCount_123(sc.getiCount_123() + 1);
    		}else if(td.getDocumentState().equals("2")){
    			sc.setiCount_2(sc.getiCount_2() + 1);
    			sc.setiCount_123(sc.getiCount_123() + 1);
    		}else if(td.getDocumentState().equals("3")){
    			sc.setiCount_3(sc.getiCount_3() + 1);
    			sc.setiCount_123(sc.getiCount_123() + 1);
    		}else if(td.getDocumentState().equals("4")){
    			sc.setiCount_4(sc.getiCount_4() + 1);
    		}else if(td.getDocumentState().equals("9")){
    			sc.setiCount_9(sc.getiCount_9() + 1);
    		}
    	}
    	
    	return sc;
    }
    
    public List<WebScUser_Distribution> getDistributionDrGridList(String documentId, String qaName, WebScUser user){
    	List<WebScUser_Distribution> drList = new ArrayList<WebScUser_Distribution>();
    	
    	//获取订单数据
    	WebScDoc doc = new WebScDoc();
    	doc.setDocumentId(documentId);
    	List<WebScDoc> docList = docMapper.selectWebScDocList(doc);
    	if(docList != null){
	    	doc = docList.get(0);
	    	
	    	//获取医院信息
	    	WebScOrganization org = docMapper.findWebScDocOrg(documentId);
	    	
	    	//获取当天医院匹配的医生
	    	
	    	List<String> uls = docMapper.getWorkDr(doc.getOperateStartTime());
	    	
	    	//创建查询条件
	    	Map<String, String> searchMap = new HashMap<String, String>();
	    	searchMap.put("qaName", qaName);
	    	searchMap.put("province", user.getProvince());
	    	searchMap.put("org_id",org.getOrgId());
	    	searchMap.put("operate_start_time", doc.getOperateStartTime());
	    	//获取医生信息
	    	drList = docMapper.getDistributionDrGridList(searchMap);
	    	
	    	for(WebScUser_Distribution duser : drList){
	    		int iDistributionScore = 0;
	    		int iScope = 0;
	    		//匹配当日医院
	    		duser.setIswork("0");
	    		if(uls != null && uls.size() > 0){
	    			for(String userid : uls){
	    				if(duser.getUserId().equals(userid)){
	    					duser.setIswork("1");
	    					iDistributionScore += 3;
	    					break;
	    				}
	    			}
	    		}
	    		//当日有空
	    		if(duser.getIscalendar() != null && !duser.getIscalendar().equals("")){
	    			iDistributionScore += 2;
	    			duser.setIscalendar("1");
	    		}else{
	    			duser.setIscalendar("0");
	    		}
	    		//备案
	    		if(duser.getIsrecord() != null && !duser.getIsrecord().equals("")){
	    			iDistributionScore += 2;
	    			duser.setIscalendar("1");
	    		}else{
	    			duser.setIsrecord("0");
	    		}
	    		
	    		//所在区域范围
	    		if(org.getProvince().equals(duser.getProvince())){
	    			iDistributionScore += 1;
	    			iScope += 1;
	    		}
	    		
	    		if(org.getCity() != null && duser.getCity() != null){
	    			if(org.getCity().equals(duser.getCity())){
	    				iDistributionScore += 1;
		    			iScope += 1;
	    			}
	    		}
	    		
	    		if(org.getArea() != null && duser.getArea() != null){
	    			if(org.getArea().equals(duser.getArea())){
	    				iDistributionScore += 1;
		    			iScope += 1;
	    			}
	    		}
	    		duser.setIsScope(iScope + "");
	    		
	    		//病人类型
	    		if(duser.getPatient_type() != null){
	    			//订单病人类型
	    			int age = doc.getPatientAge();
	    			String docPatType = "0";
	    			if(age <= 16) docPatType = "1";
	    			else if(age > 16 && age <= 60) docPatType = "2";
	    			else docPatType = "3";
	    			
	    			duser.setIsPatient("0");
	    			String[] pat = duser.getPatient_type().split(",");
	    			for(String str : pat){
	    				if(str.equals(docPatType)){
	    					duser.setIsPatient("1");
	    					iDistributionScore += 1;
	    					break;
	    				}
	    			}
	    		}
	    		
	    		//省/市/区 转换成中文
	    		if(duser.getProvince() != null && !duser.getProvince().equals(""))
	    			duser.setProvince(district.getDistrictMap().get(duser.getProvince()).getName());
	    		if(duser.getCity() != null && !duser.getCity().equals(""))
	    			duser.setCity(district.getDistrictMap().get(duser.getCity()).getName());
	    		if(duser.getArea() != null && !duser.getArea().equals(""))
	    			duser.setArea(district.getDistrictMap().get(duser.getArea()).getName());
	    		
	    		duser.setDistributionScore(iDistributionScore);
	    	}
	    	
	    	//list 排序
	    	Collections.sort(drList, new Comparator<WebScUser_Distribution>() {  
	    		  
	            @Override  
	            public int compare(WebScUser_Distribution o1, WebScUser_Distribution o2) {  
	                int i = o1.getDistributionScore() - o2.getDistributionScore();
	                if(i == 0){
	                	return o1.getUserName().compareTo(o2.getUserName());
	                }
	                return -i;  
	            }  
	        });
    	}
    	return drList;
    }
    
    public List<WebScUser> getTransferUser(Map<String, String> searchMap){
    	return docMapper.getTransferUser(searchMap);
    }
    
    public String importDocsService(MultipartFile file) throws Exception {
    	//1、判断文件类型，只能导入excel文件
		
    	//2、解析excel文件
    	List<List<Object>> rows = ExcelUtil.readExcel(file.getInputStream());
    	//3、校验内容
    	//header校验
    	for (int i = 0; i < rows.get(0).size(); i++) {
    		if (StringUtil.isNull(DocumentTitleEnum.txtOf(rows.get(0).get(i).toString()))) {
    			throw new Exception("第"+(i+1)+"列标题内容不正确");
    		};
    	}
    	//body校验
    	String msg = "";
    	List<WebScDoc> docs = new ArrayList<WebScDoc>();
    	for (int i = 1; i < rows.size(); i++) {
    		try {
    			WebScDoc doc = new WebScDoc();
    			String operateDate = "";
				String operateTime = "";
    			for (int j = 0; j < rows.get(i).size(); j++) {
    				if (DocumentTitleEnum.patient_name.getTxt().equals(rows.get(0).get(j))) {
    					
    					if(StringUtil.isEmpty(rows.get(i).get(j).toString())) {
    			    		throw new Exception("患者姓名不能为空");
    			    	}
    					
    					doc.setPatientName(rows.get(i).get(j).toString());
    				}else if (DocumentTitleEnum.patient_age.getTxt().equals(rows.get(0).get(j))) {
    					
    					if(StringUtil.isNull(rows.get(i).get(j))) {
    			    		throw new Exception("患者年龄不能为空");
    			    	}
    					
    					doc.setPatientAge(new Double((double)rows.get(i).get(j)).intValue());
    				}else if (DocumentTitleEnum.patient_sex.getTxt().equals(rows.get(0).get(j))) {
    					
    					if(StringUtil.isNull(SexEnum.txtOf(rows.get(i).get(j).toString()))) {
    						throw new Exception("患者性别为空或有误");
    					}
    					
    					doc.setPatientSex(SexEnum.txtOf(rows.get(i).get(j).toString()).getValue());
    				}else if (DocumentTitleEnum.patient_num.getTxt().equals(rows.get(0).get(j))) {
    					
    					doc.setPatientNum(rows.get(i).get(j).toString());
    				}else if (DocumentTitleEnum.patient_bednum.getTxt().equals(rows.get(0).get(j))) {
    					
    					doc.setPatientBednum(rows.get(i).get(j).toString());
    				}else if (DocumentTitleEnum.operative_name.getTxt().equals(rows.get(0).get(j))) {
    					
    					if(StringUtil.isEmpty(rows.get(i).get(j).toString())) {
							throw new Exception("手术名称不能为空");
						}
    					
    					//多个手术名称以";"（英文分号）分隔
    					String operativeNames[] = rows.get(i).get(j).toString().split(";");
    					String operativeIds = "";
    					for (String operativeName : operativeNames) {
    						WebScOperative operative = operativeMapper.selectOperative(operativeName.trim());
    						if(StringUtil.isNull(operative)) {
    							throw new Exception("手术名“"+operativeName+"”有误");
    						}
    						operativeIds = operativeIds + "," + operative.getOperativeId();
						}
    					
						doc.setOperativeId(operativeIds.substring(1));
					}else if (DocumentTitleEnum.operate_date.getTxt().equals(rows.get(0).get(j))) {
						
						if(StringUtil.isEmpty(rows.get(i).get(j).toString())) {
							throw new Exception("手术日期不能为空");
						}
						System.out.println("手术日期："+rows.get(i).get(j));
						operateDate = rows.get(i).get(j).toString();
//						doc.setOperateStartTime(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", (Date)rows.get(i).get(j)));
					}else if (DocumentTitleEnum.operate_time.getTxt().equals(rows.get(0).get(j))) {
						
						if(StringUtil.isEmpty(rows.get(i).get(j).toString())) {
							throw new Exception("手术时间不能为空");
						}
						System.out.println("手术时间："+rows.get(i).get(j));
//						
						operateTime = rows.get(i).get(j).toString();
					}else if (DocumentTitleEnum.operate_user.getTxt().equals(rows.get(0).get(j))) {
						
						if(StringUtil.isEmpty(rows.get(i).get(j).toString())) {
							throw new Exception("手术医生不能为空");
						}
						
						doc.setOperateUser(rows.get(i).get(j).toString());
					}else if (DocumentTitleEnum.operate_aide.getTxt().equals(rows.get(0).get(j))) {
						doc.setOperateQide(rows.get(i).get(j).toString());
					}else if (DocumentTitleEnum.document_title.getTxt().equals(rows.get(0).get(j))) {
						
						if(StringUtil.isEmpty(rows.get(i).get(j).toString())) {
							throw new Exception("诊断不能为空");
						}
						
						doc.setDocumentTitle(rows.get(i).get(j).toString());
					}else if (DocumentTitleEnum.anesthetic_name.getTxt().equals(rows.get(0).get(j))) {
						
						if(StringUtil.isEmpty(rows.get(i).get(j).toString())) {
							throw new Exception("麻醉方法不能为空");
						}
						
						WebScAnesthetic anesthetic = anestheticMapper.selectAnesthetic(rows.get(i).get(j).toString().trim());
						if(StringUtil.isNull(anesthetic)) {
							throw new Exception("麻醉方法“"+rows.get(i).get(j).toString()+"”有误");
						}
						
						doc.setAnestheticId(anesthetic.getAnestheticId());
					}else if (DocumentTitleEnum.org_name.getTxt().equals(rows.get(0).get(j))) {
						
						if(StringUtil.isEmpty(rows.get(i).get(j).toString())) {
							throw new Exception("医疗机构不能为空");
						}
						
						WebScOrganization org = organizationMapper.selectWebScOrganization(rows.get(i).get(j).toString().trim());
						if (StringUtil.isNull(org)) {
							throw new Exception("医疗机构“"+rows.get(i).get(j).toString()+"”未注册");
						}
						
						doc.setOrgId(org.getOrgId());
					}else if (DocumentTitleEnum.memo.getTxt().equals(rows.get(0).get(j))) {
						doc.setMemo(rows.get(i).get(j).toString());
					}
    			}
    			
    			if(DateUtils.isValidDate(operateDate +" "+ operateTime,"yyyy-MM-dd HH:mm")) {
					doc.setOperateStartTime(operateDate +" "+ operateTime);
				}else {
					throw new Exception("手术日期或时间格式有误“"+operateDate +" "+ operateTime+"”，请调整为正确的格式（yyyy-MM-dd HH:mm）");
				}
    			
    			//根据“患者姓名+手术日期+机构”判断是否为重复订单
    			if("1".equals(docMapper.isExists(
    					doc.getPatientName(), doc.getOperateStartTime(), doc.getOrgId(), doc.getOperativeId()))) {
    				throw new Exception("订单已经存在");
    			}
    			
    			//是否是紧急订单，1：紧急，0：不急
    			Date operativeDate = DateUtils.parseDate(doc.getOperateStartTime());
    			if(UnixtimeUtil.getUnixHour(new Date().getTime())+24>=
    			UnixtimeUtil.getUnixHour(operativeDate.getTime())) {
    				doc.setDocumentType("1");
    			}else {
    				doc.setDocumentType("0");
    			}
    			
    			//病人类型，1：未成年人，2：中年人，3：老年人
    			if (doc.getPatientAge()<=16) {
    				doc.setPatienttypeId("1");
    			}else if (doc.getPatientAge()>16&&doc.getPatientAge()<=60) {
    				doc.setPatienttypeId("2");
    			}else {
    				doc.setPatienttypeId("3");
    			}
    			
    			doc.setApplyUserId(ShiroUtil.getCurrentUser().getUserId()+"");
    			doc.setDocumentState("0");
    			doc.setDocumentId(UUID19.uuid());
    			docs.add(doc);
    		} catch (Exception e) {
    			e.printStackTrace();
    			msg = msg +"</br>第"+(i)+"行，"+e.getMessage();
    		}
    	}
    			
    	//4、数据持久化
    	for (WebScDoc webScDoc : docs) {
			docMapper.insert(webScDoc);
		}
    	
    	return "成功导入"+docs.size()+"条，失败"+(rows.size()-docs.size()-1)+"条："+msg;
    }
    
    public PageResultBean<WebScDoc> getImportDocsService(Map<String, Object> paraMap){
    	
    	PageResultBean<WebScDoc> prb = new PageResultBean<WebScDoc>();
		
		PageHelper.startPage(paraMap.get("page")==null?1:(int)paraMap.get("page"), 
				paraMap.get("limit")==null?10:(int)paraMap.get("limit"));
		
		if(StringUtil.isNotNull(paraMap.get("orgName"))) {
			WebScOrganization webScOrganization = organizationMapper.selectWebScOrganization(paraMap.get("orgName").toString());
			
			paraMap.put("orgId", webScOrganization != null ? webScOrganization.getOrgId() : "");
		}
		
		paraMap.put("userId", ShiroUtil.getCurrentUser().getUserId()+"");
		
		List<WebScDoc> importDocs = docMapper.selectImportDocsByConditions(paraMap);
		
		for (WebScDoc webScDoc : importDocs) {
			webScDoc.setPatientSex(SexEnum.getvalueOf(webScDoc.getPatientSex()).getTxt());
			webScDoc.setOrgName(organizationMapper.selectByPrimaryKey(webScDoc.getOrgId()).getOrgName());
			webScDoc.setAnestheticName(anestheticMapper.selectAnestheticById(webScDoc.getAnestheticId()).getAnestheticName());
			
			String operativeIds [] = webScDoc.getOperativeId().split(",");
			String operativeNames = "";
			for (int i = 0; i < operativeIds.length; i++) {
				operativeNames = operativeNames + "，" + operativeMapper.selectOperativeById(operativeIds[i]).getOperativeName();
			}
			webScDoc.setOperativeName(operativeNames.substring(1));
		}
		
		PageInfo<WebScDoc> pageInfo = new PageInfo<>(importDocs);
		
		prb.setCount(Long.valueOf(pageInfo.getTotal()).intValue());
		prb.setData(importDocs);
		
		return prb;
    }
    
    public List<WebScUser> getQaUserInfo(Map<String, String> searchMap){
    	return docMapper.getQaUserInfo(searchMap);
    }
    
}
