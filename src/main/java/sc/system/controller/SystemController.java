package sc.system.controller;

import sc.common.annotation.OperationLog;
import sc.common.information.Server;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SystemController {

    @OperationLog("查看系统信息")
    @GetMapping("/system/index")
    public String index(Model model) throws Exception {
        Server server = new Server();
        server.copyTo();
        model.addAttribute("server", server);
        return "system/index";
    }
}
