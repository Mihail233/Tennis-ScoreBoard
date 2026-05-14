package org.example.tennisscoreboard.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.tennisscoreboard.dto.ErrorResponseDTO;
import org.example.tennisscoreboard.exception.TestException;
import org.example.tennisscoreboard.util.ServletUtil;

import java.io.IOException;

@WebFilter("/*")
public class ExceptionHandlingFilter extends HttpFilter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)  throws IOException {
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            filterChain.doFilter(request, resp);
        } catch (TestException e) {
            sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown error", resp);
        } catch (Exception e) {
            sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unknown error", resp);
        }
    }

    private void sendError(int code, String message, HttpServletResponse response) throws IOException {
        ErrorResponseDTO errorEntity = new ErrorResponseDTO(message);
        ServletUtil.sendResponse(code, errorEntity, response);
    }
}
