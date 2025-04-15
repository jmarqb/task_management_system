package com.jmarqb.config.server;

import org.springframework.boot.builder.SpringApplicationBuilder;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

class ConfigServerExtension implements BeforeAllCallback {

	static final int CONFIG_PORT = 9090;

	@Override
	public void beforeAll(ExtensionContext context) {
		new SpringApplicationBuilder(ConfigServerApplication.class)
			.run("--server.port=" + CONFIG_PORT,
				"--spring.cloud.config.server.native.search-locations=classpath:/config-repo");
	}
}
