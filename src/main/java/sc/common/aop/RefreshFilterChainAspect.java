package sc.common.aop;

import sc.system.service.ShiroService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 更新过滤器链
 */
@Aspect
@Component
public class RefreshFilterChainAspect {

    @Resource
    private ShiroService shiroService;

    @Pointcut("@annotation(sc.common.annotation.RefreshFilterChain)")
    public void updateFilterChain() {}

    @AfterReturning("updateFilterChain()")
    public void doAfter() {
        shiroService.updateFilterChain();
    }

}