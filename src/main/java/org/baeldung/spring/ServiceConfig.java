package main.java.org.baeldung.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({ "main.java.org.baeldung.service,com.roslin.mwicks.spring.narf.service" })
public class ServiceConfig {
}
