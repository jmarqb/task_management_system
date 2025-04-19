package com.ms.auth.data.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import lombok.Getter;

import com.ms.auth.application.enums.Gender;
import com.ms.auth.infrastructure.adapters.output.persistence.model.RoleEntity;
import com.ms.auth.infrastructure.adapters.output.persistence.model.UserEntity;
import com.ms.auth.infrastructure.adapters.output.persistence.repository.RoleRepository;
import com.ms.auth.infrastructure.adapters.output.persistence.repository.UserRepository;

@Component
@Profile("test")
public class TestDataInitializer implements CommandLineRunner {

	private final PasswordEncoder passwordEncoder;

	private final RoleRepository roleRepository;

	private final UserRepository userRepository;

	@Getter
	private RoleEntity adminRole;

	@Getter
	private RoleEntity userRole;

	public TestDataInitializer(PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRepository userRepository) {
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
	}

	@Override
	public void run(String... args) {
		if (roleRepository.findByName("USER").isEmpty()) {
			userRole = roleRepository.save(
				RoleEntity.builder()
					.name("USER")
					.description("ROLE_USER")
					.isDefaultRole(true)
					.isAdmin(false)
					.deleted(false)
					.build());
		} else {
			userRole = roleRepository.findByName("USER").orElseThrow();
		}

		if (roleRepository.findByName("ADMIN").isEmpty()) {
			adminRole = roleRepository.save(
				RoleEntity.builder()
					.name("ADMIN")
					.description("ROLE_ADMIN")
					.isDefaultRole(false)
					.isAdmin(true)
					.deleted(false)
					.build());
		} else {
			adminRole = roleRepository.findByName("ADMIN").orElseThrow();
		}

		if (userRepository.findByUsername("testadmin@example.com").isEmpty()) {
			UserEntity admin = UserEntity.builder()
				.firstName("firstNameAdmin")
				.lastName("lastNameAdmin")
				.email("testadmin@example.com")
				.age(30)
				.password("password")
				.phone("+1234567890")
				.gender(Gender.MALE)
				.country("Testland")
				.roles(new ArrayList<>()).build();
			admin.setPassword(passwordEncoder.encode(admin.getPassword()));
			admin.getRoles().add(this.getAdminRole());
			admin.getRoles().add(this.getUserRole());
			userRepository.save(admin);
		}
	}
}


