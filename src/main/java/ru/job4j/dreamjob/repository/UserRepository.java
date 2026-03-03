package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.User;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findByEmailAndPassword(String email, String password);
}
