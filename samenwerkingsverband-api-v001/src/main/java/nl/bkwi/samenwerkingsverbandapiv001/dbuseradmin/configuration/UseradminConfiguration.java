package nl.bkwi.samenwerkingsverbandapiv001.dbuseradmin.configuration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Purpose:
 * JPA Configuration for the database useradmin.
 *
 * @Configuration Indicates that a class declares one or more @Bean methods and may be processed by the Spring container
 * to generate bean definitions and service requests for those beans at runtime.
 * @EnableJpaRepositories: Annotation to enable JPA repositories.
 * Will scan the package of the annotated configuration class for Spring Data repositories by default.
 * @EnableTransactionManagement Enables Spring's annotation-driven transaction management capability, similar to the
 * support found in Spring's <tx:*> XML namespace.
 * To be used on @Configuration classes to configure traditional, imperative transaction management or
 * reactive transaction management.
 * @Primary Indicates that a bean should be given preference when multiple candidates are qualified to autowire a
 * single-valued dependency.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        // Configures the name of the EntityManagerFactory bean definition to be used to create repositories discovered through this annotation.
        entityManagerFactoryRef = "useradminEntityManagerFactory",
        // Configures the name of the PlatformTransactionManager bean definition to be used to create repositories discovered through this annotation.
        transactionManagerRef = "useradminTransactionManager",
        // Base packages to scan for annotated components.
        basePackages = {
                "nl.bkwi.samenwerkingsverbandapiv001.dbuseradmin.repositories"
        }
)
public class UseradminConfiguration {

    @Primary
    @Bean(name = "useradminDataSource")
    @ConfigurationProperties(prefix = "application.datasource.useradmin")
    public DataSource useradminDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "useradminEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("useradminDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                // The classes whose packages should be scanned for @Entity annotations.
                .packages("nl.bkwi.samenwerkingsverbandapiv001.dbuseradmin.entities")
                // Below: database as mentioned in application.yml: jdbc:mysql://localhost:3306/useradmin
                .persistenceUnit("useradmin")
                .build();
    }

    @Primary
    @Bean(name = "useradminTransactionManager")
    public PlatformTransactionManager useradminTransactionManager(
            @Qualifier("useradminEntityManagerFactory") EntityManagerFactory
                    useradminEntityManagerFactory
    ) {
        return new JpaTransactionManager(useradminEntityManagerFactory);
    }

}
