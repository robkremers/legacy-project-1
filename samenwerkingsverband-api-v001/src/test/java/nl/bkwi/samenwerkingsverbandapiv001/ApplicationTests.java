package nl.bkwi.samenwerkingsverbandapiv001;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationTests {

  @Test
  void contextLoads() {
    assertNotNull(this);
  }
}
