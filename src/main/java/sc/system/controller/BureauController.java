package sc.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import sc.common.util.ResultBean;

/**
 * 卫监局
 * @author aisino
 *
 */
@Controller
@RequestMapping("/bureau")
public class BureauController {

	@GetMapping("/tree")
    @ResponseBody
    public ResultBean tree() {
        return ResultBean.success(deptService.selectAllDeptTree());
    }
}
