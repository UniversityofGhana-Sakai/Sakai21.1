package coza.opencollab.sakai.api.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "its_enrollments")
@Data
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@NamedQueries({
		@NamedQuery(name = "FindEnrollmentsByAcadYear", query = "SELECT itse FROM ITSEnrollment itse WHERE itse.acadYear = :acadYear"),
		@NamedQuery(name = "FindEnrollmentsByAcadYearOrderBySakaiSiteId", query = "SELECT itse FROM ITSEnrollment itse WHERE itse.acadYear = :acadYear ORDER BY sakaiSiteId"),
		@NamedQuery(name = "FindEnrollmentsForCurrentAndNextAcadYear", query = "SELECT itse FROM ITSEnrollment itse WHERE (itse.acadYear = :acadYear OR itse.acadYear = :acadYearNext) ORDER BY sakaiSiteId") })
public class ITSEnrollment {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@EqualsAndHashCode.Include
	@Column(name = "IAGSTNO", length = 9, nullable = false)
	private int IAGSTNO;

	@Column(name = "SURNAME", length = 45, nullable = false)
	private String surname;

	@Column(name = "OTHER_NAMES", length = 45, nullable = false)
	private String otherNames;

	@Column(name = "PIN", length = 10, nullable = false)
	private String pin;

	@EqualsAndHashCode.Include
	@Column(name = "ACAD_YR", length = 4, nullable = false)
	private int acadYear;

	@EqualsAndHashCode.Include
	@Column(name = "SEM_CODE", length = 2, nullable = false)
	private String semCode;

	@Column(name = "SEMESTER", length = 30, nullable = false)
	private String semester;

	@Column(name = "PROG_CODE", length = 6, nullable = false)
	private String progCode;

	@Column(name = "PROGRAM", length = 50, nullable = false)
	private String program;

	@EqualsAndHashCode.Include
	@Column(name = "COURSE_CODE", length = 7, nullable = false)
	private String courseCode;

	@Column(name = "COURSE", length = 80, nullable = false)
	private String course;

	@EqualsAndHashCode.Include
	@Column(name = "CAMPUS_CODE", length = 2, nullable = false)
	private String campusCode;

	@Column(name = "CAMPUS", length = 30, nullable = false)
	private String campus;

	@Column(name = "EMAIL", length = 80, nullable = false)
	private String email;

	@Column(name = "PHONE", length = 80, nullable = true)
	private String phone;

	@Column(name = "SAKAI_SITE_ID", length = 30, nullable = true)
	private String sakaiSiteId;

	@Column(name = "ITS_CREATE_DATE", nullable = true)
	private Date itsDateCreated;

	@Column(name = "CREATE_DATE", nullable = false)
	private Date createdDate;

	// not sure if this is needed seeing that the student number will be used?
	// @Column(name = "SAKAI_USER_ID", length = 30, nullable = true)
	// private String sakaiUserId;

	// @OneToOne
	// @JoinColumn(foreignKey = @ForeignKey(name = "fk_course_student"))
	// private SakaiUser user;

	// @OneToOne
	// @JoinColumn(foreignKey = @ForeignKey(name = "fk_course_site"))
	// private SakaiSite site;

	public ITSEnrollment() {
	}

	public ITSEnrollment(int iAGSTNO, int acadYear, String semCode, String semester, String progCode, String program,
			String courseCode, String course, String campusCode, String campus, String email, String phone,
			String sakaiSiteId, Date itsDateCreated, Date createdDate) {
		IAGSTNO = iAGSTNO;
		this.acadYear = acadYear;
		this.semCode = semCode;
		this.semester = semester;
		this.progCode = progCode;
		this.program = program;
		this.courseCode = courseCode;
		this.course = course;
		this.campusCode = campusCode;
		this.campus = campus;
		this.email = email;
		this.phone = phone;
		this.sakaiSiteId = sakaiSiteId;
		this.itsDateCreated = itsDateCreated;
		this.createdDate = createdDate;
	}

}
