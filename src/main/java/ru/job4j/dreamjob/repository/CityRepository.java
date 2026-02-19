package ru.job4j.dreamjob.repository;

import java.util.Collection;
import ru.job4j.dreamjob.model.City;

public interface CityRepository {
    Collection<City> findAll();
}
