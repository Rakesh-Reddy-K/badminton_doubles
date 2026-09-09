package com.example.badminton.repository;

import com.example.badminton.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long>, JpaSpecificationExecutor<Player> {

    Optional<Player> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Player> findByActiveTrueOrderByNameAsc();

    List<Player> findAllByOrderByNameAsc();

    List<Player> findByNameContainingIgnoreCaseOrderByNameAsc(String searchTerm);

    long countByActiveTrue();
}
