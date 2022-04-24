package com.freecraft.freecraftbackend.repo;

import com.freecraft.freecraftbackend.entity.PermissionData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

@Repository
public interface PermissionDataRepository extends CrudRepository<PermissionData, String> {

    List<PermissionData>findByUuid(String uuid);

}