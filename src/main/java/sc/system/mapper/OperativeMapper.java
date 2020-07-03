package sc.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import sc.system.model.WebScOperative;

@Mapper
public interface OperativeMapper {
	List<WebScOperative> getWebScOperativeList(String operativeName);
}
