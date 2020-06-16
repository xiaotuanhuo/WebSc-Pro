package sc.system.controller;

import sc.common.annotation.OperationLog;
import sc.common.util.ResultBean;
import sc.system.model.WebScDept;
import sc.system.service.DeptService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/dept")
public class DeptController {

    @Resource
    private DeptService deptService;

    @GetMapping("/index")
    public String index() {
        return "dept/dept-list";
    }

    @OperationLog("获取部门列表")
    @GetMapping("/list")
    @ResponseBody
    public ResultBean getList(@RequestParam(required = false) Integer parentId) {
        List<WebScDept> deptList = deptService.selectByParentId(parentId);
        return ResultBean.success(deptList);
    }

    @GetMapping("/tree/root")
    @ResponseBody
    public ResultBean treeAndRoot() {
        return ResultBean.success(deptService.selectAllDeptTreeAndRoot());
    }

    @GetMapping("/tree")
    @ResponseBody
    public ResultBean tree() {
        return ResultBean.success(deptService.selectAllDeptTree());
    }

    @GetMapping
    public String add() {
        return "dept/dept-add";
    }

    @OperationLog("新增部门")
    @PostMapping
    @ResponseBody
    public ResultBean add(WebScDept dept) {
        return ResultBean.success(deptService.insert(dept));
    }

    @OperationLog("删除部门")
    @DeleteMapping("/{deptId}")
    @ResponseBody
    public ResultBean delete(@PathVariable("deptId") Integer deptId) {
        deptService.deleteCascadeByID(deptId);
        return ResultBean.success();
    }

    @OperationLog("修改部门")
    @PutMapping
    @ResponseBody
    public ResultBean update(WebScDept dept) {
        deptService.updateByPrimaryKey(dept);
        return ResultBean.success();
    }

    @GetMapping("/{deptId}")
    public String update(@PathVariable("deptId") Integer deptId, Model model) {
        WebScDept dept = deptService.selectByPrimaryKey(deptId);
        model.addAttribute("dept", dept);
        return "dept/dept-add";
    }

    @OperationLog("调整部门排序")
    @PostMapping("/swap")
    @ResponseBody
    public ResultBean swapSort(Integer currentId, Integer swapId) {
        deptService.swapSort(currentId, swapId);
        return ResultBean.success();
    }

}
