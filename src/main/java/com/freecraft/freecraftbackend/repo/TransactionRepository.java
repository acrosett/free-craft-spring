package com.freecraft.freecraftbackend.repo;

import com.freecraft.freecraftbackend.entity.Transaction;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Integer> {


    Optional<Transaction> findById(Integer id);


    @Transactional
    @Modifying
    @Query(value = "DELETE FROM transaction WHERE executed = 0 AND creation_date < DATE_SUB(CURRENT_DATE(), INTERVAL 30 DAY)", nativeQuery = true)
    void deleteOldTransaction();

    @Query(value = "SELECT * FROM transaction WHERE executed = 0 AND creation_date > DATE_SUB(CURRENT_DATE(), INTERVAL 30 DAY)", nativeQuery = true)
    List<Transaction> getRecentTransaction();


}
