package sc.system.controller;

import com.github.pagehelper.PageInfo;
import sc.common.annotation.OperationLog;
import sc.common.util.PageResultBean;
import sc.common.util.ResultBean;
import sc.common.validate.groups.Create;
import sc.system.model.WebScUser;
import sc.system.service.RoleService;
import sc.system.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@Value("${upload-path}")
	private String uploadPath;
	
	@GetMapping("/index")
	public String index() {
		return "user/user-list";
	}
	
	@OperationLog("获取用户列表")
	@GetMapping("/list")
	@ResponseBody
	public PageResultBean<WebScUser> getList(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "limit", defaultValue = "10") int limit, WebScUser userQuery) {
		List<WebScUser> users = userService.selectAllWithGroup(page, limit, userQuery);
		PageInfo<WebScUser> userPageInfo = new PageInfo<>(users);
		return new PageResultBean<>(userPageInfo.getTotal(), userPageInfo.getList());
	}
	
    @GetMapping
	public String add(Model model) {
		return "user/user-add";
	}
    
    @GetMapping("/detail/{userId}")
	public String detail(@PathVariable("userId") String userId, Model model) {
    	WebScUser user = userService.selectOne(userId);
		model.addAttribute("user", user);
		model.addAttribute("roles", roleService.getRolesBytUserRole(Integer.parseInt(user.getRoleId())));
		return "user/user-detail";
    }
    
    /**
     * 医生、护士资质详情
     * @param userId
     * @param model
     * @return
     */
    @GetMapping("/credentials/{userId}")
	public String credentials(@PathVariable("userId") String userId, Model model) {
    	WebScUser user = userService.selectOne(userId);
		model.addAttribute("user", userService.selectOne(userId));
		model.addAttribute("patients", userService.getPatients(user.getPatientType()));
		model.addAttribute("operations", userService.getOperations(user.getOperationType()));
		return "user/credentials-info";
    }
    
    /**
     * 获取用户头像
     */
    @GetMapping("/picture")
    public void picture(String p, HttpServletResponse response) throws IOException {
    	File file = new File((uploadPath.endsWith("/") ? uploadPath : uploadPath + "/") + p);
    	FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
    }
    
	@GetMapping("/{userId}")
	public String update(@PathVariable("userId") String userId, Model model) {
//		model.addAttribute("roleIds", userService.selectRoleIdsById(userId));
		WebScUser user = userService.selectOne(userId);
		model.addAttribute("user", user);
		model.addAttribute("roles", roleService.getRolesBytUserRole(Integer.parseInt(user.getRoleId())));
		return "user/user-edit";
	}
	
	@OperationLog("新增用户")
	@PostMapping
	@ResponseBody
	public ResultBean add(@Validated(Create.class) WebScUser user) {
		return ResultBean.success(userService.add(user));
	}
	
	@OperationLog("编辑角色")
	@PutMapping
	@ResponseBody
	public ResultBean update(@Valid WebScUser user) {
		userService.update(user);
		return ResultBean.success();
	}
	
	@OperationLog("禁用账号")
	@PostMapping("/{userId:\\d+}/disable")
	@ResponseBody
	public ResultBean disable(@PathVariable("userId") String userId) {
		return ResultBean.success(userService.disableUserByID(userId));
	}
	
	@OperationLog("激活账号")
	@PostMapping("/{userId}/enable")
	@ResponseBody
	public ResultBean enable(@PathVariable("userId") String userId) {
		return ResultBean.success(userService.enableUserByID(userId));
	}
	
	@OperationLog("删除账号")
	@DeleteMapping("/{userId}")
	@ResponseBody
	public ResultBean delete(@PathVariable("userId") String userId) {
		userService.delete(userId);
		return ResultBean.success();
	}
	
	@GetMapping("/reset/{userId}")
	public String resetPassword(@PathVariable("userId") String userId, Model model) {
		model.addAttribute("userId", userId);
		return "user/user-reset-pwd";
	}
	
	@OperationLog("重置密码")
	@PostMapping("/reset/{userId}")
	@ResponseBody
	public ResultBean resetPassword(@PathVariable("userId") String userId, String password) {
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
	public ResultBean updatePassword(@PathVariable("userId") String userId, String password) {
		userService.updatePasswordByUserId(userId, password);
		return ResultBean.success();
	}
	
	@OperationLog("锁定用户")
	@PutMapping("/lock/{userId}")
	@ResponseBody
	public ResultBean lock(@PathVariable("userId") String userId) {
		userService.lock(userId);
		return ResultBean.success();
	}
	
	@OperationLog("激活用户")
	@PutMapping("/unlock/{userId}")
	@ResponseBody
	public ResultBean unlock(@PathVariable("userId") String userId) {
		userService.unlock(userId);
		return ResultBean.success();
	}
	
	@OperationLog("图片上传")
	@PostMapping(value = "upload")
	@ResponseBody
	public ResultBean imgUpload(@RequestParam("file") MultipartFile file) {
		return userService.upload(file, uploadPath);
	}
}