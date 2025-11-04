DELETE FROM users a
USING users b
WHERE a.id < b.id
  AND a.auth0id = b.auth0id;

ALTER TABLE users ADD CONSTRAINT unique_auth0id UNIQUE (auth0id);
