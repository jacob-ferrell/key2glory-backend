package com.jacobferrell.Key2Glory.repository;

import com.jacobferrell.Key2Glory.model.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
    // findFirst: older data may contain more than one row per user
    Optional<Favorites> findFirstByUsernameOrderByIdAsc(String username);
}
