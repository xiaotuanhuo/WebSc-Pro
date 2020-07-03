package sc.system.mapper;

import sc.system.model.WebScDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 医疗集团dao
@Mapper
public interface DeptMapper {

    int deleteByPrimaryKey(String deptId);

    int insert(WebScDept dept);

    WebScDept selectByPrimaryKey(String deptId);

    int updateByPrimaryKey(WebScDept dept);

    List<WebScDept> selectByParentId(@Param("parentId") String parentId);

    List<WebScDept> selectAllTree(@Param("parent_id") String parentId);

    List<String> selectChildrenIDByPrimaryKey(@Param("deptId") String deptId);

    int selectMaxOrderNum();

    int swapSort(@Param("currentId") Integer currentId, @Param("swapId") Integer swapId);

}