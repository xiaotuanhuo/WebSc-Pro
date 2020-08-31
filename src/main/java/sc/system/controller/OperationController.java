package sc.system.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import sc.common.annotation.OperationLog;
import sc.common.util.ResultBean;
import sc.system.model.WebScUser;
import sc.system.service.OperationService;

/**
 * 手术类型
 * @author aisino
 *
 */
@Controller
@RequestMapping("/operation")
public class OperationController {
	
	@Resource
	private OperationService operationService;

	@OperationLog("获取手术类型列表")
    @GetMapping("/list")
    @ResponseBody
    public ResultBean getList() {
		return ResultBean.success(operationService.getAll());
	}
	
	@OperationLog("新增用户")
    @PostMapping
    @ResponseBody
    public ResultBean add(WebScUser user) {
		System.out.println("23");
		return ResultBean.success();
	}
}
