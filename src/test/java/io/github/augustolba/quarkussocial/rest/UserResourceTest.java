package io.github.augustolba.quarkussocial.rest;

import io.github.augustolba.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
@QuarkusTest
class UserResourceTest {

    @Test
    @DisplayName("Criando usuário com sucesso")
    public void createUser(){

        var user = new CreateUserRequest();
        user.setAge(30);
        user.setName("Teste");

      var response =
              given()
                    .contentType(ContentType.JSON)
                    .body(user)
                .when()
                    .post("/users")
                .then()
                    .extract().response();
      
      assertEquals(201, response.statusCode());
      assertNotNull(response.jsonPath().getString("id"));


    }

}