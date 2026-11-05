package com.nexus.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@TestPropertySource(properties = "jwt.secret=test-jwt-secret-for-tests")
class EcommerceApplicationTests {

	@Test
	void contextLoads() {
	}

}
