package org.example.repositories.postgres;

import org.example.exceptions.ExceptionType;
import org.example.exceptions.UrlNotFoundException;
import org.example.repositories.Repository;
import org.example.repositories.entities.UrlEntity;
import org.example.servlets.NewServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;

public class DbRepository implements Repository {
    private static final Logger log = LoggerFactory.getLogger(DbRepository.class);

    public DbRepository(){}


    @Override
    public long save(UrlEntity urlEntity){
        if(check(urlEntity.getUrlOld())){
            return get(urlEntity.getUrlOld()).getId();
        }
        String sql = "INSERT INTO url (url_old) VALUES (?)";
        try(
                Connection connection = DataBase.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ){
            statement.setString(1, urlEntity.getUrlOld());
            int affected = statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    log.error("Не удалось сохранить запись в базе данных");
                }
            }
        }catch(SQLException e){
            log.error("Ошибка при сохранении записи в базе данных: {}", e.getMessage());
        }
        return 0;
    }

    @Override
    public UrlEntity get(long id){
        String sql = "SELECT id, url_old, url_new FROM url WHERE id=?";
        UrlEntity entity = null;
        try(
                Connection connection = DataBase.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setLong(1, id);
            try(ResultSet result = statement.executeQuery()){
                if(result.next()){
                    entity = new UrlEntity();
                    entity.setId(result.getLong("id"));
                    entity.setUrlOld(result.getString("url_old"));
                    entity.setUrlNew(result.getString("url_new"));
                }
            }
        }catch(SQLException e){
            log.error("Ошибка при получении записи в базе данных с id{}: {}", id, e.getMessage());
        }

        return entity;
    }

    @Override
    public UrlEntity get(String oldUrl){
        String sql = "SELECT id, url_old, url_new FROM url WHERE url_old=?";
        UrlEntity entity = null;
        try(
                Connection connection = DataBase.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setString(1, oldUrl);
            try(ResultSet result = statement.executeQuery()){
                if(result.next()){
                    entity = new UrlEntity();
                    entity.setId(result.getLong("id"));
                    entity.setUrlOld(result.getString("url_old"));
                    entity.setUrlNew(result.getString("url_new"));
                }
            }
        }catch(SQLException e){
            log.error("Ошибка при получении записи в базе данных с oldUrl{}: {}", oldUrl, e.getMessage());
        }

        return entity;
    }

    @Override
    public boolean check(String urlOld) {
        String sql = "SELECT id FROM url WHERE url_old=?";
        try(
                Connection connection = DataBase.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setString(1, urlOld);
            try(ResultSet result = statement.executeQuery()){
                if(result.next()){
                    return true;
                }
            }

        }catch(SQLException e){
            log.error("Ошибка при проверки наличия записи в базе данных с url{}: {}", urlOld, e.getMessage());
        }

        return false;
    }

    @Override
    public boolean check(long id) {
        String sql = "SELECT id FROM url WHERE id=?";
        try(
                Connection connection = DataBase.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setLong(1, id);
            try(ResultSet result = statement.executeQuery()){
                if(result.next()){
                    return true;
                }
            }

        }catch(SQLException e){
            log.error("Ошибка при проверки наличия записи в базе данных с id{}: {}", id, e.getMessage());
        }

        return false;
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM URL WHERE id=?";

        try(
                Connection connection = DataBase.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setLong(1, id);
            statement.executeUpdate();
        }catch(SQLException e){
            log.error("Ошибка при попытке удалить элемент с id{} из базы данных; {}", id, e.getMessage());
        }
    }

    @Override
    public void delete(String urlOld) {
        if(!check(urlOld)) return;
        String sql = "DELETE FROM URL WHERE url_old=?";
        try(
                Connection connection = DataBase.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setString(1, urlOld);
            statement.executeUpdate();
        }catch(SQLException e){
            log.error("Ошибка при попытке удалить элемент с url{} из базы данных; {}", urlOld, e.getMessage());
        }
    }
}
