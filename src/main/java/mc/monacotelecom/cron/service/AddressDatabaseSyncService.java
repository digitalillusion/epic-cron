package mc.monacotelecom.cron.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;


@Service
@Slf4j
public class AddressDatabaseSyncService implements Supplier<String> {

    public static final String TASK_NAME = "addressDatabaseSync";

    public String get() {
        log.info("Called " + TASK_NAME);
        return "OK";
    }
}
