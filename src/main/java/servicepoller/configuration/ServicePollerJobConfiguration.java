package servicepoller.configuration;

import java.time.Duration;

// TODO: Service poller configuration can be fetched from DB or text file in server machines.
public class ServicePollerJobConfiguration {
    public static final Integer TIMEOUT = 5000;
    public static final Duration PERIOD = Duration.ofSeconds(10);
}
