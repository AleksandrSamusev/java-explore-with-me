package ru.practicum.stat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.stat.models.EndpointHit;

public interface StatRepository extends JpaRepository<EndpointHit, Long> {
}
