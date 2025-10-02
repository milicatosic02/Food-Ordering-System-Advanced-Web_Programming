package com.example.demo.repository;

import com.example.demo.model.Order;
import com.example.demo.model.User;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

     List<Order> findByCreatedByAndErrorMessageIsNull(User user, Sort sort); // history za usera

     int countByStatusInOrScheduledForIsNotNull(List<Order.OrderStatus> statuses); //broj aktivnih in delivery i preparing + scheduled

     List<Order> findByErrorMessageIsNull(Sort sort); // history - vracamo samo one porudzbine koje su se desile ili koje su trenutne - bez neuspelih

     List<Order> findByActiveTrue(Sort sort); //  vracamo sve aktivne porudzbine kako bi admin mogao da prati(track)

     List<Order> findByActiveTrueAndCreatedBy(User user, Sort sort);  //vracamo sve aktivne porudzbine za ulogovanog korisnika kako bi mogao da prati(track)
     List<Order> findByStatus(Order.OrderStatus status);


    int countByScheduledFor(LocalDateTime scheduledFor);

    List<Order> findByScheduledFor(LocalDateTime scheduledFor);
    int countByStatusIn(List<Order.OrderStatus> statuses);




    @Modifying
     @Query("UPDATE Order o SET o.status = :newStatus, o.active = false " +
             "WHERE o.status = :currentStatus " +
             "AND o.active = true " +
             "AND o.scheduledFor IS NULL " +
             "AND o.createdAt <= :cutoffTime")
     @Transactional
     void updateOrderStatusToDelivered(@Param("newStatus") Order.OrderStatus newStatus,
                            @Param("currentStatus") Order.OrderStatus currentStatus,
                            @Param("cutoffTime") LocalDateTime cutoffTime);


     @Modifying
     @Query("UPDATE Order o SET o.status = :newStatus " +
             "WHERE o.status = :currentStatus " +
             "AND o.active = true " +
             "AND o.scheduledFor IS NULL " +
             "AND o.createdAt <= :cutoffTime")
     @Transactional
    void updateOrderStatus(@Param("newStatus") Order.OrderStatus newStatus,
                            @Param("currentStatus") Order.OrderStatus currentStatus,
                            @Param("cutoffTime") LocalDateTime cutoffTime);



     @Query("SELECT o FROM Order o WHERE " +
             "(:user IS NULL OR o.createdBy.email = :user) AND " +
             "(:status IS NULL OR o.status = :status) AND " +
             "(:dateFrom IS NULL OR :dateTo IS NULL OR (o.createdAt BETWEEN :dateFrom AND :dateTo))")
     List<Order> searchOrders(
             @Param("user") String user,
             @Param("status") Order.OrderStatus status,
             @Param("dateFrom") LocalDateTime dateFrom,
             @Param("dateTo") LocalDateTime dateTo
     );
}
