package com.example.questions_service.Utility;
import com.example.questions_service.Service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


import jakarta.servlet.Filter;

@Component
public class JWTAuthFilter implements Filter {

    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private UserHelper userHelper;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI();

        // Allow public endpoints
        List<String> publicEndpoints = Arrays.asList("/users", "/actuator");
        if (publicEndpoints.stream().anyMatch(path::startsWith) || request.getMethod().equalsIgnoreCase("OPTIONS")) {
            chain.doFilter(req, res);
            return;
        }

        // Secure endpoints
        if (path.startsWith("/quiz") || path.startsWith("/question")) {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login to proceed");
                return;
            }

            String token = authHeader.substring("Bearer ".length()).trim();

            try {
                var result = jwtUtil.validateToken(token);
                if (result.isExpired()) {
                    var newToken = userHelper.updateToken(token, result.email());
                    if (newToken != null) {
                        // Send the new token in response header
                        response.setHeader("X-New-Access-Token", newToken);
                        if(path.startsWith("/question")) {
                            boolean isAdmin = userHelper.validateAdmin(token);
                            if (!isAdmin) {
                                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You're not allowed to perform this operation");
                                return;
                            }
                        }
                        chain.doFilter(req, res);
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session expired. Please login again.");
                    }
                } else {
                    // Token valid
                    if(path.startsWith("/question")) {
                        boolean isAdmin = userHelper.validateAdmin(token);
                        if (!isAdmin) {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You're not allowed to perform this operation");
                            return;
                        }
                    }
                    chain.doFilter(req, res);
                }
                return;
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login to proceed");
                return;
            }
        }

        chain.doFilter(req, res);
    }
}