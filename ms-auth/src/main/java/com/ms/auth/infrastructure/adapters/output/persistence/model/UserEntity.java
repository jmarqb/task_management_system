package com.ms.auth.infrastructure.adapters.output.persistence.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;

import com.ms.auth.infrastructure.adapters.vo.Gender;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", indexes = {
	@Index(name = "idx_firstname", columnList = "firstname"),
	@Index(name = "idx_lastname", columnList = "lastname"),
	@Index(name = "idx_email", columnList = "email"),
	@Index(name = "idx_phone", columnList = "phone"),
	@Index(name = "idx_deleted", columnList = "deleted"),
	@Index(name = "idx_gender", columnList = "gender")
})
public class UserEntity implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "firstname", nullable = false)
	private String firstName;

	@Column(name = "lastname")
	private String lastName;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "password")
	@Getter
	private String password;

	@Column(name = "phone", unique = true)
	private String phone;

	@Column(name = "age")
	private int age;

	@Column(name = "gender", nullable = false)
	private Gender gender;

	@Column(name = "deleted", columnDefinition = "boolean default false")
	private boolean deleted;

	@Column(name = "deleted_at")
	private Date deletedAt;

	@Column(name = "country")
	private String country;

	@ManyToMany
	@JoinTable(
		name = "users_roles",
		joinColumns = @JoinColumn(name = "user_id"),
		inverseJoinColumns = @JoinColumn(name = "role_id"),
		uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role_id"})}
	)
	@ToString.Exclude
	private List<RoleEntity> roles;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream()
			.map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
			.toList();
	}

	@Override
	public String getUsername() {
		return this.getEmail();
	}
}
