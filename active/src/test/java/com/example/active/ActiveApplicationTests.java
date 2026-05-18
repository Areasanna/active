package com.example.active;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // Isso faz o Spring buscar especificamente o application-test.properties
class ActiveApplicationTests {

	@Test
	void contextLoads() {
	}
}