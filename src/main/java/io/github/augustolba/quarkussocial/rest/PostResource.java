package io.github.augustolba.quarkussocial.rest;

import io.github.augustolba.quarkussocial.domain.model.Post;
import io.github.augustolba.quarkussocial.domain.model.User;
import io.github.augustolba.quarkussocial.domain.repository.PostRepository;
import io.github.augustolba.quarkussocial.domain.repository.UserRepository;
import io.github.augustolba.quarkussocial.rest.dto.CreatePostRequest;
import io.github.augustolba.quarkussocial.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.annotations.Pos;

import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class PostResource {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Inject
    public PostResource(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest postRequest){
        User user = userRepository.findById(userId);
        if(user != null){

            Post post = new Post();
            post.setText(postRequest.getText());
            post.setUser(user);

            postRepository.persist(post);

            return Response.status(Response.Status.CREATED).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    @GET
    public Response listPosts(@PathParam("userId") Long userId){
        User user = userRepository.findById(userId);
        if(user != null){
           var query = postRepository.find("user",
                   Sort.by("dateTime", Sort.Direction.Descending),user);
           var list = query.list();

           var postResponseList = list.stream()
//                   .map(post -> PostResponse.fromEntity(post))
                   .map(PostResponse::fromEntity)
                   .collect(Collectors.toList());
            return Response.ok(postResponseList).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
