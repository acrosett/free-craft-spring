package com.freecraft.freecraftbackend.repo;

import com.freecraft.freecraftbackend.entity.FaqItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

@Repository
public interface FaqItemRepository extends CrudRepository<FaqItem, Integer> {

    @Override
    Optional<FaqItem> findById(Integer integer);

    @Transactional
    @Modifying
    @Query(value = "UPDATE faq_item SET weight = weight + 1 WHERE name = ?1", nativeQuery = true)
    void incrementByName(String name);
}