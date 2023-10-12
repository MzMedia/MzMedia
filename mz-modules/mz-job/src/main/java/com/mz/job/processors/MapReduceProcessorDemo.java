package com.mz.job.processors;

import cn.hutool.core.lang.Dict;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import com.mz.common.json.utils.JsonUtils;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.TaskResult;
import tech.powerjob.worker.core.processor.sdk.MapReduceProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * MapReduce 处理器示例
 * 控制台参数：{"batchSize": 100, "batchNum": 2}
 *
 * @author tjq
 * @since 2020/4/17
 */
@Slf4j
@Component
public class MapReduceProcessorDemo implements MapReduceProcessor {

    @Override
    public ProcessResult process(TaskContext context) throws Exception {

        OmsLogger omsLogger = context.getOmsLogger();

        log.info("============== TestMapReduceProcessor#process ==============");
        log.info("isRootTask:{}", isRootTask());
        log.info("taskContext:{}", JsonUtils.toJsonString(context));

        // 根据控制台参数获取MR批次及子任务大小
        final Dict jobParams = JsonUtils.parseMap(context.getJobParams());

        Integer batchSize = (Integer) jobParams.getOrDefault("batchSize", 100);
        Integer batchNum = (Integer) jobParams.getOrDefault("batchNum", 10);

        if (isRootTask()) {
            log.info("==== MAP ====");
            omsLogger.info("[DemoMRProcessor] start root task~");
            List<TestSubTask> subTasks = Lists.newLinkedList();
            for (int j = 0; j < batchNum; j++) {
                for (int i = 0; i < batchSize; i++) {
                    int x = j * batchSize + i;
                    subTasks.add(new TestSubTask("name" + x, x));
                }
                map(subTasks, "MAP_TEST_TASK");
                subTasks.clear();
            }
            omsLogger.info("[DemoMRProcessor] map success~");
            return new ProcessResult(true, "MAP_SUCCESS");
        } else {
            log.info("==== NORMAL_PROCESS ====");
            omsLogger.info("[DemoMRProcessor] process subTask: {}.", JsonUtils.toJsonString(context.getSubTask()));
            log.info("subTask: {}", JsonUtils.toJsonString(context.getSubTask()));
            Thread.sleep(1000);
            if (context.getCurrentRetryTimes() == 0) {
                return new ProcessResult(false, "FIRST_FAILED");
            } else {
                return new ProcessResult(true, "PROCESS_SUCCESS");
            }
        }
    }

    @Override
    public ProcessResult reduce(TaskContext context, List<TaskResult> taskResults) {
        log.info("================ MapReduceProcessorDemo#reduce ================");
        log.info("TaskContext: {}", JsonUtils.toJsonString(context));
        log.info("List<TaskResult>: {}", JsonUtils.toJsonString(taskResults));
        context.getOmsLogger().info("MapReduce job finished, result is {}.", taskResults);

        boolean success = ThreadLocalRandom.current().nextBoolean();
        return new ProcessResult(success, context + ": " + success);
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestSubTask {
        private String name;
        private int age;
    }
}
