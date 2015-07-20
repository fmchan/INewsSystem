package net.fmchan.util;
import java.util.Date;

import net.fmchan.job.CheckUpdateJob;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

public class JobUtil {
	final static Logger logger = Logger.getLogger(JobUtil.class);

	public static void job() {
		JobDetail job = new JobDetail();
		job.setName("dummyJobName");
		job.setJobClass(CheckUpdateJob.class);

		// configure the scheduler time
		SimpleTrigger trigger = new SimpleTrigger();
		trigger.setName("dummyTriggerName");
		trigger.setStartTime(new Date(System.currentTimeMillis()));
		trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
		trigger.setRepeatInterval(ConfigUtil.get().getInt("ftp.interval"));

		// schedule it
		try {
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			logger.error("cannot do job: ", e);
		}
	}
}
