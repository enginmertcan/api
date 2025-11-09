package com.mertcanengin.api.bootstrap;

import com.mertcanengin.api.dto.bootstrap.SampleDataStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SampleDataRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleDataRunner.class);

    private final SampleDataService sampleDataService;
    private final boolean bootstrapOnStartup;

    public SampleDataRunner(SampleDataService sampleDataService,
                            @Value("${app.sample-data.bootstrap-on-startup:true}") boolean bootstrapOnStartup) {
        this.sampleDataService = sampleDataService;
        this.bootstrapOnStartup = bootstrapOnStartup;
    }

    @Override
    public void run(String... args) {
        if (!bootstrapOnStartup) {
            LOGGER.info("Sample data bootstrap on startup disabled");
            return;
        }
        SampleDataStatusResponse response = sampleDataService.bootstrap(false);
        LOGGER.info("Sample data bootstrap result: {}", response.note());
    }
}
