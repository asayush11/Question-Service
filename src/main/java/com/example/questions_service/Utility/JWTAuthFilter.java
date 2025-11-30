package com.example.questions_service.Utility;
import com.example.questions_service.Service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(JWTAuthFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        logger.info("Filter: Processing incoming request for authentication");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI();

        List<String> publicEndpoints = Arrays.asList("/actuator", "/users/login", "/users/create", "/users/change-password");
        List<String> adminEndpoints = Arrays.asList("/question", "/notes/addNote", "/notes/updateNote", "/notes/deleteNote");

        if (publicEndpoints.stream().anyMatch(path::startsWith) || request.getMethod().equalsIgnoreCase("OPTIONS")) {
            chain.doFilter(req, res);
            logger.info("Filter: Public endpoint accessed, proceeding without authentication");
            return;
        }

        // Secure endpoints
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login to proceed");
            logger.error("Filter: Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();

        try {
            var result = jwtUtil.validateToken(token);
            if (result.isExpired()) {
                logger.info("Filter: Access token expired, attempting to refresh");
                var newToken = userHelper.updateToken(token, result.email());
                if (newToken != null) {
                    // Send the new token in response header
                    logger.info("Filter: Access token refreshed successfully");
                    response.setHeader("X-New-Access-Token", newToken);
                    if (adminEndpoints.stream().anyMatch(path::startsWith)) {
                        logger.info("Filter: Validating admin privileges for endpoint");
                        boolean isAdmin = userHelper.validateAdmin(token);
                        if (!isAdmin) {
                            logger.error("Filter: Unauthorized access attempt to admin endpoint");
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You're not allowed to perform this operation");
                            return;
                        }
                    }
                    chain.doFilter(req, res);
                } else {
                    logger.error("Filter: Refresh token expired or invalid, cannot refresh access token");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session expired. Please login again.");
                }
            } else {
                // Token valid
                if (adminEndpoints.stream().anyMatch(path::startsWith)) {
                    logger.info("Filter: Validating admin privileges for endpoint");
                    boolean isAdmin = userHelper.validateAdmin(token);
                    if (!isAdmin) {
                        logger.error("Filter: Unauthorized access attempt to admin endpoint");
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "You're not allowed to perform this operation");
                        return;
                    }
                }
                chain.doFilter(req, res);
            }
        } catch (Exception e) {
            logger.error("Filter: Token validation failed: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login to proceed");
        }


    }
}