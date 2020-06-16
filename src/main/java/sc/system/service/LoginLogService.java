package sc.system.service;

import com.github.pagehelper.PageHelper;
import sc.common.util.ShiroUtil;
import sc.system.mapper.LoginLogMapper;
import sc.system.model.LoginLog;
import sc.system.model.WebScUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 登陆日志 Service
 */
@Service
public class LoginLogService {

    @Resource
    private LoginLogMapper loginLogMapper;

    public void addLog(String username, boolean isAuthenticated, String ip) {
        LoginLog loginLog = new LoginLog();
        loginLog.setLoginTime(new Date());
        loginLog.setUsername(username);
        loginLog.setLoginStatus(isAuthenticated ? "1" : "0");
        loginLog.setIp(ip);
        loginLogMapper.insert(loginLog);
    }

    /**
     * 最近一周登陆次数
     */
    public List<Integer> recentlyWeekLoginCount() {
    	WebScUser user = ShiroUtil.getCurrentUser();
        return loginLogMapper.recentlyWeekLoginCount(user.getLoginName());
    }

    public List<LoginLog> selectAll(int page, int limit) {
        PageHelper.startPage(page, limit);
        return loginLogMapper.selectAll();
    }

    public int count() {
        return loginLogMapper.count();
    }
}