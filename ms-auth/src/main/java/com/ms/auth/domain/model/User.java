package com.ms.auth.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {

	private Long id;

	private String firstName;

	private String lastName;

	private String email;

	private String password;

	private String phone;

	private int age;

	private String gender;

	private boolean deleted;

	private LocalDateTime deletedAt;

	private String country;

	private List<Role> roles;
}
