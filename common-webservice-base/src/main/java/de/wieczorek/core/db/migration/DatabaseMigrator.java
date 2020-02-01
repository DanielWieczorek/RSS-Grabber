package de.wieczorek.core.db.migration;

import org.flywaydb.core.Flyway;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Map;

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
