package sc.system.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import sc.common.annotation.OperationLog;
import sc.common.util.ShiroUtil;
import sc.system.model.WebScUser;
import sc.system.model.WebScMenu;
import sc.system.service.LoginLogService;
import sc.system.service.MenuService;
import sc.system.service.RoleService;
import sc.system.service.SysLogService;
import sc.system.service.UserOnlineService;
import sc.system.service.UserService;

@Controller
public class IndexController {

    @Resource
    private MenuService menuService;

    @Resource
    private LoginLogService loginLogService;

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Resource
    private SysLogService sysLogService;

    @Resource
    private UserOnlineService userOnlineService;
    
    @Value("${appiconpath}")
	private String appiconpath;

    @GetMapping(value = {"/", "/main"})
    public String index(Model model) {
        List<WebScMenu> menuTreeVOS = menuService.selectCurrentUserMenuTree();
        model.addAttribute("menus", menuTreeVOS);
        WebScUser user = ShiroUtil.getCurrentUser();
        model.addAttribute("test", user.getUserId());
        return "index";
    }

    @OperationLog("访问我的桌面")
    @GetMapping("/welcome")
    public String welcome(Model model) throws UnsupportedEncodingException {
//        int userCount = userService.count();
//        int roleCount = roleService.count();
//        int menuCount = menuService.count();
//        int loginLogCount = loginLogService.count();
//        int sysLogCount = sysLogService.count();
//        int userOnlineCount = userOnlineService.count();
//
//        model.addAttribute("userCount", userCount);
//        model.addAttribute("roleCount", roleCount);
//        model.addAttribute("menuCount", menuCount);
//        model.addAttribute("loginLogCount", loginLogCount);
//        model.addAttribute("sysLogCount", sysLogCount);
//        model.addAttribute("userOnlineCount", userOnlineCount);
//    	User user = ShiroUtil.getCurrentUser();
//    	List<App> appls = appService.selectAppByLoginName(user.getLoginname());
//    	List<App> rappls = new ArrayList<App>();
//    	
//    	UserToken ut = new UserToken();
//    	ut.setUserId(user.getUserid());
//    	ut.setToken(UUID.randomUUID().toString());
//    	//更新数据库
//    	ut = appService.updateUserToken(ut);
//    	
//    	if(appls != null){
//	    	for(App app : appls){
//	    		app.setAppBackUrl(app.getAppBackUrl() + "?code=0&token=" + ut.getToken());
//	    		
//	    		app.setAppIconSrc("data:image/jpeg;base64," + ShiroUtil.getImageStr(appiconpath + File.separator +app.getAppIcon()));
//	    		rappls.add(app);
//	    	}
//    	}
//    	model.addAttribute("appls", rappls);
        return "welcome";
    }

    @OperationLog("查看近七日登录统计图")
    @GetMapping("/weekLoginCount")
    @ResponseBody
    public List<Integer> recentlyWeekLoginCount() {
        return loginLogService.recentlyWeekLoginCount();
    }
}
