package sc.system.mapper;

import sc.system.model.WebScOperator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 操作按钮dao
 * @author aisino
 *
 */
@Mapper
public interface OperatorMapper {
    int deleteByPrimaryKey(Integer operatorId);

    int insert(WebScOperator operator);

    WebScOperator selectByPrimaryKey(Integer operatorId);

    int updateByPrimaryKey(WebScOperator operator);

    List<WebScOperator> selectByMenuId(@Param("menuId") Integer menuId);

    List<WebScOperator> selectAll();

    int deleteByMenuId(Integer menuId);
}