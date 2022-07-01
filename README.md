# Coding Challenge - Itaú


## Description

Backend application to manage movie reviews and ratings. Please see the project [statement][statement] (pt-BR) for details.


## How-To


### Requirements

- [Docker Compose][docker-compose]


### Running

```shell
$ git clone https://github.com/Iuri-Almeida/coding-challenge-itau

$ cd coding-challenge-itau

$ docker compose up
```


## Restore database

Please see the [restore database][restore-database] file for a complete explanation.


## Documentation

Please see the [documentation][documentation] file for a complete documentation of the application.


## First Steps

1. Create a user using the endpoint `/signUp` or use one of the pre-registered users listed [here][pre-registered-users].
2. Log into the application using your `email` and `password` on the `/login` endpoint. Here you will receive a token that will be needed to perform all API actions.
3. Search for your first movie using the `/api/film?token={your_token}&name={movie}` endpoint.
4. Have fun!


## Authors

- [Iuri Almeida][author]


## License

This project is licensed under the terms of the MIT license - please see the [LICENSE][LICENSE] file for details.


<!-- Links -->
[statement]: https://github.com/Iuri-Almeida/coding-challenge-itau/blob/master/docs/STATEMENT.md
[docker-compose]: https://docs.docker.com/compose/install/
[restore-database]: https://github.com/Iuri-Almeida/coding-challenge-itau/blob/master/docs/RESTORE_DB.md
[pre-registered-users]: https://github.com/Iuri-Almeida/coding-challenge-itau/blob/master/docs/RESTORE_DB.md#pre-registered-users
[documentation]: https://github.com/Iuri-Almeida/coding-challenge-itau/blob/master/docs/DOC.md
[author]: https://github.com/Iuri-Almeida/
[LICENSE]: https://github.com/Iuri-Almeida/coding-challenge-itau/blob/master/LICENSE