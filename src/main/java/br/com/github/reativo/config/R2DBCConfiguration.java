package br.com.github.reativo.config;

import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories
public class R2DBCConfiguration extends AbstractR2dbcConfiguration {

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        
        final String DROP_TABLE = "DROP TABLE IF EXISTS AGENDA; ";
        final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS AGENDA"
                + "(ID INT PRIMARY KEY AUTO_INCREMENT,"
                + " NOME VARCHAR(100),"
                + " EMAIL VARCHAR(100)); ";
        
        H2ConnectionFactory h2ConnectionFactory = new H2ConnectionFactory(H2ConnectionConfiguration.builder()
                .url("file:./agendaDB;DB_CLOSE_DELAY=-1;")
                .username("sa")
                .build());
        
        h2ConnectionFactory.create()
                .flatMap(con -> { 
                    return con.createBatch()
                            .add(DROP_TABLE)
                            .add(CREATE_TABLE)
                            .execute()
                            .then(con.close());
                })
                .log()
                .subscribe();
        return h2ConnectionFactory;
    }
    
}
