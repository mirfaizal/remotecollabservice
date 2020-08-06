package com.edu.remotecollab.config

import com.amazonaws.HttpMethod
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
class HttpSecurityConfig : WebSecurityConfigurerAdapter() {

    private var getPermissions = "true"

    @Throws(Exception::class)
    public override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .antMatchers("/actuator/health").permitAll()
                .antMatchers("/**").access(getPermissions)
                .anyRequest().denyAll()
                .and().httpBasic()
                .and().csrf().disable()
    }
}