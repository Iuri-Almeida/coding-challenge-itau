package br.com.ialmeida.codingchallengeitau.security.filter.validation;

import br.com.ialmeida.codingchallengeitau.config.PropertiesConfig;
import br.com.ialmeida.codingchallengeitau.exceptions.JwtAuthenticationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JwtValidationFilter extends BasicAuthenticationFilter {

    private final PropertiesConfig propertiesConfig;

    public JwtValidationFilter(AuthenticationManager authenticationManager, PropertiesConfig propertiesConfig) {
        super(authenticationManager);
        this.propertiesConfig = propertiesConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String HEADER_ATTRIBUTE = "token";

        String token = request.getParameter(HEADER_ATTRIBUTE);

        if (token == null) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = this.getAuthenticationToken(token);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthenticationToken(String token) {
        try {
            String username = JWT.require(Algorithm.HMAC512(propertiesConfig.getTokenSecret()))
                    .build()
                    .verify(token)
                    .getSubject();

            return (username == null) ? null : new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
        } catch (SignatureVerificationException e) {
            throw new JwtAuthenticationException("You must use a valid JWT token.");
        }
    }
}
