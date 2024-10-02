package io.github.augustolba.quarkussocial.rest.dto;

import lombok.Data;

import java.util.List;
@Data
public class FollowersPerUserResponse {

    private Integer count;

    private List<FollowerResponse> content;
}
