package nl.fontys.tweetleuserservice.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import nl.fontys.tweetleuserservice.domain.RoleEnum;

import java.util.Date;

@Entity
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

    @Column(unique = true, nullable = false)
    private String auth0Id;

    @Column(name= "username", unique = true)
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "bio")
    private String bio;

    @Column(name = "profileimageurl")
    private String profileImageUrl;

    @Column(name = "roles")
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @Column(name = "createdat")
    private Date createdAt;
}
