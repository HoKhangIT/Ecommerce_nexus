package com.nexus.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = {
		"jwt.secret=VGhpc0lzQVRlc3RPbmx5SldUU2VjcmV0S2V5VGhhdElzQXRMZWFzdDMyQnl0ZXM="
})
class EcommerceApplicationTests {

	@Test
	void contextLoads() {
	}

}
