package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class MemoryCandidateRepository implements CandidateRepository {

    private static final MemoryCandidateRepository INSTANCE = new MemoryCandidateRepository();
    private int nextId = 1;
    private final Map<Integer, Candidate> candidates = new HashMap<>();

    public MemoryCandidateRepository() {
        save(new Candidate(0, "George",
                "Go",
                LocalDateTime.of(2025, 12, 31, 23, 59)));
        save(new Candidate(0, "Mihai",
                "Basic",
                LocalDateTime.of(2004, 11, 27, 3, 15)));
        save(new Candidate(0, "Yulia",
                "Java Developer",
                LocalDateTime.of(2024, 4, 15, 12, 1)));
        save(new Candidate(0, "Anton",
                "Angular Developer",
                LocalDateTime.of(2017, 5, 27, 12, 4)));
        save(new Candidate(0, "Evgenia",
                "C++",
                LocalDateTime.of(2022, 1, 29, 3, 15)));
    }

    public static MemoryCandidateRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId++);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(),
                (id, oldCandidate) ->
                        new Candidate(oldCandidate.getId(),
                                candidate.getName(),
                                candidate.getDescription(),
                                candidate.getCreatedDate())) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
