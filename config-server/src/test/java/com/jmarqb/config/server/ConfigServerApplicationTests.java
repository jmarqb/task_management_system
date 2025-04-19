package com.jmarqb.config.server;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ConfigServerApplicationTests {

	@Test
	void contextLoads() {
		assertThat(ConfigServerApplication.class).isNotNull();
	}

	@Test
	void applicationStarts() {
		ConfigServerApplication.main(new String[]{});
		assertThat(ConfigServerApplication.class).isNotNull();
	}

}
