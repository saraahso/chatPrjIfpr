package br.edu.ifpr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ChatPrjIfprApplication {

	private static final Logger log = LoggerFactory.getLogger(ChatPrjIfprApplication.class);
	
    public static void main(String[] args) throws Throwable {
        ConfigurableApplicationContext ctx = SpringApplication.run(ChatPrjIfprApplication.class, args);
        
        log.info("Contexto: {}", ctx.getApplicationName());
        log.info("H2: {}/{}", ctx.getApplicationName(), "h2-console");
        log.info("Docs api: {}/{}", ctx.getApplicationName(), "swagger-ui.html");
    }
}
