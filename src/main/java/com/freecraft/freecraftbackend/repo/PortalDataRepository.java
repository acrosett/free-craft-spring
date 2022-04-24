package com.freecraft.freecraftbackend.repo;

import com.freecraft.freecraftbackend.entity.PortalData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortalDataRepository extends CrudRepository<PortalData, String> {

}