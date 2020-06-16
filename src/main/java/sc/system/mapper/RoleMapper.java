package sc.system.mapper;

import sc.system.model.WebScRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {
    int deleteByPrimaryKey(Integer roleId);

    int insert(WebScRole role);

    WebScRole selectByPrimaryKey(Integer roleId);

    int updateByPrimaryKey(WebScRole role);

    List<WebScRole> selectAll();

    List<WebScRole> selectAllByQuery(WebScRole roleQuery);

    int count();
}