package com.freecraft.freecraftbackend.repo;

        import com.freecraft.freecraftbackend.entity.ClaimPlayerData;
        import org.springframework.data.repository.CrudRepository;
        import org.springframework.stereotype.Repository;

        import java.util.Optional;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

@Repository
public interface ClaimPlayerDataRepository extends CrudRepository<ClaimPlayerData, String> {

    @Override
    Optional<ClaimPlayerData> findById(String uuid);

}