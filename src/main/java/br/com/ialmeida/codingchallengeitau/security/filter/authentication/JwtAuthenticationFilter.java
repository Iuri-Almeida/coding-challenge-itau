package br.com.ialmeida.codingchallengeitau.security.filter.authentication;

import br.com.ialmeida.codingchallengeitau.config.PropertiesConfig;
import br.com.ialmeida.codingchallengeitau.details.UserDetail;
import br.com.ialmeida.codingchallengeitau.entities.User;
import br.com.ialmeida.codingchallengeitau.exceptions.JwtAuthenticationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final PropertiesConfig propertiesConfig;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, PropertiesConfig propertiesConfig) {
        this.authenticationManager = authenticationManager;
        this.propertiesConfig = propertiesConfig;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    user.getPassword(),
                    new ArrayList<>()
            ));
        } catch (IOException | InternalAuthenticationServiceException e) {
            throw new JwtAuthenticationException("Error authenticating user.");
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserDetail userDetail = (UserDetail) authResult.getPrincipal();

        String token = JWT.create()
                .withSubject(userDetail.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + propertiesConfig.getTokenExpirationTime()))
                .sign(Algorithm.HMAC512(propertiesConfig.getTokenSecret()));

        response.getWriter().write(token);
        response.getWriter().flush();
    }
}
