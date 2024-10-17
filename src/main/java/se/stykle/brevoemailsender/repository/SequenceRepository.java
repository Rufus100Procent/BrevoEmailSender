package se.stykle.brevoemailsender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.stykle.brevoemailsender.entity.Sequence;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SequenceRepository extends JpaRepository<Sequence, UUID> {
    Optional<Sequence> findByName(String name);
}
