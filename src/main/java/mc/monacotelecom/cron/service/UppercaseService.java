package mc.monacotelecom.cron.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.function.Supplier;

@Service
@Slf4j
public class UppercaseService implements Function<String, String> {
    @Override
    public String apply(String s) {
        String upperCase = s.toUpperCase();
        log.info("[UppercaseService] " + s + " -> " + upperCase);
        return upperCase;
    }
}
