package br.edu.ifrn.frotaif;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan // registra as classes com @ConfigurationProperties (ex.: ViagemProperties)
public class FrotaifApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrotaifApplication.class, args);
    }
}
