package sc.system.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import sc.common.util.ResultBean;
import sc.system.model.vo.CDO;
import sc.system.service.FrameworkService;

/**
 * 组织架构
 * group：医疗（医生）集团
 * cdc：卫监局
 * cdo：医疗机构
 * @author aisino
 *
 */
@Controller
@RequestMapping("/framework")
public class FrameworkController {
	
	@Resource
	private FrameworkService frameworkService;

	@GetMapping("/cdo/index")
	public String index() {
        return "framework/cdo-list";
    }
	
	@GetMapping("/tree/root")
	@ResponseBody
	public ResultBean tree() {
//		List<CDO> cdos = frameworkService.selectCdoTreeAndRoot();
//		String aa = JSONObject.toJSONString(cdos);
		return ResultBean.success(frameworkService.selectOrganizationTree());
	}
}
