/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.security;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Iterables.getFirst;
import java.net.MalformedURLException;
import java.net.URL;
import static java.util.Arrays.asList;
import java.util.Collection;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.config.UiConfiguration;
import org.cmdbuild.fault.FaultEventCollectorService;
import org.cmdbuild.minions.MinionService;
import static org.cmdbuild.service.rest.v2.providers.ExceptionHandlerService.buildResponseMessages;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import static org.springframework.web.cors.CorsConfiguration.ALL;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 *
 *
 * TODO: there is a lot of duplicated configuration code in this class; should
 * refactor to remove duplication.
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private UiConfiguration uiConfiguration;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return (HttpServletRequest request) -> {
            CorsConfiguration configuration = new CorsConfiguration();
            if (uiConfiguration.isCorsEnabled()) {
                logger.debug("enable cors for this request");
                try {
                    List<String> allowedOrigins = uiConfiguration.getCorsAllowedOrigins();
                    if (equal(getFirst(allowedOrigins, null), "auto")) {
                        String origin = request.getHeader("Origin"),
                                referer = request.getHeader("Referer");
                        if (isNotBlank(origin)) {
                            allowedOrigins = singletonList(origin);
                        } else if (isNotBlank(referer)) {
                            try {
                                URL url = new URL(referer);
                                url = new URL(url.getProtocol(), url.getHost(), url.getPort(), "");//TODO improve this
                                allowedOrigins = singletonList(url.toString());
                            } catch (MalformedURLException ex) {
                                throw runtime(ex);
                            }
                        } else {
                            allowedOrigins = list(allowedOrigins).without("auto");
                        }
                    }
                    logger.debug("set cors allowed origins = {}", allowedOrigins);
                    configuration.setAllowedOrigins(allowedOrigins);
                    configuration.setAllowedMethods(asList(ALL));
                    configuration.setAllowedHeaders(asList(ALL));
                    configuration.setAllowCredentials(true);
                } catch (Exception ex) {
                    logger.error(marker(), "error preparing cors config", ex);
                }
            } else {
                logger.debug("cors is not enabled for this request");
            }
            return configuration;
        };
    }

    @Configuration
    @Order(10)
    public static class BootStatusSecurityConfig extends MyWebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            setCommonConfigs(httpSecurity
                    .antMatcher("/services/rest/v*/boot/status**"))
                    .authorizeRequests()
                    .anyRequest().permitAll()
                    .and()
                    .logout().disable()
                    .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint());
        }

    }

    @Configuration
    @Order(20)
    public static class BootSecurityConfig extends MyWebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            setCommonConfigs(httpSecurity
                    .antMatcher("/services/rest/v*/boot/**"))
                    .authorizeRequests()
                    .anyRequest().permitAll()
                    .accessDecisionManager(new UnanimousBased(asList(new AccessDecisionVoter() {
                        @Override
                        public boolean supports(ConfigAttribute attribute) {
                            return true;
                        }

                        @Override
                        public boolean supports(Class clazz) {
                            return true;
                        }

                        @Override
                        public int vote(Authentication authentication, Object object, Collection attributes) {
                            return bootService.isSystemReady() ? ACCESS_DENIED : ACCESS_GRANTED;
                        }

                    })))
                    .and()
                    .logout().disable()
                    .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint());
        }

    }

    @Configuration
    @Order(30)
    public static class WsSecurityConfigForRestSession extends MyWebSecurityConfigurerAdapter {

        @Autowired
        private LenientSessionTokenFilter authenticationFilter;// use lenient filter, so an user can use sessions ws even if it does not have a default group set 

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            setCommonConfigs(httpSecurity
                    .requestMatchers()
                    .antMatchers("/services/rest/v*/sessions/**") //TODO cleanup this code; we actually need to intercept only PUT on session/{mysession} for session update on login, when the session is still invalid, and POST on sessions for session create
                    .antMatchers("/services/custom-login", "/services/custom-login/**")
                    .antMatchers("/services/saml/**")
                    .antMatchers("/services/rest/v*/users/*/password")//password reset
                    .antMatchers("/services/rest/v*/users/*/password/recovery")//password recovery
                    .and())
                    .addFilterBefore(authenticationFilter, BasicAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/services/rest/v*/sessions", "/services/rest/v*/sessions/").permitAll() //rest ws login; FIXME: the double syntax 'sessions','sessions/' is a workaround, maybe there is a better way :/
                    .antMatchers(HttpMethod.GET, "/services/rest/v*/sessions/current").permitAll() //check if session exists
                    .antMatchers(HttpMethod.GET, "/services/rest/v*/sessions/challenge").permitAll() //challenge for challenge-response rsa auth
                    .antMatchers(HttpMethod.PUT, "/services/rest/v*/users/*/password").permitAll()//password reset
                    .antMatchers(HttpMethod.POST, "/services/rest/v*/users/*/password/recovery").permitAll()//password recovery
                    .antMatchers("/services/custom-login", "/services/custom-login/**").permitAll()
                    .antMatchers("/services/saml/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .logout().disable()
                    .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint());
        }

    }

    @Configuration
    @Order(40)
    public static class WsSecurityConfig extends MyWebSecurityConfigurerAdapter {

        @Autowired
        private SessionTokenFilter authenticationFilter;

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            setCommonConfigs(httpSecurity
                    .antMatcher("/services/**"))
                    .addFilterBefore(authenticationFilter, BasicAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/services/rest/v*/sessions", "/services/rest/v*/sessions/").permitAll() // rest ws login; FIXME: the double syntax 'sessions','sessions/' is a workaround, maybe there is a better way :/
                    .antMatchers("/services/rest/v*/configuration/languages", "/services/rest/v*/configuration/languages/").permitAll()
                    .antMatchers("/services/rest/v*/configuration/public", "/services/rest/v*/configuration/public/").permitAll()
                    .antMatchers("/services/rest/v*/resources/company_logo/**").permitAll()//company logo; TODO: replace with public resources ws
                    .antMatchers("/services/rest/v*/**").authenticated() // rest ws 
                    .antMatchers("/services/geoserver/**").authenticated() // geoserver proxy
                    .antMatchers("/services/bimserver/**").authenticated() // bimserver proxy
                    .antMatchers("/services/websocket/v1/main").authenticated() // websocket endpoint
                    .antMatchers("/services/etl/gate/public/**").permitAll()
                    .antMatchers("/services/etl/gate/private/**").authenticated()
                    .antMatchers("/services/soap/Private").permitAll() // soap services
                    .anyRequest().denyAll() //default deny
                    .and()
                    .logout().disable()
                    .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint());
        }
    }

    @Configuration
    @Order(50)
    public static class UiSecurityConfig extends MyWebSecurityConfigurerAdapter {

        @Autowired
        private UiSessionTokenFilter authenticationFilter;

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            setCommonConfigs(httpSecurity)
                    .addFilterBefore(authenticationFilter, BasicAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers("/ui/", "/ui/**", "/ui_dev/**", "/ui_dev/").permitAll() //ui
                    .antMatchers("/", "/index", "/error").permitAll()
                    .anyRequest().denyAll(); //default deny
        }
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
    }

    protected abstract static class MyWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        protected MinionService bootService;
        @Autowired
        protected FaultEventCollectorService errorAndWarningCollectorService;

        protected AuthenticationEntryPoint authenticationEntryPoint() {
            return (request, response, e) -> {

                Map<String, Object> map = map("success", false, "messages", buildResponseMessages(errorAndWarningCollectorService.getCurrentRequestEventCollector().withError(e)));

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(toJson(map));
            };
        }

        protected HttpSecurity setCommonConfigs(HttpSecurity httpSecurity) throws Exception {
            return httpSecurity
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                    .cors().and()
                    .csrf().disable()
                    .headers().frameOptions().sameOrigin().and();
        }

    }

}
