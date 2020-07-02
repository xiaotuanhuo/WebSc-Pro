package sc.system.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import sc.system.model.Record;

@Mapper
public interface RecordMapper {
	
	@Select("SELECT '1' FROM WSC_RECORD WHERE doctor_phone=#{doctorPhone} "
			+ "AND org_name=#{orgName} AND end_date=str_to_date(#{endDate},'%Y-%m-%d')")
	String isExists(
			@Param("doctorPhone") String doctorPhone, 
			@Param("orgName") String orgName, 
			@Param("endDate") String endDate);
	
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