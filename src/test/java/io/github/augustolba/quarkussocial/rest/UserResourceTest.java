package io.github.augustolba.quarkussocial.rest;

import io.github.augustolba.quarkussocial.rest.dto.CreateUserRequest;
import io.github.augustolba.quarkussocial.rest.error.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @TestHTTPResource("/users")
    URL apiURL;

    @Test
    @DisplayName("Criando usuário com sucesso")
    @Order(1)
    public void createUser(){

        var user = new CreateUserRequest();
        user.setAge(30);
        user.setName("Teste");

      var response =
              given()
                    .contentType(ContentType.JSON)
                    .body(user)
                .when()
                    .post(apiURL)
                .then()
                    .extract().response();

      assertEquals(201, response.statusCode());
      assertNotNull(response.jsonPath().getString("id"));


    }

    @Test
    @DisplayName("Deve retornar erro quando o json não é válido")
    @Order(2)
    public void createUserValidationErrorTest(){

        var user = new CreateUserRequest();
        user.setAge(null);
        user.setName(null);

        var response =
                given()
                        .contentType(ContentType.JSON)
                        .body(user)
                .when()
                        .post(apiURL)
                .then()
                        .extract().response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));

    }
    @Test
    @DisplayName("Deve listar todos os usuários")
    @Order(3)
    public void listAllUserTest(){

        given()
                .contentType(ContentType.JSON)
        .when()
                .get(apiURL)
        .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }
}