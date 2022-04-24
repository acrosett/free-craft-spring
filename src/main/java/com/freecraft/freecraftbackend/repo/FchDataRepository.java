package com.freecraft.freecraftbackend.repo;

import com.freecraft.freecraftbackend.entity.FchData;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FchDataRepository extends CrudRepository<FchData, String> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE fch_userdata SET BUYING = ?1 WHERE UUID = ?2", nativeQuery = true)
    int setBuying(boolean buying, String uuid);

    @Transactional
    @Modifying
    @Query(value = "UPDATE fch_userdata SET UNLOCKED = ?1 WHERE UUID = ?2", nativeQuery = true)
    int setUnlocked(boolean buying, String uuid);

}
