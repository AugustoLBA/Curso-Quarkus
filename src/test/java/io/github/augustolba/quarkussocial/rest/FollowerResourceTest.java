package io.github.augustolba.quarkussocial.rest;

import io.github.augustolba.quarkussocial.domain.model.Follower;
import io.github.augustolba.quarkussocial.domain.model.User;
import io.github.augustolba.quarkussocial.domain.repository.FollowerRepository;
import io.github.augustolba.quarkussocial.domain.repository.UserRepository;
import io.github.augustolba.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    Long userId;
    Long followerId;


    @BeforeEach
    @Transactional
    void setUp(){
        //Usuário padrão dos testes
        var user = new User();
        user.setName("Fulano");
        user.setAge(30);
        userRepository.persist(user);
        userId = user.getId();

        // O seguidor
        var follower = new User();
        follower.setName("Cicrano");
        follower.setAge(31);
        userRepository.persist(follower);
        followerId = follower.getId();

        // Cria um follower
        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);
    }

    @Test
    @DisplayName("Deve retornar 409 quando followerId for igual ao ID do usuário\n")
    public void sameUserAsFollowerTest(){

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("You can't follow yourself"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao seguir um usuário quando o ID do usuário não existir\n")
    public void userNotFoundWhenTryingFollowTest(){

        var body = new FollowerRequest();
        body.setFollowerId(userId);
        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", inexistentUserId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Deve seguir o usuário\n")
    public void followUserTest(){

        var body = new FollowerRequest();
        body.setFollowerId(followerId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("Deve retornar 404 quando tentar seguir um usuário que o ID não existir\n")
    public void userNotFoundWhenListingFollowersTest(){
        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Deve listar os seguidores de um usuário\n")
    public void ListFollowersTest(){

        var response = given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .when()
                .get()
                .then()
                .extract().response();

        var followersCount = response.jsonPath().get("count");
        var followersContent = response.jsonPath().getList("content");
        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1, followersCount);
        assertEquals(1,followersContent.size());
    }

    @Test
    @DisplayName("Deve retornar 404 quando tentar parar de seguir um usuário que o ID não existir\n")
    public void userNotFoundWhenWhenUnfollowAUserTest(){
        var inexistentUserId = 999;

        given()
                .pathParam("userId", inexistentUserId)
                .queryParam("followerId", followerId)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Deve parar de seguir um usuário\n")
    public void UnfollowUserTest(){

        given()
                .pathParam("userId", userId)
                .queryParam("followerId", followerId)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
}