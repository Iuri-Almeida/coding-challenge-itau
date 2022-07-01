# Documentation


## Endpoints


### Authentication

* **[POST] - /signUp:** register in the application.

  ```json
  // User object
  {
    "name": "letscode",
    "email": "letscode@itau.com.br",
    "password": "letscode"
  }
  ```

* **[POST] - /login:**  login into the application.

  ```json
  {
    "email": "letscode@itau.com.br",
    "password": "letscode"
  }
  ```


### User

* **[GET] - /api/users?token={user_token}:** list all users.


* **[GET] - /api/users/{user_id}?token={user_token}:** list user with id = {user_id}.


* **[GET] - /api/makeModerator?token={user_token}&userId={user_id}:** make the user with id = {user_id} a moderator.


### Film

* **[GET] - /api/films?token={user_token}:** list all films.


* **[GET] - /api/film?token={user_token}&name={film_name}:** search for a film with name = {film_name}.


### Rating

* **[GET] - /api/rating?token={user_token}&filmId={film_id}&score={score}:** rate the film with id = {film_id} with a score = {score}.


### Comment

* **[GET] - /api/comment?token={user_token}&filmId={film_id}&message={message}:** makes a comment about the film with id = {film_id} with the message = {message}.


* **[GET] - /api/deleteComment?token={user_token}&commentId={comment_id}:** delete the comment with id = {comment_id}.


* **[GET] - /api/setRepeatedComment?token={user_token}&commentId={comment_id}:** set the comment with id = {comment_id} to repeated.


* **[GET] - /api/commentResponse?token={user_token}&commentId={comment_id}&message={message}:** reply to the comment with id = {comment_id} with the message = {message}.


* **[GET] - /api/quoteComment?token={user_token}&filmId={film_id}&commentId={comment_id}&message={message}:** quote the comment with id = {comment_id} with the message = {message} about the film = {film_id}.


### React

* **[GET] - /api/react?token={user_token}&commentId={comment_id}&reaction={reaction}:** reacts to comment with id = {comment_id} positively (true) or negatively (false).

