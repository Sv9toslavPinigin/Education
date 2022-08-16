package net.thumbtack.buscompany.config;

import lombok.AllArgsConstructor;
import net.thumbtack.buscompany.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity(debug = false)
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private UserService userService;
    private CustomAuthenticationSuccessHandler successHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter authenticationFilter = new CustomAuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManager());
        authenticationFilter.setAuthenticationSuccessHandler(successHandler);
        authenticationFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler());

        http
                .csrf().disable()
                .logout().disable()
                .formLogin().disable()
                .addFilterAt(
                        authenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                .and()
                .exceptionHandling().authenticationEntryPoint(customAuthenticationExceptionHandler())
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/sessions").permitAll()
                .antMatchers(HttpMethod.POST, "/api/admins").permitAll()
                .antMatchers(HttpMethod.POST, "/api/clients").permitAll()
                .antMatchers(HttpMethod.GET, "/api/settings").permitAll()
                .antMatchers(HttpMethod.GET, "/api/clients").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/admins").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/clients").hasRole("CLIENT")
                .antMatchers(HttpMethod.GET, "/api/buses").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/trips").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/trips/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/trips/**").hasRole("ADMIN")
                .antMatchers("/api/**").authenticated()
                .and()
                .sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true)
                .and()
                .sessionAuthenticationFailureHandler(customAuthenticationFailureHandler());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userService);
        return authenticationProvider;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public CustomAuthenticationExceptionHandler customAuthenticationExceptionHandler() {
        return new CustomAuthenticationExceptionHandler();
    }

    @Bean
    public CustomAuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }


}

