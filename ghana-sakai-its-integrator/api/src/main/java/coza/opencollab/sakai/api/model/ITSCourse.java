package coza.opencollab.sakai.api.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "its_courses")
@Data
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@NamedQueries({
		@NamedQuery(name = "FindCoursesByAcadYear", query = "SELECT itsc FROM ITSCourse itsc WHERE itsc.acadYear = :acadYear AND itsc.sakaiSiteId is null"),
		@NamedQuery(name = "FindCoursesForCurrentAndNextAcadYear", query = "SELECT itsc FROM ITSCourse itsc WHERE (itsc.acadYear = :acadYear OR itsc.acadYear = :acadYearNext) ORDER BY sakaiSiteId") })

public class ITSCourse {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;

	@EqualsAndHashCode.Include
	@Column(name = "ACAD_YR", nullable = false)
	private int acadYear;

	@EqualsAndHashCode.Include
	@Column(name = "COURSE_CODE", length = 7, nullable = false)
	private String courseCode;

	@Column(name = "COURSE", length = 80, nullable = false)
	private String course;

	@EqualsAndHashCode.Include
	@Column(name = "SEM_CODE", length = 2, nullable = false)
	private String semCode;

	@Column(name = "SEMESTER", length = 80, nullable = false)
	private String semester;

	@EqualsAndHashCode.Include
	@Column(name = "CAMPUS_CODE", length = 2, nullable = false)
	private String campusCode;

	@Column(name = "CAMPUS", length = 30, nullable = false)
	private String campus;

	@Column(name = "SAKAI_SITE_ID", length = 30, nullable = true)
	private String sakaiSiteId;

	@Column(name = "CREATE_DATE", nullable = true)
	private Date createDate;

	@Column(name = "MODIFIED_DATE", nullable = true)
	private Date modifiedDate;

	// @OneToOne
	// @JoinColumn(foreignKey = @ForeignKey(name = "fk_course_site"))
	// private SakaiSite site;

	public ITSCourse() {
	}

	public ITSCourse(int acadYear, String courseCode, String course, String semCode, String semester, String campusCode,
			String campus, String sakaiSiteId, Date createDate, Date modifiedDate) {
		this.acadYear = acadYear;
		this.courseCode = courseCode;
		this.course = course;
		this.semCode = semCode;
		this.semester = semester;
		this.campusCode = campusCode;
		this.campus = campus;
		this.sakaiSiteId = sakaiSiteId;
		this.createDate = createDate;
		this.modifiedDate = modifiedDate;
	}

}
