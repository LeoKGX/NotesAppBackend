package com.leokgx.NotesAppBackend.jwt;

import com.leokgx.NotesAppBackend.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired private JwtTokenUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!hasAuthHeader(request)){
            filterChain.doFilter(request, response);
            return;
        }
        String accessToken = getAccessToken(request);

        if (!jwtUtil.validateAccessToken(accessToken)){
            filterChain.doFilter(request, response);
            return;
        }

        setAuthenticationContext(accessToken, request);
        filterChain.doFilter(request, response);
    }

    private boolean hasAuthHeader(HttpServletRequest request){
        String header = request.getHeader("Authorization");
        return !(ObjectUtils.isEmpty(header) || !header.startsWith("Bearer"));
    }

    private String getAccessToken(HttpServletRequest request){
        String header = request.getHeader("Authorization");
        return header.split(" ")[1].trim();
    }

    private void setAuthenticationContext(String accessToken, HttpServletRequest request){
        UserDetails userDetails = getUserDetails(accessToken);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, null);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private UserDetails getUserDetails(String accessToken){
        User userDetails = new User();
        String[] subjectDetails = jwtUtil.getSubject(accessToken).split(",");
        userDetails.setId(Long.parseLong(subjectDetails[0]));
        userDetails.setUsername(subjectDetails[1]);
        return userDetails;
    }
}
