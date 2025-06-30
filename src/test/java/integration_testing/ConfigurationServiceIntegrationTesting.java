package integration_testing;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest(classes = ConfigurationServiceIntegrationTesting.class)
@Transactional
public class ConfigurationServiceIntegrationTesting {


}