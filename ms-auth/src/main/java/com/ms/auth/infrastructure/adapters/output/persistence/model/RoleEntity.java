package com.ms.auth.infrastructure.adapters.output.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles", indexes = {
	@Index(name = "idx_name", columnList = "name"),
})
public class RoleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "icon")
	private String icon;

	@Column(name = "is_admin")
	private boolean isAdmin;

	@Column(name = "is_default_role")
	private boolean isDefaultRole;

	@Column(name = "deleted")
	private boolean deleted;

	@Column(name = "deleted_at")
	private Date deletedAt;

	@ToString.Exclude
	@ManyToMany(mappedBy = "roles")
	private List<UserEntity> users;
}
