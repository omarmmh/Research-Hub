package com.webapp.researchhub.config;

import com.webapp.researchhub.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()

                /* Permit All Users to these requests */
                .requestMatchers("/").permitAll()
                .requestMatchers("/site/**").permitAll()
                .requestMatchers("/demo/**").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/js/**").permitAll()
                .requestMatchers("/images/**").permitAll()
                .requestMatchers("/packages/**").permitAll()
                .requestMatchers("/password/**").permitAll()
                .requestMatchers("/search/**").permitAll()
                .requestMatchers("/profile/**").permitAll()
                .requestMatchers("/papers/**").permitAll()
                .requestMatchers("/demo").permitAll()
                .requestMatchers("/account-creation").anonymous()
                .requestMatchers("/upload/**").hasRole("USER")
                .requestMatchers("/events/**").hasRole("USER")
                .requestMatchers("/forum/**").hasRole("USER")
                .anyRequest()
                .authenticated()
                .and()

                /* Setup Login */
                .formLogin()
                .loginPage("/login-user")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/login-success", true)
                .failureUrl("/login-user?error=true")
                .permitAll()
                .and()

                /* Setup Logout */
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll();

        return http.build();
    }

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
