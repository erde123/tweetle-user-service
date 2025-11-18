package nl.fontys.tweetleuserservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private Long userId;
    private String username;
    private String avatarUrl;
}
