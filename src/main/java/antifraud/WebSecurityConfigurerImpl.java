package antifraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;

@EnableWebSecurity
public class WebSecurityConfigurerImpl extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    public void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint) // Handles auth error
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .authorizeRequests() // manage access
                .antMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/auth/user/**").hasRole(User.ADMINISTRATOR)
                .antMatchers(HttpMethod.GET, "/api/auth/list").hasAnyRole(User.ADMINISTRATOR, User.SUPPORT)
                .antMatchers(HttpMethod.PUT, "/api/auth/role/**").hasRole(User.ADMINISTRATOR)
                .mvcMatchers(HttpMethod.PUT, "/api/antifraud/transaction/**").hasRole(User.SUPPORT)
                .mvcMatchers(HttpMethod.POST, "/api/antifraud/transaction/**").hasRole(User.MERCHANT)
                .antMatchers("/api/antifraud/suspicious-ip/**").hasRole(User.SUPPORT)
                .antMatchers("/api/antifraud/stolencard/**").hasRole(User.SUPPORT)
                .antMatchers(HttpMethod.GET, "/api/antifraud/history/**").hasRole(User.SUPPORT)
                .antMatchers(HttpMethod.PUT, "/api/auth/access/**").hasRole(User.ADMINISTRATOR)
                .antMatchers("/actuator/shutdown").permitAll() // needs to run test
                // other matchers
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
    }
}