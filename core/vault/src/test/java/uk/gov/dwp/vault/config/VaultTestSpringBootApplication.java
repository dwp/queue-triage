package uk.gov.dwp.vault.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "uk.gov.dwp.vault")
@SpringBootApplication
public class VaultTestSpringBootApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(VaultTestSpringBootApplication.class).run(args);
    }

}
