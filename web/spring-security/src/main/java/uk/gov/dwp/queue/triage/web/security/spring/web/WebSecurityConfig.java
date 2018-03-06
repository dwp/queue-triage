package uk.gov.dwp.queue.triage.web.security.spring.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private SecurityConfigurerAdapter<AuthenticationManager, AuthenticationManagerBuilder> securityConfigurerAdapter;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests()
                .anyRequest().fullyAuthenticated()
                .and()
//        httpSecurity
                .formLogin()
                .loginPage("/web/login")
                .permitAll()
                .successHandler(new SimpleUrlAuthenticationSuccessHandler("/web/failed-messages"))
                .and()
//        httpSecurity
                .logout()
                .logoutUrl("/logout")
                .and()
//        httpSecurity
                .csrf()
                .disable()
        ;
    }

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity
                .ignoring()
                .antMatchers("/static/**")
                .antMatchers("/ping")
        ;
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .apply(securityConfigurerAdapter)
        ;
    }
}
