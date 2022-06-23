package br.com.ialmeida.codingchallengeitau;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CodingChallengeItauApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodingChallengeItauApplication.class, args);
    }

}
