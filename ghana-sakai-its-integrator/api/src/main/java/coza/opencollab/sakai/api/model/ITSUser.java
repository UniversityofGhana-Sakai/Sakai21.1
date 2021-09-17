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
@Table(name = "its_users")
@Data
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@NamedQueries({
		@NamedQuery(name = "FindUsersByAcadYear", query = "SELECT itsu FROM ITSUser itsu WHERE itsu.acadYear = :acadYear") })
public class ITSUser {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;

	@EqualsAndHashCode.Include
	@Column(name = "USER_ID", length = 30, nullable = true)
	private String userId;

	@EqualsAndHashCode.Include
	@Column(name = "FIRST_NAME", length = 45, nullable = false)
	private String firstName;

	@EqualsAndHashCode.Include
	@Column(name = "LAST_NAME", length = 45, nullable = false)
	private String lastName;

	@EqualsAndHashCode.Include
	@Column(name = "EMAIL", length = 45, nullable = false)
	private String email;

	public ITSUser() {
	}

	public ITSUser(String firstName, String lastName, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
}
