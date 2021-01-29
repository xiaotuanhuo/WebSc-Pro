package sc.system.service;

import sc.common.shiro.ShiroActionProperties;
import sc.common.util.ShiroUtil;
import sc.common.util.TreeUtil;
import sc.system.mapper.MenuMapper;
import sc.system.mapper.OperatorMapper;
import sc.system.mapper.RoleMenuMapper;
import sc.system.model.WebScMenu;
import sc.system.model.WebScUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class MenuService {

    @Resource
    private MenuMapper menuMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private OperatorMapper operatorMapper;

    @Resource
    private ShiroActionProperties shiroActionProperties;

    public WebScMenu selectByPrimaryKey(Integer id) {
        return menuMapper.selectByPrimaryKey(id);
    }

    /**
     * 获取所有菜单
     */
    public List<WebScMenu> selectAll() {
        return menuMapper.selectAll();
    }

    /**
     * 根据父 ID 获取所有菜单
     */
    public List<WebScMenu> selectByParentId(Integer parentId) {
        return menuMapper.selectByParentId(parentId);
    }

    /**
     * 获取所有菜单 (树形结构)
     */
    public List<WebScMenu> getALLTree() {
        return menuMapper.selectAllTree();
    }

    /**
     * 获取所有菜单并添加一个根节点 (树形结构)
     */
    public List<WebScMenu> getALLMenuTreeAndRoot() {
        List<WebScMenu> allMenuTree = getALLTree();
        return addRootNode("导航目录", 0, allMenuTree);
    }

    /**
     * 获取所有菜单并统计菜单下的操作权限数 (树形结构)
     */
    public List<WebScMenu> getALLMenuAndCountOperatorTree() {
        return menuMapper.selectAllMenuAndCountOperator();
    }

    /**
     * 获取当前登陆用户拥有的树形菜单 (root 账户拥有所有权限.)
     */
    public List<WebScMenu> selectCurrentUserMenuTree() {
    	WebScUser user = ShiroUtil.getCurrentUser();
        return selectMenuTreeVOByUsername(user.getLoginName());
    }

    /**
     * 获取指定用户拥有的树形菜单 (root 账户拥有所有权限.)
     */
    public List<WebScMenu> selectMenuTreeVOByUsername(String loginname) {
        List<WebScMenu> menus;
        if (shiroActionProperties.getSuperAdminUsername().equals(loginname)) {
            menus = menuMapper.selectAll();
        } else {
            menus = menuMapper.selectMenuByLoginName(loginname);
        }
        return toTree(menus);
    }

    /**
     * 获取导航菜单中的所有叶子节点
     */
    public List<WebScMenu> getLeafNodeMenu() {
        List<WebScMenu> allMenuTreeVO = getALLTree();
        return TreeUtil.getAllLeafNode(allMenuTreeVO);
    }

    public void insert(WebScMenu menu) {
        int maxOrderNum = menuMapper.selectMaxOrderNum();
        menu.setOrderNum(maxOrderNum + 1);
        menuMapper.insert(menu);
    }

    public void updateByPrimaryKey(WebScMenu menu) {
        menuMapper.updateByPrimaryKey(menu);
    }


    /**
     * 删除当前菜单以及其子菜单
     */
    @Transactional
    public void deleteByIDAndChildren(Integer menuId) {
        // 删除子菜单
        List<Integer> childIDList = menuMapper.selectChildrenIDByPrimaryKey(menuId);
        for (Integer childID : childIDList) {
            deleteByIDAndChildren(childID);
        }
        // 删除菜单下的操作权限
        operatorMapper.deleteByMenuId(menuId);
        // 删除分配给用户的菜单
        roleMenuMapper.deleteByMenuId(menuId);
        // 删除自身
        menuMapper.deleteByPrimaryKey(menuId);
    }

    public int count() {
        return menuMapper.count();
    }

    public void swapSort(Integer currentId, Integer swapId) {
        menuMapper.swapSort(currentId, swapId);
    }

    /**
     * 转换为树形结构
     */
    private List<WebScMenu> toTree(List<WebScMenu> menuList) {
        return TreeUtil.toTree(menuList, "menuId", "parentId", "children", WebScMenu.class);
    }

    public List<WebScMenu> getALLMenuAndCountOperatorTreeAndRoot() {
        List<WebScMenu> menus = getALLMenuAndCountOperatorTree();
        return addRootNode("导航目录", 0, menus);
    }

    /**
     * 将树形结构添加到一个根节点下.
     */
    private List<WebScMenu> addRootNode(String rootName, Integer rootId, List<WebScMenu> children) {
        WebScMenu root = new WebScMenu();
        root.setMenuId(rootId);
        root.setMenuName(rootName);
        root.setChildren(children);
        List<WebScMenu> rootList = new ArrayList<>();
        rootList.add(root);
        return rootList;
    }
}