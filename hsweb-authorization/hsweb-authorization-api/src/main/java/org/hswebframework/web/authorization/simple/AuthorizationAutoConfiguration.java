package org.hswebframework.web.authorization.simple;

import org.hswebframework.web.authorization.Authentication;
import org.hswebframework.web.authorization.builder.AuthenticationBuilderFactory;
import org.hswebframework.web.authorization.builder.DataAccessConfigBuilderFactory;
import org.hswebframework.web.authorization.simple.builder.DataAccessConfigConvert;
import org.hswebframework.web.authorization.simple.builder.SimpleAuthenticationBuilderFactory;
import org.hswebframework.web.authorization.simple.builder.SimpleDataAccessConfigBuilderFactory;
import org.hswebframework.web.convert.CustomMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * TODO 完成注释
 *
 * @author zhouhao
 */
@Configuration
public class AuthorizationAutoConfiguration {

    @Autowired(required = false)
    private List<DataAccessConfigConvert> dataAccessConfigConverts;

    @Bean
    @ConditionalOnMissingBean(DataAccessConfigBuilderFactory.class)
    @ConfigurationProperties(prefix = "hsweb.authorization.data-access", ignoreInvalidFields = true)
    public SimpleDataAccessConfigBuilderFactory dataAccessConfigBuilderFactory() {
        SimpleDataAccessConfigBuilderFactory factory = new SimpleDataAccessConfigBuilderFactory();
        if (null != dataAccessConfigConverts) {
            dataAccessConfigConverts.forEach(factory::addConvert);
        }
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationBuilderFactory.class)
    public AuthenticationBuilderFactory authenticationBuilderFactory(DataAccessConfigBuilderFactory dataAccessConfigBuilderFactory) {
        return new SimpleAuthenticationBuilderFactory(dataAccessConfigBuilderFactory);
    }

    @Bean
    public CustomMessageConverter authenticationCustomMessageConverter(AuthenticationBuilderFactory factory) {
        return new CustomMessageConverter() {
            @Override
            public boolean support(Class clazz) {
                return clazz == Authentication.class;
            }

            @Override
            public Object convert(Class clazz, byte[] message) {
                String json = new String(message);

                return factory.create().json(json).build();
            }
        };
    }
}
