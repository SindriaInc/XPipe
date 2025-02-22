package org.sindria.xpipe.lib.nanoREST.job;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.*;

public class CronJobDispatcher extends JobDispatcher {
    private final CronParser cronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));

    public CronJobDispatcher() {
        super();
    }

    public void scheduleCronJob(CronJob cronJob) {
        Cron cron = cronParser.parse(cronJob.getCronExpression());
        scheduler.scheduleAtFixedRate(() -> {
            ZonedDateTime now = ZonedDateTime.now();
            Optional<ZonedDateTime> nextExecution = ExecutionTime.forCron(cron).nextExecution(now);
            if (nextExecution.isPresent() && nextExecution.get().isBefore(now.plusSeconds(1))) {
                submitJob(cronJob);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }


}

