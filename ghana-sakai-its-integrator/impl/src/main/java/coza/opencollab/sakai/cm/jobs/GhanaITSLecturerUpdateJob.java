package coza.opencollab.sakai.cm.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import coza.opencollab.sakai.api.service.ITSIntegratorService;
import lombok.extern.slf4j.Slf4j;

/**
 * This is a quartz job that will update (adding/removing) Course sites with Lecturers
 * 
 * @author JC Gillman
 * 
 */
@Slf4j
public class GhanaITSLecturerUpdateJob implements Job {
	
	private ITSIntegratorService integratorService;
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info("GhanaITSLecturerUpdateJob - execute start");
		getIntegratorService().updateITSLecturers();
		log.info("GhanaITSLecturerUpdateJob - execute finished");
	}

	public ITSIntegratorService getIntegratorService() {
		return integratorService;
	}

	public void setIntegratorService(ITSIntegratorService integratorService) {
		this.integratorService = integratorService;
	}
}

