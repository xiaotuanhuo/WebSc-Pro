package sc.system.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import sc.common.constants.RecordTitleEnum;
import sc.common.util.ExcelUtil;
import sc.common.util.MailTelValidateUtil;
import sc.common.util.PageResultBean;
import sc.common.util.StringUtil;
import sc.system.mapper.OrganizationMapper;
import sc.system.mapper.RecordMapper;
import sc.system.mapper.UserMapper;
import sc.system.model.Record;
import sc.system.model.WebScOrganization;
import sc.system.model.WebScUser;

@Service
public class RecordService {
	private static final Logger logger = LoggerFactory.getLogger(RecordService.class);
	
	@Resource
	RecordMapper recordMapper;
	@Resource
	UserMapper userMapper;
	@Resource
	OrganizationMapper organizationMapper;
	
	public PageResultBean<Record> getDoctorRecordsService(Map<String, Object> paraMap){

		PageResultBean<Record> prb = new PageResultBean<Record>();
		
		PageHelper.startPage(paraMap.get("page")==null?1:(int)paraMap.get("page"), 
				paraMap.get("limit")==null?10:(int)paraMap.get("limit"));
		
		List<Record> records = recordMapper.selectRecordByConditions(paraMap);
		
		PageInfo<Record> pageInfo = new PageInfo<>(records);
		
		prb.setCount(Long.valueOf(pageInfo.getTotal()).intValue());
		prb.setData(records);
		
		return prb;
	}
	
	/**
	 * 编辑医生备案信息
	 * @param record
	 * @return
	 * @throws Exception 
	 */
	public String editRecordService(Record record) throws Exception {
		
		record = checkRecord(record);
		
		recordMapper.updateByPrimaryKey(record);
		
		return "医生备案信息修改成功";
	}
	
	/**
	 * 增加医生备案信息
	 * @param record
	 * @return
	 * @throws Exception 
	 */
	public String addRecordService(Record record) throws Exception {
		
		record = checkRecord(record);
		
		recordMapper.insert(record);
		
		return "医生备案信息添加成功";
	}
	
	/**
	 * 医生备案信息批量导入
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public String importRecordsService(MultipartFile file) throws Exception {
		//1、判断文件类型，只能导入excel文件
		
		//2、解析excel文件
		List<List<Object>> rows = ExcelUtil.readExcel(file.getInputStream());
		//3、校验内容
		//header校验
		for (int i = 0; i < rows.get(0).size(); i++) {
			if (StringUtil.isNull(RecordTitleEnum.txtOf(rows.get(0).get(i).toString()))) {
				throw new Exception("第"+(i+1)+"列标题内容不正确");
			};
		}
		//body校验
		String msg = "";
		List<Record> records = new ArrayList<Record>();
		for (int i = 1; i < rows.size(); i++) {
			try {
				Record record = new Record();
				for (int j = 0; j < rows.get(i).size(); j++) {
					if (RecordTitleEnum.DOCTOR_NAME.getTxt().equals(rows.get(0).get(j))) {
						record.setDoctorName(rows.get(i).get(j).toString());
					}else if (RecordTitleEnum.DOCTOR_PHONE.getTxt().equals(rows.get(0).get(j))) {
						record.setDoctorPhone(rows.get(i).get(j).toString());
					}else if (RecordTitleEnum.ORG_NAME.getTxt().equals(rows.get(0).get(j))) {
						record.setOrgName(rows.get(i).get(j).toString());
					}else if (RecordTitleEnum.RECORD_DATE.getTxt().equals(rows.get(0).get(j))) {
						record.setRecordDate((Date)rows.get(i).get(j));
					}else if (RecordTitleEnum.END_DATE.getTxt().equals(rows.get(0).get(j))) {
						record.setEndDate((Date)rows.get(i).get(j));
					}
				}
				
				//校验
				record = checkRecord(record);
				
				records.add(record);
			} catch (Exception e) {
				msg = msg +"</br>第"+(i)+"行，"+e.getMessage();
			}
		}
		
		logger.info("records : "+records);
		logger.info("msg : "+msg);
		
		//4、数据持久化
		for (Record record : records) {
			recordMapper.insert(record);
		}
		
		return "成功导入"+records.size()+"条，失败"+(rows.size()-records.size()-1)+"条："+msg;
	}
	
	/**
	 * 校验医生备案信息是否合规
	 * @param record
	 * @throws Exception 
	 */
	private Record checkRecord(Record record) throws Exception {
		
		if(!MailTelValidateUtil.isMobileNO(record.getDoctorPhone())) {
			throw new Exception("手机号格式有误");
		}
		
		WebScUser user = userMapper.selectOneByLoginName(record.getDoctorPhone());
		//校验医生是否注册
		if(StringUtil.isNull(user)) {
			throw new Exception("手机号“"+record.getDoctorPhone()+"”未注册");
		}else {
			if(!user.getUserName().equals(record.getDoctorName())) {
				throw new Exception("医生名字和注册时的名字不相同，（注册姓名：“"+user.getUserName()+"”）");
			}
		}
		
		//校验医疗机构是否存在
		WebScOrganization organization = organizationMapper.selectWebScOrganization(record.getOrgName());
		if(StringUtil.isNull(organization)) {
			throw new Exception("“"+record.getOrgName()+"”医疗机构未注册");
		}
		
		//备案日期不能小于截止日期
		if (record.getRecordDate().compareTo(record.getEndDate()) == 1) {
			throw new Exception("备案日期（"+record.getRecordDate()+"）不能大于截止日期（"+record.getEndDate()+"）");
		}
		
		//查重
		
		record.setUserId(user.getUserId());
		record.setOrgId(organization.getOrgId());
		record.setCreateDate(new Date());
		return record;
	}
	
	/**
	 * 删除指定的医生备案信息
	 * @param record
	 * @return
	 */
	public String deleteRecordService(Record record) {
		
		recordMapper.deleteRecord(record.getRecordId());
		
		return "医生备案信息删除成功";
	}

}
