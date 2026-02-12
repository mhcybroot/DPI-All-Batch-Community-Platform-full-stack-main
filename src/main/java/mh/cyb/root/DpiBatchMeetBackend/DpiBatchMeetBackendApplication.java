package mh.cyb.root.DpiBatchMeetBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.data.jpa.repository.config.EnableJpaAuditing
public class DpiBatchMeetBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DpiBatchMeetBackendApplication.class, args);
	}

}
