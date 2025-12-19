package aniket762.combinehealth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
	public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        logger.info("Application ready to serve on port: {}",
                System.getProperty("server.port","8080"));
	}

}
