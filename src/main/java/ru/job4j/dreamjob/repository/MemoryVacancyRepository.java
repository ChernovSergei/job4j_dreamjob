package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.jcip.annotations.ThreadSafe;

@Repository
@ThreadSafe
public class MemoryVacancyRepository implements VacancyRepository {

    private static final MemoryVacancyRepository INSTANCE = new MemoryVacancyRepository();
    private final AtomicInteger nextId = new AtomicInteger(1);

    private final Map<Integer, Vacancy> vacancies = new ConcurrentHashMap<>();

    public MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer",
                "Learn how to press keys for money",
                LocalDateTime.of(2023, 01, 05, 12, 36)));
        save(new Vacancy(0, "Junior Java Developer",
                "Learn how to program for money",
                LocalDateTime.of(2000, 3, 28, 16, 5)));
        save(new Vacancy(0, "Junior+ Java Developer",
                "Here you are",
                LocalDateTime.of(2013, 9, 9, 9, 30)));
        save(new Vacancy(0, "Middle Java Developer",
                "You will implement some projects in a team ",
                LocalDateTime.of(2007, 11, 1, 22, 0)));
        save(new Vacancy(0, "Middle+ Java Developer",
                "You will implement your job + senior job without additional payment",
                LocalDateTime.of(2009, 10, 22, 22, 22)));
        save(new Vacancy(0, "Senior Java Developer",
                "You will be alone in the building and making money",
                LocalDateTime.of(1996, 8, 11, 9, 30)));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId.incrementAndGet());
        vacancies.putIfAbsent(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return vacancies.remove(id) != null;
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(),
                (id, oldVacancy) ->
                        new Vacancy(oldVacancy.getId(),
                                vacancy.getTitle(),
                                vacancy.getDescription(),
                                vacancy.getCreationDate())) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}