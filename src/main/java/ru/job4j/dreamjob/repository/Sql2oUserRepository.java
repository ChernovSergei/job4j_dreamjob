package ru.job4j.dreamjob.repository;

import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.model.User;
import java.util.Optional;
import org.sql2o.Sql2oException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class Sql2oUserRepository implements UserRepository {

    private final Sql2o sql2o;

    public Sql2oUserRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<User> save(User user) {
        String sqlState = null;
        try (var connection = sql2o.open()) {
            var sql = """
                    INSERT INTO users(email, name, password)
                    VALUES (:email, :name, :password)
                    """;
            var query = connection.createQuery(sql, true)
                    .addParameter("email", user.getEmail())
                    .addParameter("name", user.getName())
                    .addParameter("password", user.getPassword());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            user.setId(generatedId);
        } catch (Sql2oException e) {
            log.error(e.getMessage());
            for (Throwable exception = e; exception != null; exception = exception.getCause()) {
                if (exception instanceof PSQLException psqlException) {
                    sqlState = psqlException.getSQLState();
                    break;
                }
            }
        }
        return "23505".equals(sqlState) ? Optional.empty() : Optional.of(user);
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM users "
                    + "WHERE  email = :email AND password = :password");
            query.addParameter("email", email);
            query.addParameter("password", password);
            var user = query.executeAndFetchFirst(User.class);
            return Optional.ofNullable(user);
        }
    }
}
