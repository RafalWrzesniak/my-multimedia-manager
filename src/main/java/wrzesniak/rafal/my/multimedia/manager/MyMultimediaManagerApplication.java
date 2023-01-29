package wrzesniak.rafal.my.multimedia.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Slf4j
@EnableAspectJAutoProxy
@SpringBootApplication
public class MyMultimediaManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyMultimediaManagerApplication.class, args);
	}

}
