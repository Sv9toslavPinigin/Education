package net.thumbtack.buscompany.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.buscompany.dto.response.ErrorDto;
import net.thumbtack.buscompany.dto.response.MyError;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exc) throws IOException, ServletException {
        ObjectMapper mapper = new ObjectMapper();
        Authentication auth
                = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {

        }
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(mapper.writeValueAsString(new ErrorDto(List.of(new MyError(
                "Access denied",
                "login",
                "Access denied"
        )))));

    }


}