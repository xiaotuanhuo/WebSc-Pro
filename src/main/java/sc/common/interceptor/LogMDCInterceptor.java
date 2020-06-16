package sc.common.interceptor;

import sc.common.util.IPUtils;
import sc.system.model.WebScUser;

import org.apache.shiro.SecurityUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * MDC 拦截器, 用于将当前操作人的用户名及 IP 添加到 MDC 中. 以在日志中进行显示.
 */
@Component
public class LogMDCInterceptor implements HandlerInterceptor {
 
    private final static String MDC_LOGINNAME = "loginName";

    private final static String IP = "ip";

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) {
        // 如已进行登录, 则获取当前登录者的用户名放入 MDC 中.
        String loginname = "";
        WebScUser user = (WebScUser) SecurityUtils.getSubject().getPrincipal();
        if (user != null) {
        	loginname = user.getLoginName();
        }

        MDC.put(IP, IPUtils.getIpAddr());
        MDC.put(MDC_LOGINNAME, loginname);
        return true;
    }
 
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) {
        String loginname = MDC.get(MDC_LOGINNAME);
        MDC.remove(loginname);
    }
}