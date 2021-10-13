package sc.common.quartz;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import sc.common.util.CalendarUtil;
import sc.common.util.DateUtils;
import sc.system.mapper.DocMapper;

/**
 * 订单状态更改计划任务
 * 将订单状态从待完成改为已完成状态
 * @author pp
 *
 */
@Component
public class DocumentStateUpdScheduleTask {
	private static final Logger logger = LoggerFactory.getLogger(DocumentStateUpdScheduleTask.class);

	@Autowired
	DocMapper docMapper;
	
	@Scheduled(cron = "${document-state-upd-cron}")
	public void statisticsEvaluate() {
		logger.info("----------订单状态从待完成到已完成更改计划任务已启动-----------------------");
		try {
			//获取时间
			
			String date = DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", CalendarUtil.dayAdds(new Date(), -1));
			
			List<String> documentIds = docMapper.selectByDocumentState("4", date);
			for (String documentId : documentIds) {
				int c = docMapper.updDocumentState("5", documentId);
				if (c != 1) {
					logger.error("订单状态从待完成到已完成修改失败 ，订单号："+documentId);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
