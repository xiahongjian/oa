package tech.hongjian.oa.controller;

import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.hongjian.oa.flowable.FlowVariables;
import tech.hongjian.oa.flowable.service.FlowService;
import tech.hongjian.oa.model.R;
import tech.hongjian.oa.service.BizTaskService;
import tech.hongjian.oa.util.CommonUtil;
import tech.hongjian.oa.util.WebUtil;

import java.util.Collections;

/**
 * Created by xiahongjian on 2021/4/10.
 */
@RestController
@RequestMapping("/processes/tasks")
public class TaskController {
    @Setter(onMethod_ = {@Autowired})
    private BizTaskService bizTaskService;

    @Setter(onMethod_ = {@Autowired})
    private FlowService flowService;

    @GetMapping("")
    public R listTask(@RequestParam(required = false) String procDefKey,
                      @RequestParam(required = false) String name,
                      @RequestParam(required = false) Integer suspend,
                      @RequestParam int page,
                      @RequestParam int limit) {
        return R.ok(bizTaskService.listTask(procDefKey, name, null, CommonUtil.isSuspendState(suspend), page, limit, true));
    }

    @GetMapping("/mine")
    public R listMyTask(@RequestParam(required = false) String procDefKey,
                        @RequestParam(required = false) String name,
                        @RequestParam(required = false) Integer suspend,
                        @RequestParam int page,
                        @RequestParam int limit) {
        return R.ok(bizTaskService.listTask(procDefKey, name, WebUtil.currentUser().getId(), CommonUtil.isSuspendState(suspend), page, limit, true));
    }

    @PutMapping("/{taskId}/complete")
    public R completeTask(@PathVariable String taskId, @RequestParam(required = false) String action, @RequestParam(required = false) String comment) {
        if (StringUtils.isNotBlank(action)) {
            flowService.completeTask(taskId, comment, Collections.singletonMap(FlowVariables.V_ACTION, action));
        } else {
            flowService.completeTask(taskId, comment, null);
        }
        return R.ok();
    }

    @PutMapping("/{taskId}/reassign")
    public R reassign(@PathVariable String taskId, @RequestParam Integer userId, @RequestParam(required = false) String comment) {
        flowService.reassign(taskId, userId, comment);
        return R.ok();
    }


}
