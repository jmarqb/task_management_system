package com.ms.auth.infrastructure.adapters.input.rest.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.auth.data.seed.TestDataInitializer;
import com.ms.auth.infrastructure.adapters.input.rest.dtos.request.CreateRoleDto;
import com.ms.auth.domain.model.Error;

import com.ms.auth.infrastructure.security.config.SpringSecurityConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static com.ms.auth.data.Data.createRoleUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SpringSecurityConfig.class, TestDataInitializer.class})
class HandlerExceptionControllerTest {

    @Autowired
    private TestRestTemplate client;

    @LocalServerPort
    private int port;

    @Test
    void handleInvalidTokenException() throws JsonProcessingException {
        String TEST_SECRET = "M1_Testing_secret_ECOMMERCE_SECRET_KEY";
        SecretKey secret = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));

        List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("USER"),
                new SimpleGrantedAuthority("ADMIN"));

        Long userId = 1L;

       Claims claims = Jwts.claims()
                .add("authorities", new ObjectMapper().writeValueAsString(roles))
                .add("username", null)
                .add("id", userId)
                .add("iss", "ms-auth")
                .build();

      String mockToken = Jwts.builder()
                .subject(null)
                .issuer("ms-auth")
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + 1200000))
                .issuedAt(new Date())
                .signWith(secret)
                .compact();


        client.getRestTemplate().getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("Authorization", "Bearer " + mockToken);
            return execution.execute(request, body);
        });

        CreateRoleDto createRoleDto = createRoleUser();
        createRoleDto.setName("USER");
        ResponseEntity<Error> response = client.postForEntity(createURI("/api/roles"), createRoleDto,
                Error.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        Error error = response.getBody();

        assertNotNull(error);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), error.getStatus());
        assertEquals("Unauthorized", error.getError());
        assertEquals("Unauthorized", error.getMessage());
    }

    private String createURI(String uri) {
        return "http://localhost:" + port + uri;
    }
}