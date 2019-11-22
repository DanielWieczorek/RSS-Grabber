package de.wieczorek.chart.core.ui;

import de.wieczorek.chart.core.business.Controller;
import de.wieczorek.rss.core.persistence.EntityManagerContext;
import de.wieczorek.rss.core.ui.Resource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

@Resource
@ApplicationScoped
@EntityManagerContext
@Path("/")
public class AuthenticationResource {

    @Inject
    private Controller controller;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("login")
    public String login(@Context HttpHeaders headers) throws NoSuchAlgorithmException {
        List<String> authHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || authHeader.isEmpty()) {
            throw new BadRequestException("Missing authentication data");
        }

        String[] authData = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0).split(" ");

        if (authData.length < 2) {
            throw new BadRequestException("Missing authentication data");
        }

        String authType = authData[0];

        if (authType.equals("Basic")) {
            String authString = authData[1];
            authString = new String(Base64.getDecoder().decode(authString), Charset.forName("ascii"));
            String[] credentials = authString.split(Pattern.quote(":"));

            if (credentials.length == 2) {
                return controller.login(credentials[0], credentials[1]);
            }

            throw new BadRequestException("Invalid authentication data");
        }
        throw new BadRequestException("Invalid authentication type");
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("logout")
    public void logout(SessionInfo info) {
        controller.logout(info.getUsername(), info.getToken());
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("validate")
    public String isSessionValid(SessionInfo info) {
        return controller.isSessionValid(info.getUsername(), info.getToken()) ? "true" : "false";
    }

}
