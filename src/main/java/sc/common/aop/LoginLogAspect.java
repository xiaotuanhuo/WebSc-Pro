package sc.common.aop;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import sc.common.util.IPUtils;
import sc.system.model.WebScUser;
import sc.system.service.LoginLogService;

@Aspect
@Component
@ConditionalOnProperty(value = "shiro-action.log.login", havingValue = "true")
public class LoginLogAspect {

    @Resource
    private LoginLogService loginLogService;

    @Pointcut("execution(sc.common.util.ResultBean sc.system.controller..LoginController.login(sc.system.model.User, String) )")
    public void loginLogPointCut() {}

    @After("loginLogPointCut()")
    public void recordLoginLog(JoinPoint joinPoint) {
        // 获取登陆参数
        Object[] args = joinPoint.getArgs();
        WebScUser user = (WebScUser) args[0];

        Subject subject = SecurityUtils.getSubject();

        String ip = IPUtils.getIpAddr();
        loginLogService.addLog(user.getLoginName(), subject.isAuthenticated(), ip);
    }
}