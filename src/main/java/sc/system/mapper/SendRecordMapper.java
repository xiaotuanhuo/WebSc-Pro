package sc.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import sc.system.model.WebScSendRecord;

@Mapper
public interface SendRecordMapper {
	void insert(WebScSendRecord record);
	
	void update(WebScSendRecord record);
	
	List<WebScSendRecord> getWebScSendRecord();
}
