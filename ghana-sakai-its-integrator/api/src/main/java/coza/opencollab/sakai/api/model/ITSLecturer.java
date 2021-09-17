package coza.opencollab.sakai.api.model;

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
@Table(name = "its_lecturers")
@Data
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@NamedQueries({
		@NamedQuery(name = "FindLecturersByAcadYear", query = "SELECT itsl FROM ITSLecturer itsl WHERE itsl.acadYear = :acadYear"),
		@NamedQuery(name = "FindLecturersByAcadYearOrderBySakaiSiteId", query = "SELECT itsl FROM ITSLecturer itsl WHERE itsl.acadYear = :acadYear ORDER BY sakaiSiteId"),
		@NamedQuery(name = "FindLecturersForCurrentAndNextAcadYear", query = "SELECT itsl FROM ITSLecturer itsl WHERE (itsl.acadYear = :acadYear OR itsl.acadYear = :acadYearNext) ORDER BY sakaiSiteId") })
public class ITSLecturer {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	
	@EqualsAndHashCode.Include
	@Column(name = "STAFFNO", length = 9, nullable = false)
	private int STAFFNO;

	@Column(name = "PIN", length = 10, nullable = false)
	private String pin;

	@Column(name = "SURNAME", length = 45, nullable = false)
	private String surname;

	@Column(name = "OTHERNAMES", length = 45, nullable = false)
	private String otherNames;

	@EqualsAndHashCode.Include
	@Column(name = "ACAD_YEAR", length = 4, nullable = false)
	private int acadYear;

	@EqualsAndHashCode.Include
	@Column(name = "DEPT", length = 45, nullable = false)
	private String dept;

	@Column(name = "TEL", length = 45, nullable = true)
	private String tel;

	@Column(name = "EMAIL", length = 45, nullable = false)
	private String email;

	@EqualsAndHashCode.Include
	@Column(name = "COURSE", length = 80, nullable = false)
	private String course;

	@EqualsAndHashCode.Include
	@Column(name = "OFFERING_TYPE", length = 45, nullable = false)
	private String offeringType;

	@EqualsAndHashCode.Include
	@Column(name = "SEMESTER", length = 80, nullable = false)
	private String semester;

	@Column(name = "SAKAI_SITE_ID", length = 30, nullable = true)
	private String sakaiSiteId;

	// not sure if this is needed seeing that the staff number will be used?
	// @Column(name = "SAKAI_USER_ID", length = 30, nullable = true)
	// private String sakaiUserId;

	// @OneToOne
	// @JoinColumn(foreignKey = @ForeignKey(name = "fk_course_teacher"))
	// private SakaiUser user;

	// @OneToOne
	// @JoinColumn(foreignKey = @ForeignKey(name = "fk_course_site"))
	// private SakaiSite site;

	public ITSLecturer() {
	}

	public ITSLecturer(int sTAFFNO, String surname, String otherNames, String dept, String tel, String email,
			String course, String offeringType, int acadYear, String semester, String sakaiSiteId) {
		STAFFNO = sTAFFNO;
		this.surname = surname;
		this.otherNames = otherNames;
		this.dept = dept;
		this.tel = tel;
		this.email = email;
		this.course = course;
		this.offeringType = offeringType;
		this.acadYear = acadYear;
		this.semester = semester;
		this.sakaiSiteId = sakaiSiteId;
	}
}
