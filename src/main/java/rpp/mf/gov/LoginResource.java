package rpp.mf.gov;

import java.time.Duration;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("login")
public class LoginResource {

    @Inject
    UserRepo repo;

    @ConfigProperty(name = "rpp.token.duration")
    Duration duration;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(LoginRequest request) {
        var optUser = repo.findUserByName(request.name());
        if (optUser.isPresent()) {
            var user = optUser.get();
            var passwordHash = user.getPassword();
            if (BcryptUtil.matches(request.password(), passwordHash)) {
                return Response.ok(generateToken(user)).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

    }

    String generateToken(User user) {
        return Jwt.issuer("http://localhost:8080")
                .audience("http://localhost:8080")
                .subject(user.getName())
                .groups(user.getRoles())
                .expiresIn(duration)
                .sign();
    }
}
