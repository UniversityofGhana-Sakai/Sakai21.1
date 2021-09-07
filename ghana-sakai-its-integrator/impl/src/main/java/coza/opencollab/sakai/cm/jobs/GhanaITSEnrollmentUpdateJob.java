package coza.opencollab.sakai.cm.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import coza.opencollab.sakai.api.service.ITSIntegratorService;
import lombok.extern.slf4j.Slf4j;

/**
 * This is a quartz job that will update Course sites with enrolled (adding/removing) Student, creating Sakai sites if they do not exist from the ITS course data
 * data.
 * 
 * @author JC Gillman
 * 
 */
@Slf4j
public class GhanaITSEnrollmentUpdateJob implements Job {

	private ITSIntegratorService integratorService;
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info("GhanaITSEnrollmentUpdateJob - execute start");
		getIntegratorService().updateITSEnrollments();
		log.info("GhanaITSEnrollmentUpdateJob - execute finished");
	}

	public ITSIntegratorService getIntegratorService() {
		return integratorService;
	}

	public void setIntegratorService(ITSIntegratorService integratorService) {
		this.integratorService = integratorService;
	}	
}
