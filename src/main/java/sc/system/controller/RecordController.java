package sc.system.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import sc.common.util.PageResultBean;
import sc.common.util.ResultBean;
import sc.system.model.Record;
import sc.system.service.RecordService;

@Controller
@RequestMapping(value = "record")
public class RecordController {
	private static final Logger logger = LoggerFactory.getLogger(RecordController.class);
	
	@Resource RecordService recordService;
	
	@GetMapping(value = "toRecordManage")
	public String toRecordManage() {
		return "record/record-manage";
	}
	
	@PostMapping(value = "getDoctorRecords")
	@ResponseBody
	public PageResultBean<Record> getDoctorRecords(
			@RequestBody Map<String, Object> paraMap) {
		PageResultBean<Record> prb = new PageResultBean<Record>();
		try {
			
			prb = recordService.getDoctorRecordsService(paraMap);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取医生备案情况失败，"+e.getMessage());
		}
		
		return prb;
	}
	
	@PostMapping(value = "editRecord")
	@ResponseBody
	public ResultBean editRecord(@RequestBody Record record) {
		ResultBean rBean = null;
		try {
			
			rBean = ResultBean.success(recordService.editRecordService(record));
			
		} catch (Exception e) {
			logger.error("修改医生备案信息失败，"+e.getMessage());
			rBean = ResultBean.error("修改医生备案信息失败，"+e.getMessage());
		}
		
		return rBean;
	}
	
	@PostMapping(value = "addRecord")
	@ResponseBody
	public ResultBean addRecord(@RequestBody Record record) {
		ResultBean rBean = null;
		try {
			
			rBean = ResultBean.success(recordService.addRecordService(record));
			
		} catch (Exception e) {
			logger.error("增加医生备案信息失败，"+e.getMessage());
			rBean = ResultBean.error("增加医生备案信息失败，"+e.getMessage());
		}
		
		return rBean;
	}
	
	@PostMapping(value = "importRecords")
	@ResponseBody
	public ResultBean importRecords(
			HttpServletRequest resquest,
			@RequestParam("file") MultipartFile file) {
		ResultBean rBean = null;
		try {
			
			rBean = ResultBean.success(recordService.importRecordsService(file));
			
		} catch (Exception e) {
			logger.error("增加医生备案信息失败，"+e.getMessage());
			rBean = ResultBean.error("增加医生备案信息失败，"+e.getMessage());
		}
		
		return rBean;
	}
	
	@PostMapping(value = "deleteRecord")
	@ResponseBody
	public ResultBean deleteRecord(@RequestBody Record record) {
		ResultBean rBean = null;
		try {
			
			rBean = ResultBean.success(recordService.deleteRecordService(record));
			
		} catch (Exception e) {
			logger.error("删除医生备案信息失败，"+e.getMessage());
			rBean = ResultBean.error("删除医生备案信息失败，"+e.getMessage());
		}
		
		return rBean;
	}
	
	@GetMapping(value = "getDoctors")
	@ResponseBody
	public ResultBean getDoctors() {
		ResultBean rBean = null;
		try {
			
			rBean = ResultBean.success(recordService.getDoctorsService());
			
		} catch (Exception e) {
			logger.error("获取医生列表失败，"+e.getMessage());
			rBean = ResultBean.error("获取医生列表失败，"+e.getMessage());
		}
		
		return rBean;
	}
	
	@GetMapping(value = "getOrgs")
	@ResponseBody
	public ResultBean getOrgs() {
		ResultBean rBean = null;
		try {
			
			rBean = ResultBean.success(recordService.getOrgsService());
			
		} catch (Exception e) {
			logger.error("获取医疗机构列表失败，"+e.getMessage());
			rBean = ResultBean.error("获取医疗机构列表失败，"+e.getMessage());
		}
		
		return rBean;
	}
}
