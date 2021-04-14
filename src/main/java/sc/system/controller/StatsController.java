package sc.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sc.common.annotation.OperationLog;

/**
 * 报表统计业务
 * @author aisino
 *
 */
@Controller
@RequestMapping("/stats")
public class StatsController {
	
	@GetMapping("/doctor")
	public String index() {
		return "stats/doctor-list";
	}
	
}
