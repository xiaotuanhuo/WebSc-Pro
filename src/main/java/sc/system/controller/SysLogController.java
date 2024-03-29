package sc.system.controller;

import com.github.pagehelper.PageInfo;
import sc.common.annotation.OperationLog;
import sc.common.util.PageResultBean;
import sc.system.model.SysLog;
import sc.system.service.SysLogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/log/sys")
public class SysLogController {

    @Resource
    private SysLogService sysLogService;

    @GetMapping("/index")
    public String index() {
        return "log/sys-logs";
    }

    @OperationLog("查看操作日志")
    @GetMapping("/list")
    @ResponseBody
    public PageResultBean<SysLog> getList(@RequestParam(value = "page", defaultValue = "1") int page,
                                          @RequestParam(value = "limit", defaultValue = "10")int limit) {
        List<SysLog> loginLogs = sysLogService.selectAll(page, limit);
        PageInfo<SysLog> rolePageInfo = new PageInfo<>(loginLogs);
        return new PageResultBean<>(rolePageInfo.getTotal(), rolePageInfo.getList());
    }

}
