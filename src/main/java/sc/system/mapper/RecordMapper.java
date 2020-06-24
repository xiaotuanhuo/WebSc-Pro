package sc.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import sc.system.model.Record;

@Mapper
public interface RecordMapper {
	
	@Delete("DELETE FROM WSC_RECORD WHERE record_id=${recordId}")
	int deleteRecord(@Param("recordId") int recordId);
	
	/**
	 * 根据可变条件查询医生备案记录
	 * @param paraMap 包括：医生姓名和医疗机构名称
	 * @return
	 */
	List<Record> selectRecordByConditions(Map<String, Object> paraMap);
	
	@Select("SELECT * FROM WSC_RECORD")
	List<Record> selectRecords();
	
    int insert(Record record);
    
    int insertSelective(Record record);
    
    int updateByPrimaryKey(Record record);
    
    int updateByPrimaryKeySelective(Record record);
}