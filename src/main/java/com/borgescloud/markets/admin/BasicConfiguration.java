package com.borgescloud.markets.admin;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class BasicConfiguration extends WebSecurityConfigurerAdapter {
 
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder encoder = 
          PasswordEncoderFactories.createDelegatingPasswordEncoder();
        auth
          .inMemoryAuthentication()
          .withUser("user@borgescloud.com")
          .password(encoder.encode("user"))
          .roles("USER")
          .and()
          .withUser("admin@borgescloud.com")
          .password(encoder.encode("admin"))
          .roles("USER", "ADMIN");
    }
 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
          .authorizeRequests()
          .antMatchers("/", "/index.html", "/search").permitAll()
          .antMatchers("/webjars/**", "/css/**", "/js/**", "/images/**").permitAll()
          .antMatchers("/management/health", "/management/info").permitAll()
          .anyRequest()
          .authenticated()
          .and()
          .formLogin().loginPage("/login.html").failureUrl("/login-error.html").permitAll()
          .and()
          .logout().invalidateHttpSession(true).deleteCookies("JSESSIONID");
    }
}