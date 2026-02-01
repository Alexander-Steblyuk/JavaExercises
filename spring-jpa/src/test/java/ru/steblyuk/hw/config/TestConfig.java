package ru.steblyuk.hw.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.event.AfterTestExecutionEvent;
import org.springframework.test.context.event.annotation.AfterTestExecution;

import java.lang.annotation.Annotation;

import static java.util.Objects.nonNull;

@Configuration
public class TestConfig {

    @Autowired
    private Flyway flyway;

    @AfterTestExecution
    public void afterTestExecution(AfterTestExecutionEvent event) {
        TestContext context = event.getTestContext();
        Annotation classAnnotation = context.getTestClass()
                .getAnnotation(RefreshDb.class);
        Annotation methodAnnotation = context.getTestMethod()
                .getAnnotation(RefreshDb.class);

        boolean isPermitted = nonNull(classAnnotation) || nonNull(methodAnnotation);
        if (isPermitted) {
            flyway.clean();
            flyway.migrate();
        }
    }
}
