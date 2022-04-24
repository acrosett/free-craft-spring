package com.freecraft.freecraftbackend.repo;

import com.freecraft.freecraftbackend.entity.PermissionPlayerData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

@Repository
public interface PermissionPlayerDataRepository extends CrudRepository<PermissionPlayerData, String> {

    @Override
    Optional<PermissionPlayerData> findById(String uuid);

    @Query(value = "SELECT * FROM luckperms_players WHERE executed = 0 AND date > DATEADD(day, -7, GETDATE())", nativeQuery = true)
    List<PermissionPlayerData> getNormalPlayers();

}