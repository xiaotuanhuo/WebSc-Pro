package sc.system.mapper;

import org.apache.ibatis.annotations.Mapper;

import sc.system.model.WebScDoc;

@Mapper
public interface DocTmpMapper {
	int insert(WebScDoc doc);
	
	int update(WebScDoc doc);
	
	int count();
}
