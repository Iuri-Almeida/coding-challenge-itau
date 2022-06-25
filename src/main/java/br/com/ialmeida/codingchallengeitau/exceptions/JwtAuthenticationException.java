package br.com.ialmeida.codingchallengeitau.exceptions;

public class JwtAuthenticationException extends RuntimeException {

    public JwtAuthenticationException(String msg) {
        super(msg);
    }

}
