package nl.bkwi.samenwerkingsverbandapiv001.dbsuwinetinkijk.repositories;

import nl.bkwi.samenwerkingsverbandapiv001.dbsuwinetinkijk.entities.Profile;
import nl.bkwi.samenwerkingsverbandapiv001.dbsuwinetinkijk.entities.ProfileId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface ProfileRepository extends JpaRepository<Profile, ProfileId> {

    @Query(
            value = """
                    select p.value
                    from   profile p
                    where  p.context = :userDn
                    and    p.name = 'Samenwerkingsverband.voorkeursgemeente'""",
            nativeQuery = true
    )
    String findVoorkeursGemeente(@Param("userDn") String userDn);
}
