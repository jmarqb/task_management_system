package com.jmarqb.ms.project.core.infrastructure.adapters.output.external.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;

@Configuration
public class FeignClientConfiguration {

	@Bean
	RequestInterceptor requestInterceptor() {
		return requestTemplate -> {
			var requestAttributes = RequestContextHolder.getRequestAttributes();

			if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
				var request = servletRequestAttributes.getRequest();
				String authorizationHeader = request.getHeader("Authorization");

				if (authorizationHeader != null) {
					requestTemplate.header("Authorization", authorizationHeader);
				}
			}
		};
	}
}
