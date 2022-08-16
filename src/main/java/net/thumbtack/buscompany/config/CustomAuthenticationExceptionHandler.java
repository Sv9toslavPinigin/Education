package net.thumbtack.buscompany.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.buscompany.dto.response.ErrorDto;
import net.thumbtack.buscompany.dto.response.MyError;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CustomAuthenticationExceptionHandler implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(new ErrorDto(List.of(new MyError(
                "Forbidden",
                "login",
                "Log in or register"
        )))));
    }
}
