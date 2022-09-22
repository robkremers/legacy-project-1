package nl.bkwi.samenwerkingsverbandapiv001.dbsuwinetinkijk.configuration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Purpose:
 * JPA Configuration for the database suwinetinkijk.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        // Configures the name of the EntityManagerFactory bean definition to be used to create repositories discovered through this annotation.
        entityManagerFactoryRef = "suwinetinkijkEntityManagerFactory",
        // Configures the name of the PlatformTransactionManager bean definition to be used to create repositories discovered through this annotation.
        transactionManagerRef = "suwinetinkijkTransactionManager",
        // Base packages to scan for annotated components.
        basePackages = {"nl.bkwi.samenwerkingsverbandapiv001.dbsuwinetinkijk.repositories"}
)
public class SuwinetInkijkConfiguration {
    @Bean(name = "suwinetinkijkDataSource")
    @ConfigurationProperties(prefix = "application.datasource.suwinetinkijk")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "suwinetinkijkEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    barEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("suwinetinkijkDataSource") DataSource dataSource
    ) {
        return
                builder
                        .dataSource(dataSource)
                        // The classes whose packages should be scanned for @Entity annotations.
                        .packages("nl.bkwi.samenwerkingsverbandapiv001.dbsuwinetinkijk.entities")
                        // Below: database as mentioned in application.yml: jdbc:mysql://localhost:3306/suwinetinkijk
                        .persistenceUnit("suwinetinkijk")
                        .build();
    }

    @Bean(name = "suwinetinkijkTransactionManager")
    public PlatformTransactionManager suwinetinkijkTransactionManager(
            @Qualifier("suwinetinkijkEntityManagerFactory") EntityManagerFactory
                    suwinetinkijkEntityManagerFactory
    ) {
        return new JpaTransactionManager(suwinetinkijkEntityManagerFactory);
    }
}
