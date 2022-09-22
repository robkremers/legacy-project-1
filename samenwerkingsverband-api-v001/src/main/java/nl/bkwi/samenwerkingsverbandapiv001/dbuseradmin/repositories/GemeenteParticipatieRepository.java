package nl.bkwi.samenwerkingsverbandapiv001.dbuseradmin.repositories;

import java.util.List;

import nl.bkwi.samenwerkingsverbandapiv001.dbuseradmin.entities.GemeenteParticipatie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface GemeenteParticipatieRepository extends JpaRepository<GemeenteParticipatie, Integer> {


    @Query(
            value = """
                    select gp1.naam
                    from   gemeente_participatie gp1
                    where  gp1.samenwerkingsverband_id IN
                      (
                        select s1.id
                        from samenwerkingsverband s1
                        where s1.onderdeel = :onderdeel
                        and s1.id = gp1.samenwerkingsverband_id
                      )
                    and gp1.samenwerkingsverband_id IN
                      (
                        select gp2.samenwerkingsverband_id
                        from   gemeente_participatie gp2
                        where  gp2.uitvoerend = 1
                        and  gp2.naam = :naam
                        and  gp2.begin <= CURDATE()
                        and  (gp2.eind is NULL or gp2.eind >= CURDATE() )
                      )
                    and    gp1.type_organisatie = 'GEMEENTE'
                    group  by gp1.naam
                    order  by gp1.naam
                    """
            , nativeQuery = true)
    List<String> findGemeenteParticipaties(@Param("naam") String naam, @Param("onderdeel") String onderdeel);
}
