package rpp.mf.gov;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.time.Duration;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.jwt.build.Jwt;

@QuarkusTest
public class LoginResourceTest {

  @ParameterizedTest
  @CsvSource({
      "khaled,1994,200",
      "khaled,1997,401" // wrong
  })
  void testLogin(String name, String password, int expectedStatus) {
    String loginBody = "{\"name\":\"" + name + "\",\"password\":\"" + password + "\"}";

    given()
        .contentType(ContentType.JSON)
        .body(loginBody)
        .when()
        .post("/login")
        .then()
        .statusCode(expectedStatus)
        .body(notNullValue())
        .extract()
        .asString();
  }

  @Test
  void testProtectedEndpointWithValidToken() {
    String loginBody = "{\"name\":\"khaled\",\"password\":\"1994\"}";
    var token = given()
        .contentType(ContentType.JSON)
        .body(loginBody)
        .when()
        .post("/login")
        .then()
        .extract()
        .asString();
    given()
        .auth().oauth2(token)
        .when()
        .get("/protected")
        .then()
        .statusCode(200)
        .body(containsString("hello"));
  }

  @Test
  void testProtectedEndpointWithInvalidToken() {
    PrivateKey privateKey = null;
    try {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(2048);
      KeyPair keyPair = keyGen.generateKeyPair();
      privateKey = keyPair.getPrivate();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("error while generating wrong private key");
    }

    // invalid token: signed with wrong key
    String invalidToken = Jwt.issuer("http://localhost:8080")
        .audience("http://localhost:8080")
        .subject("khaled")
        .groups(Set.of("admin", "user"))
        .expiresIn(Duration.ofHours(1))
        .sign(privateKey);
    given()
        .auth().oauth2(invalidToken)
        .when()
        .get("/protected")
        .then()
        .statusCode(401);

  }

  @Test
  void testwithoutToken() {
    given()
        .when()
        .get("/protected")
        .then()
        .statusCode(401);

  }
}
