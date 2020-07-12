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

	WebScDept selectByParentIdAndDistrict(@Param("parent_id") String parentId, @Param("province_code") String province,
			@Param("city_code") String city);
    
    List<WebScDept> selectByParentId(@Param("parentId") String parentId);

    /**
     * 查询当前节点及其子孙节点（树形）
     * @param deptId
     * @return
     */
    List<WebScDept> selectTree(@Param("dept_id") String deptId);
    
    /**
     * 查询当前节点的子节点（树形）
     * @param parentId
     * @return
     */
    List<WebScDept> selectSubTree(@Param("parent_id") String parentId);
    
    List<WebScDept> selectUnleafTree(@Param("dept_id") String deptId);

    List<String> selectChildrenIDByPrimaryKey(@Param("deptId") String deptId);

    int selectMaxOrderNum();

    int swapSort(@Param("currentId") Integer currentId, @Param("swapId") Integer swapId);

}