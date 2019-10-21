package de.wieczorek.rss.trading.common.oracle;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.wieczorek.nn.NeuralNetworkName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@ApplicationScoped
public class OracleConfigurationDao {
    private static final Logger logger = LoggerFactory.getLogger(OracleConfigurationDao.class);

    @Inject
    @NeuralNetworkName
    private String fileName;

    public OracleConfiguration read() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(buildPath());

        try {
            return mapper.readValue(file,OracleConfiguration.class);
        } catch (IOException e) {
            logger.error("Could not read file "+file.getAbsolutePath(),e);
            return null;
        }
    }

    public void write(OracleConfiguration configuration) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(buildPath());
        try {
            file.createNewFile();
            mapper.writeValue(file,configuration);
        } catch (IOException e) {
            logger.error("Could not read write value to file "+file.getAbsolutePath(),e);
        }
    }


    private String buildPath(){
        return Paths.get(System.getProperty("user.home"), "neural-networks", fileName).toString();
    }
}
