package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT r FROM Request r WHERE r.eventId = ?1 and r.status = 'CONFIRMED'")
    List<Request> findAllConfirmedRequestsByEventId(Long eventId);

    @Query("SELECT r FROM Request r WHERE r.eventId = ?1 and r.status = 'PENDING'")
    List<Request> findAllPendingRequestsByEventId(Long eventId);

    @Query("SELECT r FROM Request r WHERE  r.requesterId = ?1 ")
    List<Request> findAllRequestsByUserId(Long userId);

    @Query("SELECT r FROM Request r WHERE r.eventId = ?1")
    List<Request> findAllRequestsByEventId(Long eventId);
}
