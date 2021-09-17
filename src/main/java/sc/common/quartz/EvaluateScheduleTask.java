package sc.common.quartz;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import sc.common.util.NumberUtil;
import sc.system.mapper.DocMapper;
import sc.system.mapper.OrganizationMapper;
import sc.system.mapper.UserMapper;

@Component
public class EvaluateScheduleTask {
	private static final Logger logger = LoggerFactory.getLogger(EvaluateScheduleTask.class);
	
	@Autowired
	DocMapper docMapper;
	@Autowired
	UserMapper userMapper;
	@Autowired
	OrganizationMapper organizationMapper;

	@Scheduled(cron = "${statistics-evaluate-cron}")
	public void statisticsEvaluate() {
		logger.info("----------统计医生和机构的平均评分-----------------------");
		try {
			//1、统计医生的总体评分
			List<Map<String, Object>> doctorEvaluateList = docMapper.selectDoctorAvgEvaluate();
			for (Map<String, Object> doctorEvaluateMap : doctorEvaluateList) {
				userMapper.updDoctorAvgEvaluate((String)doctorEvaluateMap.get("doctorId"), Double.parseDouble(NumberUtil.format((double)doctorEvaluateMap.get("doctorEvaluate")*2, "0.0")));
			}
			
		} catch (Exception e) {
			logger.error("统计医生总体评分失败，"+e.getMessage());
			e.printStackTrace();
		}
		
		try {
			//2、统计机构的总体评分
			List<Map<String, Object>> orgEvaluateList = docMapper.selectOrgAvgEvaluate();
			for (Map<String, Object> orgEvaluateMap : orgEvaluateList) {
				organizationMapper.updOrgAvgEvaluate((String)orgEvaluateMap.get("orgId"), Double.parseDouble(NumberUtil.format((double)orgEvaluateMap.get("orgEvaluate")*2, "0.0")));
			}
		} catch (Exception e) {
			logger.error("统计机构总体评分失败，"+e.getMessage());
			e.printStackTrace();
		}
	}
}
