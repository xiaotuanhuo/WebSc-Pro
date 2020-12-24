package sc.system.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import sc.common.annotation.OperationLog;
import sc.common.constants.DocStateEnum;
import sc.common.util.CalendarUtil;
import sc.common.util.DateUtils;
import sc.common.util.HttpsUtil;
import sc.common.util.LuceneUtil;
import sc.common.util.PageResultBean;
import sc.common.util.ResultBean;
import sc.common.util.StringUtil;
import sc.common.util.UUID19;
import sc.common.util.UnixtimeUtil;
import sc.system.mapper.AnestheticMapper;
import sc.system.mapper.DocMapper;
import sc.system.mapper.OperativeMapper;
import sc.system.mapper.UserMapper;
import sc.system.model.Record;
import sc.system.model.WebScDoc;
import sc.system.model.WebScUser;

@Controller
@RequestMapping(value = "xcx")
public class XcxTestController {
	private static final Logger logger = LoggerFactory.getLogger(XcxTestController.class);
	
	public static final String appid = "wx5139fc8c8b8878f7";
	public static final String secret = "f46eab7526212299671b17e854a2a5d7";
	
	@Autowired
	AnestheticMapper anestheticMapper;
	@Autowired
	OperativeMapper operativeMapper;
	@Autowired
	DocMapper docMapper;
	@Autowired
	UserMapper userMapper;
	
	@OperationLog("获取麻醉方法列表和手术名列表")
    @GetMapping(value = "/getAnestheticsAndOperatives")
    @ResponseBody
    public ResultBean getAnestheticsAndOperatives() {
		ResultBean resultBean = null;
		try {
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("anesthetics", anestheticMapper.getWebScAnesthetics());
//			resMap.put("operatives", operativeMapper.getWebScOperatives());
			resultBean = ResultBean.success(resMap);
		} catch (Exception e) {
			resultBean = ResultBean.error("获取麻醉方法列表和手术名列表失败");
		}
		
        return resultBean;
    }
	
	@OperationLog("保存订单")
    @PostMapping(value = "/saveOrder")
    @ResponseBody
	public ResultBean saveOrder(@RequestBody() WebScDoc doc) {
		ResultBean resultBean = null;
		try {
			logger.info(doc.toString());
			doc.setDocumentId(UUID19.uuid());
			doc.setDocumentState("0");
			
			if(0 == userMapper.isExistByDoctorName(doc.getOperateUser())) {
				throw new Exception("姓名为："+doc.getOperateUser()+"的医生不存在");
			}
			
			docMapper.insert(doc);
			
			resultBean = ResultBean.success("订单发布成功");
			
		} catch (Exception e) {
			resultBean = ResultBean.error("订单发布失败，"+e.getMessage());
		}
		
        return resultBean;
    }
	
	@OperationLog("获取订单明细")
    @GetMapping(value = "/getDocDetail")
    @ResponseBody
	public ResultBean getDocDetail(@RequestParam("documentId") String documentId) {
		ResultBean resultBean = null;
		try {
			logger.info(documentId);
			WebScDoc webScDoc = docMapper.selectByPrimaryKey(documentId);
			
			//手术名称
			String operativeIds[] = webScDoc.getOperativeId().split(",");
			String operativeName = "";
			for (int i = 0; i < operativeIds.length; i++) {
				if(i == 0) {
					operativeName = operativeMapper.selectOperativeById(operativeIds[i]).getOperativeName();
				}else {
					operativeName = operativeName +"，"+operativeMapper.selectOperativeById(operativeIds[i]).getOperativeName();
				}
			}
			webScDoc.setOperativeName(operativeName);
			
			//麻醉方法
			webScDoc.setAnestheticName(anestheticMapper.selectAnestheticById(webScDoc.getAnestheticId()).getAnestheticName());
			
			resultBean = ResultBean.success(webScDoc);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			resultBean = ResultBean.error("获取订单明细失败");
		}
		
        return resultBean;
    }
	
