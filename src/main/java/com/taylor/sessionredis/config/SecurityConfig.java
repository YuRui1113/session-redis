/*
 * File: src\main\java\com\taylor\sessionredis\config\SecurityConfig.java
 * Project: jpa
 * Created Date: Wednesday, December 6th 2023, 5:44:07 pm
 * Author: Rui Yu (yurui_113@hotmail.com)
 * -----
 * Last Modified: Wednesday, 6th December 2023 5:46:17 pm
 * Modified By: Rui Yu (yurui_113@hotmail.com>)
 * -----
 * Copyright (c) 2023 Rui Yu
 * -----
 * HISTORY:
 * Date                     	By       	Comments
 * -------------------------	---------	----------------------------------------------------------
 * Wednesday, December 6th 2023	Rui Yu		Initial version
 */

package com.taylor.sessionredis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService users(PasswordEncoder passwordEncoder) {
        UserDetails user = User.withUsername("user")
                .password("$2a$10$lDUTwo3pS6bt7Iwv4oVmPuM3hfYNKdddurzv4xBpvzk31RS7LfS72")
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password("$2a$10$lDUTwo3pS6bt7Iwv4oVmPuM3hfYNKdddurzv4xBpvzk31RS7LfS72")
                .roles("ADMIN", "USER")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                authorize -> authorize
                        .requestMatchers("api/v1/session/**").hasRole("ADMIN")
                        .anyRequest()
                        .authenticated())
                // HTTP Basic are stateless and won't store authentication token in session
                // Below code will make authentication token to be stored in Redis
                .httpBasic((basic) -> basic
                        .addObjectPostProcessor(new ObjectPostProcessor<BasicAuthenticationFilter>() {
                            @Override
                            public <O extends BasicAuthenticationFilter> O postProcess(O filter) {
                                filter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());
                                return filter;
                            }
                        }))
                // Avoid 401 issue for HTTP post and put method
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}