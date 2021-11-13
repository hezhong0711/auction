package com.tw.auction.extension;

import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import ch.vorburger.mariadb4j.springframework.MariaDB4jSpringService;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class MariaDB4jExtension implements BeforeAllCallback, AfterAllCallback {
    private static Logger log = LoggerFactory.getLogger(MariaDB4jExtension.class);

    private static final int DEFAULT_PORT = 60000;
    private static MariaDB4jSpringService _mariaDB4jSpringService;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (Objects.nonNull(_mariaDB4jSpringService)) {
            return;
        }

        MariaDB4jSpringService mariaDB4jSpringService = new MariaDB4jSpringService();
        DBConfigurationBuilder dBConfigurationBuilder = mariaDB4jSpringService.getConfiguration();
        dBConfigurationBuilder.addArg("--character-set-server=utf8");
        dBConfigurationBuilder.addArg("--lower_case_table_names=1");
        dBConfigurationBuilder.addArg("--collation-server=utf8_general_ci");
        dBConfigurationBuilder.addArg("--max-connections=1024");
        dBConfigurationBuilder.addArg("--user=root");

        mariaDB4jSpringService.setDefaultPort(DEFAULT_PORT);

        try {
            mariaDB4jSpringService.start();
            mariaDB4jSpringService.getDB().createDB("tw_auction");
            _mariaDB4jSpringService = mariaDB4jSpringService;
            log.info("started new mariadb4j instance ");
        } catch (Exception e) {
            log.error("failed to start mariadb4j", e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                _mariaDB4jSpringService.stop();
            } catch (Exception e) {
                log.error("failed to stop mariadb4j", e);
            }
        }));
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
    }
}
