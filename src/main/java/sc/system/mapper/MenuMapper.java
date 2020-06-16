package sc.system.mapper;

import sc.system.model.WebScMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper {
    int deleteByPrimaryKey(Integer menuId);

    int insert(WebScMenu menu);

    WebScMenu selectByPrimaryKey(Integer menuId);

    int updateByPrimaryKey(WebScMenu menu);

    /**
     * 获取所有菜单
     */
    List<WebScMenu> selectAll();

    List<WebScMenu> selectAllTree();

    List<WebScMenu> selectAllMenuAndCountOperator();

    List<WebScMenu> selectByParentId(Integer parentId);

    /**
     * 删除当前菜单的所有子菜单
     */
    int deleteByParentId(Integer parentId);

    /**
     * 查找某菜单的所有子类 ID
     */
    List<Integer> selectChildrenIDByPrimaryKey(@Param("menuId") Integer menuId);

    /**
     * 获取某个用户的所拥有的导航菜单
     */
    List<WebScMenu> selectMenuByLoginName(@Param("loginname") String loginname);

    int count();

    /**
     * 交换两个菜单的顺序
     */
    int swapSort(@Param("currentId") Integer currentId, @Param("swapId") Integer swapId);

    int selectMaxOrderNum();

}