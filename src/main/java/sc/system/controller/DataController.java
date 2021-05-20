package sc.system.controller;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import sc.common.util.RedisUtil;
import sc.system.model.WebScAnesthetic;
import sc.system.model.WebScOperative;
import sc.system.model.WebScOrganization;
import sc.system.service.AnestheticService;
import sc.system.service.OperativeService;
import sc.system.service.OrganizationService;

@Controller
@RequestMapping("/data")
public class DataController {
	private static final Logger log = LoggerFactory.getLogger(DataController.class);
	
	@Resource
	private OperativeService operativeService;
	
	@Resource
	private AnestheticService anestheticService;
	
	@Resource
	private OrganizationService organizationService;
	
	@Resource
	private RedisUtil rs;
	
	/**
	 * 获取手术名称清单
	 */
	public List<WebScOperative> getWebScOperativeList(){
		//从redis里获取
		log.info("Redis:获取手术名称列表");
		@SuppressWarnings("unchecked")
		List<WebScOperative> operativeNamels = (List<WebScOperative>) rs.get("WSC_OPERATIVE");
		
		if(operativeNamels == null){
			log.info("Redis获取失败:从数据读取手术名称列表");
			operativeNamels = operativeService.getWebScOperativeList(null);
			rs.set("WSC_OPERATIVE", operativeNamels, 86400);
		}
		return operativeNamels;
	}
	
	/**
	 * 获取麻醉方法清单
	 */
	public List<WebScAnesthetic> getWebScAnestheticList(){
		//从redis里获取
		log.info("Redis:获取麻醉方法列表");
		@SuppressWarnings("unchecked")
		List<WebScAnesthetic> anestheticls = (List<WebScAnesthetic>) rs.get("WSC_ANESTHETIC");
		
		if(anestheticls == null){
			log.info("Redis获取失败:从数据读取麻醉方法列表");
			anestheticls = anestheticService.getWebScAnestheticList(null);
			rs.set("WSC_ANESTHETIC", anestheticls, 86400);
		}
		return anestheticls;
	}
	
	/**
	 * 获取麻醉方法清单
	 */
	public List<WebScOrganization> getWebScOrganizationList(){
		//从redis里获取
//		log.info("Redis:获取麻醉方法列表");
//		@SuppressWarnings("unchecked")
//		List<WebScOrganization> anestheticls = (List<WebScOrganization>) rs.get("WSC_Organization");
//		
//		if(anestheticls == null){
//			log.info("Redis获取失败:从数据读取麻醉方法列表");
		List<WebScOrganization> orgls = organizationService.selectAll(1, 10000, false, false, null, new WebScOrganization());
		//getList(1, 10000, new WebScOrganization());
//			rs.set("WSC_ANESTHETIC", anestheticls, 86400);
//		}
		return orgls;
	}
}