	@OperationLog("查询订单列表")
    @PostMapping(value = "/searchOrderList")
    @ResponseBody
	public PageResultBean<WebScDoc> searchOrderList(@RequestBody() Map<String, Object> paraMap) {
		PageResultBean<WebScDoc> prb = null;
		try {
			logger.info(paraMap.toString());
			prb = new PageResultBean<WebScDoc>();
			PageHelper.startPage(paraMap.get("page")==null?1:(int)paraMap.get("page"), 
					paraMap.get("limit")==null?10:(int)paraMap.get("limit"));
			
			List<WebScDoc> docs = docMapper.selectXcxDocsByConditions(paraMap);
			for (WebScDoc doc : docs) {
				//手术名称
				String operativeIds[] = doc.getOperativeId().split(",");
				String operativeName = "";
				for (int i = 0; i < operativeIds.length; i++) {
					if(i == 0) {
						operativeName = operativeMapper.selectOperativeById(operativeIds[i]).getOperativeName();
					}else {
						operativeName = operativeName +"，"+operativeMapper.selectOperativeById(operativeIds[i]).getOperativeName();
					}
				}
				doc.setOperativeName(operativeName);
				
				doc.setDocumentState(DocStateEnum.getvalueOf(doc.getDocumentState()).getTxt());
			}
			
			PageInfo<WebScDoc> pageInfo = new PageInfo<>(docs);
			
			prb.setCount(Long.valueOf(pageInfo.getTotal()).intValue());
			prb.setData(docs);
			prb.setCode(0);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			prb = new PageResultBean<WebScDoc>();
			prb.setCode(-1);
		}
		
        return prb;
    }
	
	@OperationLog("评价")
    @PostMapping(value = "/orderEvaluate")
    @ResponseBody
	public ResultBean orderEvaluate(@RequestBody() Map<String, Object> paraMap) {
		ResultBean resultBean = null;
		try {
			docMapper.updDocById(
					paraMap.get("documentId").toString(), 
					paraMap.get("evaluateText").toString(), 
					(int) (Float.parseFloat(paraMap.get("evaluateStar").toString())*2));
			resultBean = ResultBean.success("评价成功，谢谢！");
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			resultBean = ResultBean.error("评价失败，请重试...");
		}
		
        return resultBean;
    }
	
