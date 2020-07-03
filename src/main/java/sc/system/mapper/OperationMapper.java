package sc.system.mapper;

import java.util.List;

import sc.system.model.WebScOperation;

/**
 * 手术类型dao
 * @author aisino
 *
 */
public interface OperationMapper {
    int deleteByPrimaryKey(String operationtypeId);

    int insert(WebScOperation record);

    int insertSelective(WebScOperation record);

    WebScOperation selectByPrimaryKey(String operationtypeId);
    
    /**
     * 获取所有手术类型
     * @return
     */
    List<WebScOperation> selectAll();

    int updateByPrimaryKeySelective(WebScOperation record);

    int updateByPrimaryKey(WebScOperation record);
}