package org.codedivoire.dembesi.dictionary.repository;

import org.codedivoire.dembesi.dictionary.entity.Diction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author  Christian Amani on 23/08/2018.
 */
@Repository
public interface DictionRepository extends JpaRepository<Diction,Long> {
}