	@OperationLog("登录")
    @PostMapping(value = "/login")
    @ResponseBody
	public ResultBean login(@RequestBody() Map<String, Object> paraMap) {
		ResultBean resultBean = null;
		try {
			logger.info(paraMap.toString());
			//0、check用户名和密码
			WebScUser webScUser = userMapper.selectOneByLoginName(paraMap.get("name").toString());
			if(StringUtil.isNull(webScUser)) {
				logger.info("用户名有误");
				resultBean = ResultBean.error("用户名或密码有误");
			}else {
				String pws = new Md5Hash(paraMap.get("pws").toString(), webScUser.getSalt()).toString();
				if(pws.equals(webScUser.getLoginPwd())) {
					//1、获取openId和sessionKey
					URI uri = new URI(
							"https://api.weixin.qq.com/sns/jscode2session?"
							+ "appid="+appid
									+ "&secret="+secret
											+ "&js_code="+paraMap.get("code").toString()
													+ "&grant_type="+paraMap.get("code").toString());
					String responseMsg = HttpsUtil.getHttps(uri);
					JSONObject responseMsgJson = JSONObject.parseObject(responseMsg);
					
					//2、保存openId到数据库
					userMapper.updOpenid(responseMsgJson.getString("openid"), webScUser.getUserId());
					
					//3、将用户信息、openId和sessionKey传到前台
					Map<String, Object> resMap = new HashMap<String, Object>();
					resMap.put("openid", responseMsgJson.getString("openid"));
					resMap.put("session_key", responseMsgJson.getString("session_key"));
					resMap.put("userInfo", webScUser);
					
					resultBean = ResultBean.success(resMap);
				}else {
					logger.info("密码有误");
					resultBean = ResultBean.error("用户名或密码有误");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			resultBean = ResultBean.error("登录失败，请重试...");
		}
		
        return resultBean;
    }
	
	@OperationLog("登出")
    @PostMapping(value = "/signOut")
    @ResponseBody
	public ResultBean signOut(@RequestBody() Map<String, Object> paraMap){
		ResultBean resultBean = null;
		try {
			
			userMapper.updOpenid("", Integer.parseInt(paraMap.get("userId").toString()));
			
			resultBean = ResultBean.success("登出成功");
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			resultBean = ResultBean.error("登出失败，请重试...");
		}
		
        return resultBean;
	}
	
	@OperationLog("自动登录")
    @PostMapping(value = "/autoLogin")
    @ResponseBody
    public ResultBean autoLogin(@RequestBody() Map<String, Object> paraMap){
		ResultBean resultBean = null;
		try {
			//1、获取openId和sessionKey
			URI uri = new URI(
					"https://api.weixin.qq.com/sns/jscode2session?"
					+ "appid="+appid
							+ "&secret="+secret
									+ "&js_code="+paraMap.get("code").toString()
											+ "&grant_type="+paraMap.get("code").toString());
			String responseMsg = HttpsUtil.getHttps(uri);
			JSONObject responseMsgJson = JSONObject.parseObject(responseMsg);
			
			WebScUser webScUser = userMapper.selectUserByOpenid(responseMsgJson.getString("openid"));
			if(StringUtil.isNull(webScUser)) {
				resultBean = new ResultBean("未绑定微信openid", null, 2);
			}else {
				//3、将用户信息、openId和sessionKey传到前台
				Map<String, Object> resMap = new HashMap<String, Object>();
				resMap.put("openid", responseMsgJson.getString("openid"));
				resMap.put("session_key", responseMsgJson.getString("session_key"));
				resMap.put("userInfo", webScUser);
				resultBean = ResultBean.success(resMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			resultBean = ResultBean.error("登出失败，请重试...");
		}
		
        return resultBean;
	}
	
	@OperationLog("搜索手术名称列表")
    @PostMapping(value = "/searchOperativeNames")
    @ResponseBody
	public ResultBean searchOperativeNames(@RequestBody() Map<String, Object> paraMap) {
		ResultBean resultBean = null;
		try {
			resultBean = ResultBean.success(
					LuceneUtil.searchOperativeNames(
							"D:\\lucene\\lucene-db\\sc\\operativeName", paraMap.get("key").toString(), 5));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			resultBean = ResultBean.error("搜索手术名称列表失败，请重试...");
		}
		return resultBean;
	}
	
	@OperationLog("获取手术量信息")
    @GetMapping(value = "/getOperativeCount")
    @ResponseBody
	public ResultBean getOperativeCount(@RequestParam("orgId") String orgId) {
		ResultBean resultBean = null;
		try {
			Map<String, Object> sourceMap = new HashMap<String, Object>();
			//1、获取前七天的手术量
			List<Map<String, Object>> operativeCountList = new ArrayList<Map<String,Object>>();
			long nowDate = UnixtimeUtil.getUnixDay(new Date().getTime());
			for (int i = (int) (nowDate-7); i < nowDate; i++) {
				Map<String, Object> operativeCountMap = new HashMap<String, Object>();
				
				String startDateStr = UnixtimeUtil.getStringDay(i);
				String endDateStr = UnixtimeUtil.getStringDay(i+1);
				Date date = DateUtils.parseDate(startDateStr);
				
				int operativeCount = docMapper.selectOperativeCount(orgId, startDateStr, endDateStr);
				
				operativeCountMap.put("date", CalendarUtil.getMonth(date)+"月"+CalendarUtil.getDay(date)+"号");
				operativeCountMap.put("count", operativeCount);
				
				operativeCountList.add(operativeCountMap);
			}
			
			sourceMap.put("source", operativeCountList);
			//2、获取当天的手术量
			int curDayCount = docMapper.selectOperativeCount(
					orgId,
					UnixtimeUtil.getStringDay(nowDate), 
					UnixtimeUtil.getStringDay(nowDate+1));
			sourceMap.put("curDayCount", curDayCount);
			//3、获取前一周总手术量
			int curWeekCount = docMapper.selectOperativeCount(
					orgId,
					UnixtimeUtil.getStringDay(nowDate-7), 
					UnixtimeUtil.getStringDay(nowDate));
			sourceMap.put("curWeekCount", curWeekCount);
			//4、当月总手术量
			Date date = new Date();
			int curMonthCount = docMapper.selectOperativeCount(
					orgId,
					CalendarUtil.getYear(date)+"-"+CalendarUtil.getMonth(date)+"-01", 
					CalendarUtil.getYear(date)+"-"+CalendarUtil.getMonth(date)+"-30");
			sourceMap.put("curMonthCount", curMonthCount);
			//5、当月平均手术时间
			List<WebScDoc> webScDocs = docMapper.selectWebScDocs(orgId, "5", 30);
			long sumTime = 0l;
			for (WebScDoc webScDoc : webScDocs) {
				if(StringUtil.isNotEmpty(webScDoc.getOperateStartTime())&&StringUtil.isNotEmpty(webScDoc.getOperateEndTime())) {
					long operativeStartTime = UnixtimeUtil.getUnixMinute(
							DateUtils.parseDate(webScDoc.getOperateStartTime()).getTime());
					long operativeEndTime = UnixtimeUtil.getUnixMinute(
							DateUtils.parseDate(webScDoc.getOperateEndTime()).getTime());
					sumTime = operativeEndTime-operativeStartTime;
				}
			}
			long aveTime = sumTime/webScDocs.size();
			sourceMap.put("averageDuration", aveTime);
			
			resultBean = ResultBean.success(sourceMap);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			resultBean = ResultBean.error("获取手术量信息");
		}
		return resultBean;
	}

}
