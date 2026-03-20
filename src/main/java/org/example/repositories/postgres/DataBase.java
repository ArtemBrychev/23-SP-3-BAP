package org.example.repositories.postgres;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.configs.EnvConfig;
import org.example.servlets.NewServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {
    private static DataSource instance;

    private static HikariConfig config;
    private static final Logger log = LoggerFactory.getLogger(DataBase.class);

    public DataBase(){}

    public static DataSource getInstance(){
        if(instance == null){
            log.debug("getInstance if(instance==null)");
            synchronized (DataBase.class){
                log.debug("getInstance syncrhonised block");
                if(instance == null){
                    log.debug("getInstance if(instance==null) part 2");
                    config = new HikariConfig();
                    log.debug("Setting DriverClassName: " + org.postgresql.Driver.class.getName());
                    config.setDriverClassName(org.postgresql.Driver.class.getName());
                    String url = EnvConfig.get("DB_URL");
                    log.debug("DB_URL: " + url);
                    config.setJdbcUrl(url);
                    config.setUsername(EnvConfig.get("DB_USER"));
                    config.setPassword(EnvConfig.get("DB_PASSWORD"));
                    try {
                        instance = new HikariDataSource(config);
                        log.debug("Hikari created successfully");
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    }
                    log.debug("Entering createDb method");
                    createDataBase();
                }
            }
        }

        log.debug("Before return statement");
        return instance;
    }

    private static void createDataBase(){
        log.debug("Entered createDb part, instance={}", instance==null);
        if(instance != null) {
            log.debug("In createDb, in if(instance!=null)");
            try(
                    Connection connection = instance.getConnection();
                    Statement statement = connection.createStatement();
                    InputStream is = DataBase.class.getClassLoader()
                            .getResourceAsStream("schema.sql")
            ) {
                if (is == null) {
                    throw new SQLException("schema.sql not found in resources");
                }
                String sql = new String(is.readAllBytes());
                log.debug("Creation sql: " + sql);
                for (String query : sql.split(";")) {
                    String trimmed = query.trim();
                    if (!trimmed.isEmpty()) {
                        statement.execute(trimmed);
                    }
                }
            }catch (SQLException e){
                log.error("Failed to run a database schema, because of SQLException:{}", e.getMessage());
            }catch (IOException e){
                log.error("Failed to run a database schema, because of IOException:{}", e.getMessage());
            }
        }
    }
}
