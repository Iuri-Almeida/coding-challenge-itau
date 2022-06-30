# Restore Database


## Description

The [dump.sql][dump] file contains some pre-registered users, so there is no need to create an ADVANCED or MODERATOR user.


## How-To

### Requirements

- [Docker][docker]


### Running

```shell
$ docker exec -it ec-postgres /bin/bash

# Inside the container
$ su postgres

$ pg_restore -c -d postgres < /docker-entrypoint-initdb.d/dump.sql
```


## Users

- READER

  ```json
  {
    "email": "letscode@itau.com.br",
    "password": "letscode"
  }
  ```
  ```json
  {
    "email": "reader@itau.com.br",
    "password": "reader"
  }
  ```

- BASIC

  ```json
  {
    "email": "basic@itau.com.br",
    "password": "basic"
  }
  ```

- ADVANCED

  ```json
  {
    "email": "advanced@itau.com.br",
    "password": "advanced"
  }
  ```

- MODERATOR

  ```json
  {
    "email": "moderator@itau.com.br",
    "password": "moderator"
  }
  ```

<!-- Links -->
[dump]: https://github.com/Iuri-Almeida/coding-challenge-itau/blob/master/database/dump.sql
[docker]: https://docs.docker.com/engine/install/
