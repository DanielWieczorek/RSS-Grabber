package de.wieczorek.rss.trading.business;


import de.wieczorek.core.config.ServiceName;
import io.jenetics.IntegerGene;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.IO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@ApplicationScoped
public class EvolutionProgressDao {
    private static final Logger logger = LoggerFactory.getLogger(EvolutionProgressDao.class);


    private static final String FILE_NAME = "evolution-result";

    @Inject
    @ServiceName
    private String rootDirectory;

    public EvolutionResult<IntegerGene, Double> read() {
        File file = new File(buildPath());
        file.getParentFile().mkdirs();

        if (file.exists()) {
            try {
                return (EvolutionResult<IntegerGene, Double>) IO.object.read(file);
            } catch (IOException e) {
                logger.error("error reading from evolution result file", e);
                throw new RuntimeException(e);
            }
        } else {
            logger.info("evolution result file could not be found");
            return null;
        }
    }

    public void write(EvolutionResult<IntegerGene, Double> result) {
        File file = new File(buildPath());

        try {
            IO.object.write(result, file);
        } catch (IOException e) {
            logger.error("error writing to evolution result file", e);
            throw new RuntimeException(e);
        }
    }

    private String buildPath() {
        return Paths.get(System.getProperty("user.home"), "neural-networks", rootDirectory, FILE_NAME).toString();
    }

    public void delete() {
        new File(buildPath()).delete();
    }
}
