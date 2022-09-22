package nl.bkwi.samenwerkingsverbandapiv001.dbuseradmin.repositories;

import nl.bkwi.samenwerkingsverbandapiv001.dbuseradmin.entities.Samenwerkingsverband;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * This repository has been added in order to support the unit tests for GemeenteParticipatieRepository.
 */
@Repository
@Transactional(readOnly = true)
public interface SamenwerkingsverbandRepository extends JpaRepository<Samenwerkingsverband, Integer> {

}
