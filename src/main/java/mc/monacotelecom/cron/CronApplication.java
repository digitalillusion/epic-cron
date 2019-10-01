package mc.monacotelecom.cron;

import mc.monacotelecom.cron.service.AddressDatabaseSyncService;
import mc.monacotelecom.cron.service.UppercaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;
import java.util.function.Supplier;

@SpringBootApplication
public class CronApplication {

	@Autowired
	AddressDatabaseSyncService addressDatabaseSyncService;

	@Autowired
	UppercaseService uppercaseService;

	@Bean
	public Supplier<String> addressDatabaseSync() {
		return addressDatabaseSyncService::get;
	}

	@Bean
	public Function<String, String> uppercase() {
		return uppercaseService::apply;
	}

	public static void main(String[] args) {
		SpringApplication.run(CronApplication.class, args);
	}
}
