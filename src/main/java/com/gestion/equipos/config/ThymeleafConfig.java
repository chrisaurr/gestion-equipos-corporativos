package com.gestion.equipos.config;

import com.gestion.equipos.util.ThymeleafUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

import java.util.Collections;
import java.util.Set;

@Configuration
public class ThymeleafConfig {

    @Bean
    public ThymeleafUtilsDialect thymeleafUtilsDialect() {
        return new ThymeleafUtilsDialect();
    }

    public static class ThymeleafUtilsDialect extends AbstractDialect implements IExpressionObjectDialect {

        public ThymeleafUtilsDialect() {
            super("thymeleafutils");
        }

        @Override
        public IExpressionObjectFactory getExpressionObjectFactory() {
            return new IExpressionObjectFactory() {
                @Override
                public Set<String> getAllExpressionObjectNames() {
                    return Collections.singleton("dateUtils");
                }

                @Override
                public Object buildObject(IExpressionContext context, String expressionObjectName) {
                    if ("dateUtils".equals(expressionObjectName)) {
                        return new ThymeleafUtils();
                    }
                    return null;
                }

                @Override
                public boolean isCacheable(String expressionObjectName) {
                    return true;
                }
            };
        }
    }
}