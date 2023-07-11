package com.lianziyou.bot.controller.midjourney.support;

import com.lianziyou.bot.base.result.ApiResult;
import com.lianziyou.bot.constant.CommonConst;
import com.lianziyou.bot.enums.mj.TaskStatus;
import com.lianziyou.bot.enums.sys.ResultEnum;
import com.lianziyou.bot.model.MjTask;
import com.lianziyou.bot.service.mj.NotifyService;
import com.lianziyou.bot.service.mj.TaskStoreService;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskQueueHelper {

    private final ThreadPoolTaskExecutor taskExecutor;
    private final List<MjTask> runningMjTasks;
    private final Map<Long, Future<?>> taskFutureMap = Collections.synchronizedMap(new HashMap<>());
    @Resource
    private TaskStoreService taskStoreService;
    @Resource
    private NotifyService notifyService;

    public TaskQueueHelper() {
        this.taskExecutor = new ThreadPoolTaskExecutor();
        this.runningMjTasks = new CopyOnWriteArrayList<>();
        this.taskExecutor.setCorePoolSize(CommonConst.MJ_TASK_QUEUE_CORE_SIZE);
        this.taskExecutor.setMaxPoolSize(CommonConst.MJ_TASK_QUEUE_CORE_SIZE);
        this.taskExecutor.setQueueCapacity(CommonConst.MJ_TASK_QUEUE_QUEUE_SIZE);
        this.taskExecutor.setThreadNamePrefix("TaskQueue-");
        this.taskExecutor.initialize();
    }

    public Set<Long> getQueueTaskIds() {
        return this.taskFutureMap.keySet();
    }

    public MjTask getRunningTask(Long id) {
        if (null == id) {
            return null;
        }
        return this.runningMjTasks.stream().filter(t -> id.equals(t.getId())).findFirst().orElse(null);
    }

    public Stream<MjTask> findRunningTask(Predicate<MjTask> condition) {
        return this.runningMjTasks.stream().filter(condition);
    }

    public Future<?> getRunningFuture(Long taskId) {
        return this.taskFutureMap.get(taskId);
    }

    public ApiResult<Void> submitTask(MjTask mjTask, Callable<ApiResult<Void>> discordSubmit) {
        this.taskStoreService.save(mjTask);
        int size;
        try {
            size = this.taskExecutor.getThreadPoolExecutor().getQueue().size();
            Future<?> future = this.taskExecutor.submit(() -> executeTask(mjTask, discordSubmit));
            this.taskFutureMap.put(mjTask.getId(), future);
        } catch (RejectedExecutionException e) {
            this.taskStoreService.delete(mjTask.getId());
            return ApiResult.finalBuild("队列已满，请稍后尝试");
        } catch (Exception e) {
            log.error("submit mjTask error", e);
            return ApiResult.finalBuild("提交失败，系统异常");
        }
        if (size == 0) {
            return ApiResult.okBuild();
        } else {
            return ApiResult.finalBuild("排队中，前面还有" + size + "个任务");
        }
    }

    private void executeTask(MjTask mjTask, Callable<ApiResult<Void>> discordSubmit) {
        this.runningMjTasks.add(mjTask);
        try {
            mjTask.start();
            ApiResult<Void> result = discordSubmit.call();
            if (result.getStatus() != ResultEnum.SUCCESS.getCode()) {
                mjTask.fail(result.getMessage());
                changeStatusAndNotify(mjTask, TaskStatus.FAILURE);
                return;
            }
            changeStatusAndNotify(mjTask, TaskStatus.SUBMITTED);
            do {
                mjTask.sleep();
                changeStatusAndNotify(mjTask, mjTask.getStatus());
            } while (mjTask.getStatus() == TaskStatus.IN_PROGRESS);
            log.debug("mjTask finished, id: {}, status: {}", mjTask.getId(), mjTask.getStatus());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("mjTask execute error", e);
            mjTask.fail("执行错误，系统异常");
            changeStatusAndNotify(mjTask, TaskStatus.FAILURE);
        } finally {
            this.runningMjTasks.remove(mjTask);
            this.taskFutureMap.remove(mjTask.getId());
        }
    }

    public void changeStatusAndNotify(MjTask mjTask, TaskStatus status) {
        mjTask.setStatus(status);
        this.taskStoreService.save(mjTask);
        this.notifyService.notifyTaskChange(mjTask);
    }
}