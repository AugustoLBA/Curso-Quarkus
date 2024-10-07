package io.github.augustolba.quarkussocial.rest;

import io.github.augustolba.quarkussocial.domain.model.Follower;
import io.github.augustolba.quarkussocial.domain.model.Post;
import io.github.augustolba.quarkussocial.domain.model.User;
import io.github.augustolba.quarkussocial.domain.repository.FollowerRepository;
import io.github.augustolba.quarkussocial.domain.repository.PostRepository;
import io.github.augustolba.quarkussocial.domain.repository.UserRepository;
import io.github.augustolba.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
@QuarkusTest
@TestHTTPEndpoint(PostResource.class)  // Essa anotation já pega a URL da classe sem precisar indicar por parâmetro
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    @Inject
    PostRepository postRepository;
    Long userId;
    Long userNotFollowerId;

    Long userFollowerId;
    @BeforeEach
    @Transactional
    public void setUp(){
        //Usuário padrão dos testes
        var user = new User();
        user.setName("Fulano");
        user.setAge(30);
        userRepository.persist(user);
        userId = user.getId();

        // Criada a postagem para usuário
        Post post = new Post();
        post.setText("Hello");
        post.setUser(user);
        postRepository.persist(post);

        // Usuário que não segue ninguém
        var userNotFollower = new User();
        user.setName("Cicrano");
        user.setAge(33);
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        // Usuário seguidor
        var userFollower = new User();
        userFollower.setName("Beltrano");
        userFollower.setAge(28);
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);


    }


    @Test
    @DisplayName("Deve criar uma postagem para um usuário")
    public void createPostTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", userId)
                .when()
                .post()
                .then().statusCode(201);
    }

    @Test
    @DisplayName("deve retornar 404 ao tentar fazer uma postagem para usuário inexistente")
    public void postForAnInexistentUserTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", inexistentUserId)
                .when()
                .post()
                .then().statusCode(404);
    }

    @Test
    @DisplayName("Deve retornar 404 quando o usuário não existe")
    public void listPostUserNotFoundTest(){

        var inexistentUserId = 999;

        given()
                .pathParam("userId", inexistentUserId)
                .get()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve retornar 404 quando o cabeçalho do ID do seguidor não estiver presente")
    public void listPostUserFollowerHeaderNotSendTest(){
        given()
                .pathParam("userId", userId)
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("You forgot the header followerId"));

    }
    @Test
    @DisplayName("Deve retornar 404 quando o seguidor não existe")
    public void listPostUserFollowerNotFoundTest(){

        var inexistentFollowerId = 999;
        given()
                .pathParam("userId", userId)
                .header("followerId", inexistentFollowerId)
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("Inexistent followerId"));
    }

    @Test
    @DisplayName("Deve retornar 403 quando o seguidor não segue o usuário")
    public void listPostNotAFollowerTest(){
        given()
                .pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
                .when()
                .get()
                .then()
                .statusCode(403)
                .body(Matchers.is("You can't see these posts"));
    }
    @Test
    @DisplayName("Deve retornar os posts do usuário")
    public void listPostTest(){
        given()
                .pathParam("userId", userId)
                .header("followerId", userFollowerId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()",Matchers.is(1));
    }

}