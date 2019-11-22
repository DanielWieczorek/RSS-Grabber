package de.wieczorek.chart.core.business;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import de.wieczorek.chart.core.persistence.Credentials;
import de.wieczorek.chart.core.persistence.CredentialsDao;
import de.wieczorek.chart.core.persistence.Session;
import de.wieczorek.chart.core.persistence.SessionDao;
import de.wieczorek.rss.core.timer.RecurrentTaskManager;
import de.wieczorek.rss.core.ui.ControllerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Inject
    private RecurrentTaskManager timer;

    @Inject
    private CredentialsDao dao;

    @Inject
    private SessionDao sessionDao;

    @Override
    public void start() {
        logger.info("started");
        timer.start();
    }

    @Override
    public void stop() {
        logger.info("stopped");
        timer.stop();
    }

    public String login(String username, String password) throws NoSuchAlgorithmException {
        Credentials credentialsFromDB = dao.findByUsername(username);
        if (credentialsFromDB == null) {
            throw new NotAuthorizedException("invalid username or password");
        }

        String passwordHash = generatePasswordHash(password, credentialsFromDB.getSalt(),
                credentialsFromDB.getIterations());

        if (credentialsFromDB.getPasswordHash().equals(passwordHash)) {

            String token = generateToken();
            sessionDao.upsert(buildSession(username, token, LocalDateTime.now()));

            return token;
        } else {
            throw new NotAuthorizedException("invalid username or password");
        }
    }

    private Session buildSession(String username, String token, LocalDateTime now) {
        Session session = new Session();
        session.setUsername(username);
        session.setToken(token);
        session.setExpirationDate(now.plusHours(1));
        return session;
    }

    private String generateToken() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private String generatePasswordHash(String password, String salt, int iterations) {
        HashFunction hashFunction = Hashing.sha512();

        HashCode temp = hashFunction.hashString(password + salt, Charset.forName("ascii"));
        for (int i = 1; i < iterations; i++) {
            temp = hashFunction.hashBytes(temp.asBytes());
        }
        return temp.toString();
    }

    public boolean isSessionValid(String username, String token) {
        Session session = sessionDao.findByUsername(username);
        return session != null //
                && session.getToken().equals(token) //
                && session.getExpirationDate().isAfter(LocalDateTime.now());
    }

    public void logout(String username, String token) {
        Session session = sessionDao.findByUsername(username);

        if (session.getToken().equals(token)) {
            sessionDao.delete(session);
        } else {
            throw new NotAuthorizedException("invalid username or password");
        }

    }

}
