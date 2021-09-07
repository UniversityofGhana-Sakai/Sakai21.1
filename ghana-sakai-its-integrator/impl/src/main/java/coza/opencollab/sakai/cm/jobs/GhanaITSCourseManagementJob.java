package coza.opencollab.sakai.cm.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import coza.opencollab.sakai.api.service.ITSIntegratorService;
import lombok.extern.slf4j.Slf4j;

/**
 * This is a quartz job that sync with Ghana ITS to get all course data
 *
 * @author JC Gillman
 * 
 */
@Slf4j
public class GhanaITSCourseManagementJob implements Job {
	
	private ITSIntegratorService integratorService;
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info("GhanaITSCourseManagementJob - execute start");
		getIntegratorService().updateITSCourses();
		log.info("GhanaITSCourseManagementJob - execute finished");
	}
	
	public ITSIntegratorService getIntegratorService() {
		return integratorService;
	}

	public void setIntegratorService(ITSIntegratorService integratorService) {
		this.integratorService = integratorService;
	}	
}