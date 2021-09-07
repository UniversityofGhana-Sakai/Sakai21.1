package coza.opencollab.sakai.impl;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.time.api.UserTimeService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.util.api.FormattedText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.javafaker.Faker;

import coza.opencollab.sakai.api.service.ITSIntegratorService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ITSIntegratorServiceTestConfiguration.class})
public class ITSIntegratorServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

    private static final Faker faker = new Faker();

    @Autowired private AuthzGroupService authzGroupService;
    @Autowired private SecurityService securityService;
    @Autowired private SessionManager sessionManager;
    @Autowired private ITSIntegratorService integratorService;
    @Autowired private EntityManager entityManager;
    @Autowired private ServerConfigurationService serverConfigurationService;
    @Autowired private SiteService siteService;
    @Autowired private FormattedText formattedText;
    @Resource(name = "org.sakaiproject.time.api.UserTimeService")
    private UserTimeService userTimeService;

    private ResourceLoader resourceLoader;

    @Before
    public void setUp() {
//        when(serverConfigurationService.getAccessUrl()).thenReturn("http://localhost:8080/access");
//        resourceLoader = mock(ResourceLoader.class);
//        when(resourceLoader.getLocale()).thenReturn(Locale.ENGLISH);
//        when(resourceLoader.getString("gen.inpro")).thenReturn("In progress");
//        when(resourceLoader.getString("gen.dra2")).thenReturn("Draft -");
//        when(resourceLoader.getString("gen.subm4")).thenReturn("Submitted");
//        when(resourceLoader.getString("gen.nograd")).thenReturn("No Grade");
//        when(resourceLoader.getString("ungra")).thenReturn("Ungraded");
//        when(resourceLoader.getString("pass")).thenReturn("Pass");
//        when(resourceLoader.getString("fail")).thenReturn("Fail");
//        when(resourceLoader.getString("gen.checked")).thenReturn("Checked");
//        when(resourceLoader.getString("assignment.copy")).thenReturn("Copy");
//        ((AssignmentServiceImpl) assignmentService).setResourceLoader(resourceLoader);
//        when(userTimeService.getLocalTimeZone()).thenReturn(TimeZone.getDefault());
    }

    @Test
    public void ITSIntegratorServiceIsValid() {
        Assert.assertNotNull(integratorService);
    }

    @Test
    public void updateITSCourses() {
    	integratorService.updateITSCourses();
//        String context = UUID.randomUUID().toString();
//        Assignment assignment = createNewAssignment(context);
//        String stringRef = AssignmentReferenceReckoner.reckoner().context(assignment.getContext()).subtype("a").id(assignment.getId()).reckon().getReference();
//        FakeReference reference = new FakeReference(assignmentService, stringRef);
//        assignmentService.parseEntityReference(stringRef, reference);
//        when(entityManager.newReference(stringRef)).thenReturn(reference);
//        Entity entity = assignmentService.createAssignmentEntity(assignment.getId());
//        Assert.assertEquals(assignment.getId(), entity.getId());
//        Assert.assertEquals(reference.getReference(), entity.getReference());
    }
}
