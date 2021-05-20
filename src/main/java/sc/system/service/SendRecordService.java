package sc.system.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import sc.common.util.UUID19;
import sc.system.mapper.SendRecordMapper;
import sc.system.mapper.UserMapper;
import sc.system.model.WebScDoc;
import sc.system.model.WebScSendRecord;
import sc.system.model.WebScUser;

@Service
public class SendRecordService {
	@Resource
	DocService docService;
	@Resource
	private UserMapper userMapper;
	@Resource
	private SendRecordMapper sendRecordMapper;
	
	/**
	 * 插入操作记录
	 * @param documentId 单据id
	 * @param user	用户信息
	 * @param type  操作码
	 */
	public void insertSendRecord(String documentId, WebScUser user, int sendtype, String sendUserId) throws Exception{
		//订单信息
		WebScDoc doc = docService.selectByPrimaryKey(documentId);
		
		WebScSendRecord record = new WebScSendRecord();
		record.setDocumentId(documentId);
		record.setUserId(user.getUserId());
		
//		if(user.getRoleId().equals("4")){
			//管理员
			if(sendtype == 3001){
				//分配订单
				record.setMemo("<div class='gray'>" + doc.getOperateStartTime() + "</div><div class='normal'>有新的订单,请及时处理！来源： 管理员分配</div>");
				//通知订单医生
				record.setRecordId(UUID19.uuid());
				record.setSendUserId(sendUserId);
				sendRecordMapper.insert(record);
			}else if(sendtype == 3002){
				//取消分配订单
				record.setMemo("<div class='gray'>" + doc.getOperateStartTime() + "</div><div class='normal'>管理员取消分配给您的订单！</div>");
				//通知订单医生
				List<WebScUser> userls = userMapper.getDocumentQaUser(documentId);
				for(WebScUser tmpUser : userls){
					record.setRecordId(UUID19.uuid());
					record.setSendUserId(tmpUser.getUserId());
					sendRecordMapper.insert(record);
				}
			}else if(sendtype == 9001){
				//撤销订单
				record.setMemo("<div class='gray'>" + doc.getOperateStartTime() + "</div><div class='normal'>管理员撤销订单！订单机构：" + doc.getOrgName() + ",患者：" + doc.getPatientName() + ",手术时间：" + doc.getOperateStartTime() + "</div>");
				//通知订单医生
				List<WebScUser> userls = userMapper.getDocumentQaUser(documentId);
				for(WebScUser tmpUser : userls){
					record.setRecordId(UUID19.uuid());
					record.setSendUserId(tmpUser.getUserId());
					sendRecordMapper.insert(record);
				}
			}
//		}else if(user.getRoleId().equals("5")){
			//医生
			else if(sendtype == 4001){
				//接收订单
				record.setMemo("<div class='gray'>" + doc.getOperateStartTime() + "</div><div class='normal'>医生-" + user.getUserName() + " 已接单！订单机构：" + doc.getOrgName() + ",患者：" + doc.getPatientName() + ",手术时间：" + doc.getOperateStartTime() + "</div>");
				//获取订单对应区域管理员
				//原管理员
				record.setRecordId(UUID19.uuid());
				record.setSendUserId(doc.getAdminUserId());
				sendRecordMapper.insert(record);
			}else if(sendtype == 4002){
				//接收订单
				record.setMemo("<div class='gray'>" + doc.getOperateStartTime() + "</div><div class='normal'>医生-" + user.getUserName() + " 撤销接单！订单机构：" + doc.getOrgName() + ",患者：" + doc.getPatientName() + ",手术时间：" + doc.getOperateStartTime() + "</div>");
				//获取订单对应区域管理员
				List<WebScUser> userls = userMapper.getDocumentLocalAdminUser(documentId);
				for(WebScUser tmpUser : userls){
					record.setRecordId(UUID19.uuid());
					record.setSendUserId(tmpUser.getUserId());
					sendRecordMapper.insert(record);
				}
			}else if(sendtype == 4003){
				//拒绝订单
				record.setMemo("<div class='gray'>" + doc.getOperateStartTime() + "</div><div class='normal'>医生-" + user.getUserName() + " 拒绝接收转单！订单机构：" + doc.getOrgName() + ",患者：" + doc.getPatientName() + ",手术时间：" + doc.getOperateStartTime() + "</div>");
				//通知订单原医生
				if(doc.getTransferUserId() != null){
					record.setRecordId(UUID19.uuid());
					record.setSendUserId(doc.getTransferUserId());
					sendRecordMapper.insert(record);
				}
			}else if(sendtype == 4004){
				//发送转单
				record.setMemo("<div class='gray'>" + doc.getOperateStartTime() + "</div><div class='normal'>有新的订单,请及时处理！来源： 医生-" + user.getUserName() + "的转单</div>");
				//通知转单医生
				record.setRecordId(UUID19.uuid());
				record.setSendUserId(sendUserId);
				sendRecordMapper.insert(record);
			}else if(sendtype == 4005){
				//手术完成
				record.setMemo("<div class='gray'>" + doc.getOperateStartTime() + "</div><div class='normal'>医生-" + user.getUserName() + " 已完成手术！订单机构：" + doc.getOrgName() + ",患者：" + doc.getPatientName() + ",手术时间：" + doc.getOperateStartTime() + "</div>");
				//获取订单对应区域管理员
				List<WebScUser> userls = userMapper.getDocumentLocalAdminUser(documentId);
				for(WebScUser tmpUser : userls){
					record.setRecordId(UUID19.uuid());
					record.setSendUserId(tmpUser.getUserId());
					sendRecordMapper.insert(record);
				}
			}
//		}else if(user.getRoleId().equals("8") || user.getRoleId().equals("9")){
			//订单录入人员
			else if(sendtype == 1001){
				//发布订单
				record.setMemo("<div class='gray'>" + doc.getOperateStartTime() + "</div><div class='normal'>录入员" + user.getUserName() + "发布新订单" + "</div>");
				//通知区域所有管理员
				//获取订单对应区域管理员
				List<WebScUser> userls = userMapper.getDocumentLocalAdminUser(documentId);
				for(WebScUser tmpUser : userls){
					record.setRecordId(UUID19.uuid());
					record.setSendUserId(tmpUser.getUserId());
					sendRecordMapper.insert(record);
				}
			}
//		}
	}
	
	public void updateSendRecord(WebScSendRecord record){
		sendRecordMapper.update(record);
	}
	
	public List<WebScSendRecord> getWebScSendRecord(){
		return sendRecordMapper.getWebScSendRecord();
	}
	
}
