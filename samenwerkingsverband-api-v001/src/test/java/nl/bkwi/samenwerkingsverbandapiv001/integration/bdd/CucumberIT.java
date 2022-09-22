package nl.bkwi.samenwerkingsverbandapiv001.integration.bdd;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import nl.bkwi.samenwerkingsverbandapiv001.Application;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("bdd")
@RunWith(Cucumber.class)
@CucumberOptions(
  plugin = {"pretty"},
  glue = {"nl.bkwi.samenwerkingsverbandapiv001.integration.bdd"},
  features = {"src/test/resources"})
public class CucumberIT {

}
