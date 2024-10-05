package com.jacobferrell.Key2Glory.repository;

import com.jacobferrell.Key2Glory.model.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
    @Query("SELECT f FROM Favorites f WHERE f.username = :username")
    Optional<Favorites> findByUsername(String username);
}
