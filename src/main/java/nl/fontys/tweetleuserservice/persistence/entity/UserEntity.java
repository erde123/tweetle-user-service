package nl.fontys.tweetleuserservice.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column
    private String auth0Id;

    @NotBlank
    @Column(name= "username")
    private String username;

    @NotBlank
    @Column(name = "email")
    private String email;

    @Column(name = "bio")
    private String bio;

    @Column(name = "profileImageUrl")
    private String profileImageUrl;

    @Column(name = "createdAt")
    private Date createdAt;
}
