package sc.system.service;

import sc.common.util.TreeUtil;
import sc.system.mapper.MenuMapper;
import sc.system.mapper.OperatorMapper;
import sc.system.mapper.RoleOperatorMapper;
import sc.system.model.WebScMenu;
import sc.system.model.WebScOperator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class OperatorService {

    @Resource
    private OperatorMapper operatorMapper;

    @Resource
    private MenuMapper menuMapper;

    @Resource
    private RoleOperatorMapper roleOperatorMapper;

    public void deleteByPrimaryKey(Integer operatorId) {
//        // 删除分配给用户的操作权限
//        roleOperatorMapper.deleteByOperatorId(operatorId);
        // 删除自身
        operatorMapper.deleteByPrimaryKey(operatorId);
    }

    public int add(WebScOperator operator){
        return operatorMapper.insert(operator);
    }

    public WebScOperator selectByPrimaryKey(Integer operatorId){
        return operatorMapper.selectByPrimaryKey(operatorId);
    }

    public int updateByPrimaryKey(WebScOperator operator){
        return operatorMapper.updateByPrimaryKey(operator);
    }

    public List<WebScOperator> selectByMenuId(Integer menuId) {
        return operatorMapper.selectByMenuId(menuId);
    }

    public List<WebScOperator> selectAll() {
        return operatorMapper.selectAll();
    }

    public List<WebScMenu> getALLMenuAndOperatorTree() {

        // 获取用户拥有的所在操作权限
        List<WebScOperator> operators = operatorMapper.selectAll();

        List<WebScMenu> menuList = menuMapper.selectAll();

        // 获取功能权限树时, 菜单应该没有复选框, 不可选.
        for (WebScMenu menu : menuList) {
            menu.setCheckArr(null);
        }

        List<WebScMenu> menuTree = TreeUtil.toTree(menuList,
                "menuId", "parentId", "children", WebScMenu.class);



        List<WebScMenu> menuLeafNode = TreeUtil.getAllLeafNode(menuTree);

        // 将操作权限拼接到页面的树形结构下.
        for (WebScMenu menu : menuLeafNode) {

            List<WebScMenu> children = menu.getChildren();
            if (children == null) {
                children = new ArrayList<>();
            }

            for (WebScOperator operator : operators) {
                if (menu.getMenuId().equals(operator.getMenuId())) {

                    // 将操作权限转化为 Menu 结构. 由于操作权限可能与菜单权限的 ID 值冲突, 故将操作权限的 ID + 10000. 使用操作权限的 ID 时再减去这个数
                    WebScMenu temp = new WebScMenu();
                    temp.setMenuId(operator.getOperatorId() + 10000);
                    temp.setParentId(operator.getMenuId());
                    temp.setMenuName(operator.getOperatorName());
                    children.add(temp);
                }
            }
            menu.setChildren(children);
        }

        return menuTree;
    }
}
