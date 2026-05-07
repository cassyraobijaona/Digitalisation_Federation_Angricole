package mg.hei.federation_agricole.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String VALID_API_KEY = System.getenv("API_KEY") != null
            ? System.getenv("API_KEY")
            : "agri-secure-key"; // ✅ valeur par défaut si .env non chargé

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = request.getHeader("x-api-key");

        if (apiKey == null || !apiKey.equals(VALID_API_KEY)) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("Bad credentials");
            return;
        }

        filterChain.doFilter(request, response);
    }
}