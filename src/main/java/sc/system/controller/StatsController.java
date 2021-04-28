package sc.system.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;

import sc.common.annotation.OperationLog;
import sc.common.util.PageResultBean;
import sc.common.util.ResultBean;
import sc.system.model.WebScUser;
import sc.system.model.vo.Vote;
import sc.system.service.StatsService;

/**
 * 报表统计业务
 * @author aisino
 *
 */
@Controller
@RequestMapping("/stats")
public class StatsController {
	
	@Resource
	private StatsService statsService;
	
	@GetMapping("/doctor")
	public String index() {
		return "stats/doctor-list";
	}
	
	@OperationLog("医生统计列表")
	@GetMapping("/doctor/list")
	@ResponseBody
	public PageResultBean<WebScUser> getList(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "limit", defaultValue = "10") int limit, String name) {
		List<WebScUser> doctors = statsService.stats(page, limit, name);
		PageInfo<WebScUser> userPageInfo = new PageInfo<>(doctors);
		return new PageResultBean<>(userPageInfo.getTotal(), userPageInfo.getList());
	}
	
	@RequestMapping("/vote/{id}")
	@ResponseBody
	public ResultBean vote(@PathVariable("id") String id) {
		return ResultBean.success(statsService.vote(id));
	}
	
}
