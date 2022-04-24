package com.freecraft.freecraftbackend.repo;

import com.freecraft.freecraftbackend.entity.HeadData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeadDataRepository extends CrudRepository<HeadData, String> {

}
