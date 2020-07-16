package sc.system.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import sc.common.constants.DocumentTitleEnum;
import sc.common.util.DateUtils;
import sc.common.util.ExcelUtil;
import sc.common.util.ShiroUtil;
import sc.common.util.StringUtil;
import sc.common.util.UUID19;
import sc.system.mapper.AnestheticMapper;
import sc.system.mapper.DocMapper;
import sc.system.mapper.OperativeMapper;
import sc.system.mapper.OrganizationMapper;
import sc.system.model.WebScAnesthetic;
import sc.system.model.WebScDoc;
import sc.system.model.WebScOperative;
import sc.system.model.WebScOrganization;
import sc.system.model.WebScUser;
import sc.system.model.WebScUser_Distribution;

import com.github.pagehelper.PageHelper;

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
    	return docMapper.selectWebScDocList(doc);
    }
    
    public List<WebScUser> getDistributionDrGridList(String documentId, String province){
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
	    	searchMap.put("province", province);
	    	searchMap.put("org_id",org.getOrgId());
	    	searchMap.put("operate_start_time", doc.getOperateStartTime());
	    	//获取医生信息
	    	List<WebScUser_Distribution> drList = docMapper.getDistributionDrGridList(searchMap);
    	
    	}
    	return null;
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
    					
    					if(StringUtil.isEmpty(rows.get(i).get(j).toString())) {
    			    		throw new Exception("患者性别不能为空");
    			    	}
    					
    					doc.setPatientSex(rows.get(i).get(j).toString());
    				}else if (DocumentTitleEnum.patient_num.getTxt().equals(rows.get(0).get(j))) {
    					
    					if(StringUtil.isEmpty(rows.get(i).get(j).toString())) {
    			    		throw new Exception("住院号不能为空");
    			    	}
    					
    					doc.setPatientNum(rows.get(i).get(j).toString());
    				}else if (DocumentTitleEnum.patient_bednum.getTxt().equals(rows.get(0).get(j))) {
    					
    					if(StringUtil.isEmpty(rows.get(i).get(j).toString())) {
    			    		throw new Exception("病床号不能为空");
    			    	}
    					
    					doc.setPatientBednum(rows.get(i).get(j).toString());
    				}else if (DocumentTitleEnum.operative_name.getTxt().equals(rows.get(0).get(j))) {
    					
    					if(StringUtil.isEmpty(rows.get(i).get(j).toString())) {
							throw new Exception("手术名称不能为空");
						}
    					
    					//多个手术名称以"，"（中文逗号）分隔
    					String operativeNames[] = rows.get(i).get(j).toString().split("，");
    					String operativeIds = "";
    					for (String operativeName : operativeNames) {
    						WebScOperative operative = operativeMapper.selectOperative(operativeName.trim());
    						if(StringUtil.isNull(operative)) {
    							throw new Exception("手术名“"+operativeName+"”有误");
    						}
    						operativeIds = operativeIds + "," + operative.getOperativeId();
						}
    					
						doc.setOperativeId(operativeIds.substring(1));
					}else if (DocumentTitleEnum.operate_start_time.getTxt().equals(rows.get(0).get(j))) {
						
						if(StringUtil.isEmpty(rows.get(i).get(j).toString())) {
							throw new Exception("手术时间不能为空");
						}
						
						doc.setOperateStartTime(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", (Date)rows.get(i).get(j)));
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
    			
    			//根据“患者姓名+手术日期+机构”判断是否为重复订单
    			if("1".equals(docMapper.isExists(
    					doc.getPatientName(), doc.getOperateStartTime(), doc.getOrgId(), doc.getOperativeId()))) {
    				throw new Exception("订单已经存在");
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
    
}
