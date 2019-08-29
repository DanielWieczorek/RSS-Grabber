package de.wieczorek.rss.core.db.migration;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.flywaydb.core.Flyway;

@ApplicationScoped
public class DatabaseMigrator {

    @Inject
    @MigrationConfiguration
    private Instance<Map<String, String>> configuration;

    public void migrate() {
        if (configuration.isAmbiguous()) {
            throw new RuntimeException("multiple migration configurations");
        }

        if (!configuration.isUnsatisfied()) {
            Map<String, String> config = configuration.get();
            Flyway flyway = Flyway.configure().configuration(config).load();
            try {
                flyway.migrate();
            } catch (Exception e) {
                e.printStackTrace(); // TODO
            }
        }
    }
}
