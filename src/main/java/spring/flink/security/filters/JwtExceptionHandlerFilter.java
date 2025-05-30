package spring.flink.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import spring.flink.apiPayload.ApiResponse;
import spring.flink.apiPayload.exception.GeneralException;

import java.io.IOException;

@Component
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch(GeneralException ex) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, request, response, ex);
        }


    }

    public void setErrorResponse(HttpStatus status, HttpServletRequest req,
                                 HttpServletResponse res, Throwable ex) throws IOException {
        res.setStatus(status.value());
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        ApiResponse<?> apiResponse = ApiResponse.onFailure(HttpStatus.UNAUTHORIZED.name(), "COMMON401", ex.getMessage());
        res.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
    }

}
