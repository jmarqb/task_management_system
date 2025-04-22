package com.jmarqb.config.server;

import static com.jmarqb.config.server.ConfigServerExtension.CONFIG_PORT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ConfigServerExtension.class)
@SpringBootTest(properties = {"spring.cloud.config.enabled=true",
	"spring.config.import=configserver:http://localhost:" + CONFIG_PORT + "/config-server"})
class ConfigServerIntegrationTest {

	private @Value("${variable}") String variable;

	@Test
	void configServer() {
		assertThat(variable).isEqualTo("value");
	}
}
