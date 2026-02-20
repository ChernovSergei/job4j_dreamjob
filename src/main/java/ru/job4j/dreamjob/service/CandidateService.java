package ru.job4j.dreamjob.service;

import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;

import java.util.Collection;
import java.util.Optional;

public interface CandidateService {

    Candidate save(Candidate candidate, FileDto resume);

    boolean deleteById(int id);

    boolean update(Candidate candidate, FileDto resume);

    Optional<Candidate> findById(int id);

    Collection<Candidate> findAll();
}
