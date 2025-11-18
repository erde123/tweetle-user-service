package nl.fontys.tweetleuserservice.domain;

import nl.fontys.tweetleuserservice.persistence.entity.UserEntity;

/**
 * Result wrapper for user synchronization, indicating whether creation happened.
 */
public record UserSyncResult(UserEntity user, boolean newlyCreated) {
}

