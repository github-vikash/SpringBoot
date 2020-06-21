package com.vikash.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	DataSource dataSource;
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		
		/*
		 * auth.inMemoryAuthentication().withUser("Vikash").password("{noop}Vik@123").
		 * roles("USER").and().withUser("San")
		 * .password("{noop}San@123").roles("Admin");
		 */
		 
		
		  auth.jdbcAuthentication().dataSource(dataSource).
		  usersByUsernameQuery("SELECT username,password,enabled FROM user WHERE username=?"
		  ).
		  authoritiesByUsernameQuery("SELECT username,role FROM user_roles WHERE username=?"
		  );
		 
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/images/**").permitAll().antMatchers("/login*").permitAll()
		.antMatchers("/**").hasAnyRole("ADMIN", "USER").anyRequest().authenticated().and().formLogin()
		.loginPage("/login").usernameParameter("username").passwordParameter("password")
		.loginProcessingUrl("/doLogin").defaultSuccessUrl("/index", true).failureUrl("/accessDenied")
		.permitAll().and().exceptionHandling().accessDeniedPage("/accessDenied").and().logout()
		.logoutUrl("/logout").logoutSuccessUrl("/login").permitAll();
	}
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/images/**").addResourceLocations("/classpath:/static/images/**");
	}

}
