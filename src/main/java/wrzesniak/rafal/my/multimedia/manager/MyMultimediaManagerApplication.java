package wrzesniak.rafal.my.multimedia.manager;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import javax.sql.DataSource;

@Slf4j
@SpringBootApplication
public class MyMultimediaManagerApplication {

	@Autowired
	DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(MyMultimediaManagerApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initializeDatabaseWithFlyway() {
		log.info("Initializing database with Flyway");
		Flyway flyway = Flyway.configure().dataSource(dataSource).load();
		flyway.migrate();
	}

}
