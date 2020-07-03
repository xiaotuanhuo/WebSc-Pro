package sc.system.mapper;

import sc.system.model.WebScDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 医疗集团dao
@Mapper
public interface DeptMapper {

    int deleteByPrimaryKey(Integer deptId);

    int insert(WebScDept dept);

    WebScDept selectByPrimaryKey(Integer deptId);

    int updateByPrimaryKey(WebScDept dept);

    List<WebScDept> selectByParentId(@Param("parentId") Integer parentId);

    List<WebScDept> selectAllTree();

    List<Integer> selectChildrenIDByPrimaryKey(@Param("deptId") Integer deptId);

    int selectMaxOrderNum();

    int swapSort(@Param("currentId") Integer currentId, @Param("swapId") Integer swapId);

}