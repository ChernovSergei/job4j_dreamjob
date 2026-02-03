package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDate;
import java.util.*;

public class MemoryCandidateRepository implements CandidateRepository{

    private static final MemoryCandidateRepository INSTANCE = new MemoryCandidateRepository();
    private int nextId = 1;
    private final Map<Integer, Candidate> candidates = new HashMap<>();

    public MemoryCandidateRepository() {
        save( new Candidate(0, "George", "Engineer", LocalDate.now()));
        save( new Candidate(0, "Mihai", "Doctor", LocalDate.now()));
        save( new Candidate(0, "Yulia", "Teacher", LocalDate.of(1986, 4, 15)));
        save( new Candidate(0, "Anton", "Shopper", LocalDate.now()));
        save( new Candidate(0, "Evgenia", "Pilot", LocalDate.of(1940, 1, 29)));
    }


    public static MemoryCandidateRepository getInstance () {
        return INSTANCE;
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId++);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public void deleteById(int id) {
        candidates.remove(id);
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
