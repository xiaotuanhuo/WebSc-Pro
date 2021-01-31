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
    
    /**
     * 查询出医疗集团根节点
     * 医疗集团根节点的父节点默认是0 且只有一个医疗集团根节点
     * @return
     */
	WebScDept selectSuperDept();

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

    /**
     * 统计当前区划下的医疗集团数量
     * @param province
     * @param city
     * @return
     */
	int countByDist(@Param("province_code") String province, @Param("city_code") String city);
//	int countByDist(@Param("province_code") String province);
    
    /**
     * 统计该医疗集团名称数量
     * @param deptId	不为null是表示修改，统计数量时不包含该项
     * @param deptName
     * @return
     */
    int countByName(@Param("dept_id") String deptId, @Param("dept_name") String deptName);
}