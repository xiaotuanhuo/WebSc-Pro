package sc.system.mapper;

import java.util.List;

import sc.system.model.WscOperativeType;

public interface OperativeTypeMapper {
	int insert(WscOperativeType record);
	
	int insertSelective(WscOperativeType record);
	
	List<WscOperativeType> selectAll();
}