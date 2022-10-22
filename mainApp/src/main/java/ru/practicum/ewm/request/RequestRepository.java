package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT r FROM Request r WHERE  r.requesterId = ?1 and r.eventId = ?2")
    List<Request> findAllRequestsByUserIdAndEventId(Long userId, Long eventId);

    @Query("SELECT r FROM Request r WHERE r.requestId = ?1")
    Request findRequestByRequestId(Long requestId);

    @Query("SELECT r FROM Request r WHERE r.eventId = ?1 and r.status = 'CONFIRMED'")
    List<Request> findAllConfirmedRequestsByEventId(Long eventId);

    @Query("SELECT r FROM Request r WHERE r.eventId = ?1 and r.status = 'PENDING'")
    List<Request> findAllPendingRequestsByEventId(Long eventId);


}
