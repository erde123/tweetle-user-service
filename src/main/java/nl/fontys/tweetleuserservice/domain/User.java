package nl.fontys.tweetleuserservice.domain;

import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String auth0Id;
    private String username;
    private String email;
    private String bio;
    private String profileImageUrl;
    private Date createdAt;
}
