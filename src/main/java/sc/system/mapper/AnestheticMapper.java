package sc.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import sc.system.model.WebScAnesthetic;

@Mapper
public interface AnestheticMapper {
	public List<WebScAnesthetic> getWebScAnestheticList(String anestheticName);
}
