package sc.system.controller;

import com.github.pagehelper.PageInfo;
import sc.common.annotation.OperationLog;
import sc.common.util.PageResultBean;
import sc.common.util.ResultBean;
import sc.common.validate.groups.Create;
import sc.system.model.WebScUser;
import sc.system.service.RoleService;
import sc.system.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @GetMapping("/index")
    public String index() {
        return "user/user-list";
    }

    @OperationLog("获取用户列表")
    @GetMapping("/list")
    @ResponseBody
    public PageResultBean<WebScUser> getList(@RequestParam(value = "page", defaultValue = "1") int page,
                                        @RequestParam(value = "limit", defaultValue = "10") int limit,
                                        WebScUser userQuery) {
        List<WebScUser> users = userService.selectAllWithGroup(page, limit, userQuery);
        PageInfo<WebScUser> userPageInfo = new PageInfo<>(users);
        return new PageResultBean<>(userPageInfo.getTotal(), userPageInfo.getList());
    }

    @GetMapping
    public String add(Model model) {
        model.addAttribute("roles", roleService.selectAll());
        return "user/user-add";
    }

    @GetMapping("/{userId}")
    public String update(@PathVariable("userId") Integer userId, Model model) {
        model.addAttribute("roleIds", userService.selectRoleIdsById(userId));
        model.addAttribute("user", userService.selectOne(userId));
        model.addAttribute("roles", roleService.selectAll());
        return "user/user-add";
    }

    @OperationLog("编辑角色")
    @PutMapping
    @ResponseBody
    public ResultBean update(@Valid WebScUser user, @RequestParam(value = "role[]", required = false) Integer[] roleIds) {
        userService.update(user, roleIds);
        return ResultBean.success();
    }

    @OperationLog("新增用户")
    @PostMapping
    @ResponseBody
    public ResultBean add(@Validated(Create.class) WebScUser user, @RequestParam(value = "role[]", required = false) Integer[] roleIds) {
        return ResultBean.success(userService.add(user, roleIds));
    }

    @OperationLog("禁用账号")
    @PostMapping("/{userId:\\d+}/disable")
    @ResponseBody
    public ResultBean disable(@PathVariable("userId") Integer userId) {
        return ResultBean.success(userService.disableUserByID(userId));
    }

    @OperationLog("激活账号")
    @PostMapping("/{userId}/enable")
    @ResponseBody
    public ResultBean enable(@PathVariable("userId") Integer userId) {
        return ResultBean.success(userService.enableUserByID(userId));
    }

    @OperationLog("删除账号")
    @DeleteMapping("/{userId}")
    @ResponseBody
    public ResultBean delete(@PathVariable("userId") Integer userId) {
        userService.delete(userId);
        return ResultBean.success();
    }

    @GetMapping("/{userId}/reset")
    public String resetPassword(@PathVariable("userId") Integer userId, Model model) {
        model.addAttribute("userId", userId);
        return "user/user-reset-pwd";
    }

    @OperationLog("重置密码")
    @PostMapping("/{userId}/reset")
    @ResponseBody
    public ResultBean resetPassword(@PathVariable("userId") Integer userId, String password) {
        userService.updatePasswordByUserId(userId, password);
        return ResultBean.success();
    }
    
    @GetMapping("/{userId}/updatepwd")
    public String updatePassword(@PathVariable("userId") Integer userId, Model model) {
        model.addAttribute("userId", userId);
        return "user/user-update-pwd";
    }
    
    @OperationLog("重置密码")
    @PostMapping("/{userId}/updatepwd")
    @ResponseBody
    public ResultBean updatePassword(@PathVariable("userId") Integer userId, String password) {
        userService.updatePasswordByUserId(userId, password);
        return ResultBean.success();
    }
}