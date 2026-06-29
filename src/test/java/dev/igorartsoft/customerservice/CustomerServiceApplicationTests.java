package dev.igorartsoft.customerservice;

// import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
// @Disabled("Disabled until test MongoDB configuration is added")
@ActiveProfiles("test")
class CustomerServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
