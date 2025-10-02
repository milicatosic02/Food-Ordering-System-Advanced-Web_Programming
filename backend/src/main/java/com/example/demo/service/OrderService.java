package com.example.demo.service;

import com.example.demo.model.ErrorMessage;
import com.example.demo.model.Message;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.repository.ErrorMessageRepository;
import com.example.demo.repository.OrderRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.time.Duration;
import java.time.format.DateTimeFormatter;


@Service
public class OrderService implements IServicee<Order, Long>{

    private final OrderRepository orderRepository;
    private final ErrorMessageRepository errorMessageRepository;
    //private SimpMessageSendingOperations messagingTemplate;
    private SimpMessagingTemplate messagingTemplate;


    private TaskScheduler taskScheduler;

    @Autowired
    public OrderService(OrderRepository orderRepository, ErrorMessageRepository errorMessageRepository, TaskScheduler taskScheduler, SimpMessagingTemplate messagingTemplate) {
        this.orderRepository = orderRepository;
        this.errorMessageRepository = errorMessageRepository;
        this.taskScheduler = taskScheduler;
        this.messagingTemplate = messagingTemplate;
    }
    @Override
    public <S extends Order> S save(S order) {
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }
    @Override
    public void deleteById(Long var1) {

    }

    public List<Order> findHistoryByUser(User user) {
        return orderRepository.findByCreatedByAndErrorMessageIsNull(user, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

//    public int countActiveOrders(Order.OrderStatus... statuses) {
//        return orderRepository.countByStatusInOrScheduledForIsNotNull(List.of(statuses));
//    }

    public int countScheduledOrdersAtTime(LocalDateTime scheduledFor) {
        return orderRepository.countByScheduledFor(scheduledFor);
    }

    public int countActiveOrders(Order.OrderStatus... statuses) {
        return orderRepository.countByStatusIn(List.of(statuses));
    }


    public ErrorMessage logError(String message, Order order) {
        ErrorMessage error = new ErrorMessage();
        error.setMessage(message);
        error.setTimestamp(LocalDateTime.now());
        error.setOrder(order);
        errorMessageRepository.save(error);

        return error;
    }

    public List<Order> findOrderHistory(){
        return orderRepository.findByErrorMessageIsNull(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public List<Order> findByActiveTrue(){
        return orderRepository.findByActiveTrue(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public List<Order> findByActiveTrueAndCreatedBy(User user){
        return orderRepository.findByActiveTrueAndCreatedBy(user, Sort.by(Sort.Direction.DESC, "createdAt"));
    }


    //Automatska promena statusa  sa ORDERED na PREPARING
    @Scheduled(fixedRate = 10000) // Proverava svakih 10 sekundi
    public void changeStatusToPreparing() {
        LocalDateTime cutoffTime = LocalDateTime.now()
                .minus(Duration.ofSeconds(10).plusMillis(getRandomDeviation(2000)));

        orderRepository.updateOrderStatus(Order.OrderStatus.PREPARING, Order.OrderStatus.ORDERED, cutoffTime);

        // Dohvat ažuriranih narudžbina
        List<Order> updatedOrders = orderRepository.findByStatus(Order.OrderStatus.PREPARING);
        for (Order order : updatedOrders) {
            Message message = new Message(order.getId(), Order.OrderStatus.PREPARING);
            messagingTemplate.convertAndSend("/topic/order-status", message);
        }

    }

    // Automatska promena statusa sa PREPARING na IN_DELIVERY
    @Scheduled(fixedRate = 15000) // Proverava svakih 15 sekundi
    public void changeStatusToInDelivery() {
        LocalDateTime cutoffTime = LocalDateTime.now()
                .minus(Duration.ofSeconds(15).plusMillis(getRandomDeviation(2000)));

        orderRepository.updateOrderStatus(Order.OrderStatus.IN_DELIVERY, Order.OrderStatus.PREPARING, cutoffTime);
        List<Order> updatedOrders = orderRepository.findByStatus(Order.OrderStatus.IN_DELIVERY);

        for (Order order : updatedOrders) {
            Message message = new Message(order.getId(), Order.OrderStatus.IN_DELIVERY);
            messagingTemplate.convertAndSend("/topic/order-status", message);
        }

    }

    @Scheduled(fixedRate = 20000) // Proverava svakih 20 sekundi
    public void changeStatusToDelivered() {
        // Trenutno vreme minus 20 sekundi plus nasumična devijacija
        LocalDateTime cutoffTime = LocalDateTime.now()
                .minus(Duration.ofSeconds(20).plusMillis(getRandomDeviation(2000)));


        orderRepository.updateOrderStatusToDelivered(Order.OrderStatus.DELIVERED, Order.OrderStatus.IN_DELIVERY, cutoffTime);

        List<Order> updatedOrders = orderRepository.findByStatus(Order.OrderStatus.DELIVERED);


        for (Order order : updatedOrders) {
            Message message = new Message(order.getId(), Order.OrderStatus.DELIVERED);
            messagingTemplate.convertAndSend("/topic/order-status", message);
        }
    }

    private int getRandomDeviation(int maxDeviation) {
        Random rand = new Random();
        int deviation = rand.nextInt(maxDeviation);
        System.out.println("Random Deviation: " + deviation + " ms");
        return deviation;
    }


    public List<Order> searchOrders(String email,Order.OrderStatus status, String dateFrom, String dateTo) {
        LocalDateTime parsedDateFrom = dateFrom != null ? parseDate(dateFrom) : null;
        LocalDateTime parsedDateTo = dateTo != null ? parseDate(dateTo) : null;

        return orderRepository.searchOrders(email, status, parsedDateFrom, parsedDateTo);
    }

    private LocalDateTime parseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Format u koji se očekuje datum
        return LocalDateTime.parse(date + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME); // Dodajemo "T00:00:00" da bismo formirali puni LocalDateTime
    }


    public void scheduledOrder(Order order) {
        LocalDateTime scheduledFor = order.getScheduledFor();
        System.out.println("Zakazivanje porudžbine za: " + scheduledFor);


        // Konvertovanje LocalDateTime u java.util.Date
        Date executionTime = Date.from(scheduledFor.atZone(ZoneId.systemDefault()).toInstant());

        // Zakazivanje zadatka za određeno vreme
        this.taskScheduler.schedule(() -> {
            order.setScheduledFor(null);
            orderRepository.save(order);
        }, executionTime);
    }


    public List<Order> getScheduledForTime(LocalDateTime scheduledFor){
        return orderRepository.findByScheduledFor(scheduledFor);
    }


}
