package com.example.demo.bootstrap;

import com.example.demo.model.Dish;
import com.example.demo.model.ErrorMessage;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.repository.DishRepository;
import com.example.demo.repository.ErrorMessageRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class BootstrapData implements CommandLineRunner {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    private DishRepository dishRepository;
    private OrderRepository orderRepository;

    private ErrorMessageRepository errorMessageRepository;

    public BootstrapData(UserRepository userRepository, PasswordEncoder passwordEncoder, DishRepository dishRepository,
                         OrderRepository orderRepository, ErrorMessageRepository errorMessageRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.dishRepository = dishRepository;
        this.orderRepository = orderRepository;
        this.errorMessageRepository = errorMessageRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        User u1 = new User();
        u1.setFirstName("Nadja");
        u1.setLastName("Radojicic");
        u1.setEmail("nadja@raf.rs");
        u1.setPassword(passwordEncoder.encode("nadja123"));

        u1.getRoles().setCan_create_users(true);
        u1.getRoles().setCan_read_users(true);
        u1.getRoles().setCan_update_users(true);
        u1.getRoles().setCan_delete_users(true);

        u1.getRoles().setCan_place_order(true);
        u1.getRoles().setCan_cancel_order(true);
        u1.getRoles().setCan_schedule_order(true);
        u1.getRoles().setCan_track_order(true);
        u1.getRoles().setCan_search_order(true);

        User u2 = new User();
        u2.setFirstName("Nenad");
        u2.setLastName("Kujundzic");
        u2.setEmail("nenad@raf.rs");
        u2.setPassword(passwordEncoder.encode("nenad123"));
        u2.getRoles().setCan_read_users(true);
        u2.getRoles().setCan_create_users(true);
        u2.getRoles().setCan_place_order(true);
        u2.getRoles().setCan_cancel_order(true);

        User u3 = new User();
        u3.setFirstName("Nina");
        u3.setLastName("Petkovic");
        u3.setEmail("nina@raf.rs");
        u3.setPassword(passwordEncoder.encode("nina123"));
        u3.getRoles().setCan_read_users(true);
        u3.getRoles().setCan_place_order(true);
        u3.getRoles().setCan_cancel_order(true);

        User u4 = new User();
        u4.setFirstName("Anica");
        u4.setLastName("Matovic");
        u4.setEmail("anica@raf.rs");
        u4.setPassword(passwordEncoder.encode("anica123"));

        userRepository.save(u1);
        userRepository.save(u2);
        userRepository.save(u3);
        userRepository.save(u4);


        // Adding Dishes
        Dish dish1 = new Dish();
        dish1.setDishName("Avocado Toast");
        dish1.setDescription("Whole grain toast topped with smashed avocado, lemon, and seeds.");
        dish1.setBreakfast(true);
        dish1.setImageUrl("/images/avocado_toast.jpg");
        dish1.setCena(790);

        Dish dish2 = new Dish();
        dish2.setDishName("Smoothie Bowl");
        dish2.setDescription("Blended fruits topped with granola, nuts, and seeds.");
        dish2.setBreakfast(true);
        dish2.setImageUrl("/images/smoothie_bowl.jpg");
        dish2.setCena(550);

        Dish dish3 = new Dish();
        dish3.setDishName("Oatmeal");
        dish3.setDescription("Steel-cut oats with almond butter and fresh berries.");
        dish3.setBreakfast(true);
        dish3.setImageUrl("/images/oatmeal.jpg");
        dish3.setCena(750);

        Dish dish4 = new Dish();
        dish4.setDishName("Greek Yogurt Parfait");
        dish4.setDescription("Low-fat Greek yogurt layered with honey and granola.");
        dish4.setBreakfast(true);
        dish4.setImageUrl("/images/greek_yogurt.jpg");
        dish4.setCena(550);


        Dish dish6 = new Dish();
        dish6.setDishName("Quinoa Salad");
        dish6.setDescription("Quinoa mixed with fresh vegetables and olive oil dressing.");
        dish6.setLunch(true);
        dish6.setImageUrl("/images/quinoa.jpg");
        dish6.setCena(1100);

        Dish dish7 = new Dish();
        dish7.setDishName("Grilled Chicken Wrap");
        dish7.setDescription("Whole-grain wrap filled with grilled chicken and greens.");
        dish7.setLunch(true);
        dish7.setImageUrl("/images/chicken_wrap2.jpg");
        dish7.setCena(1220);

        Dish dish8 = new Dish();
        dish8.setDishName("Lentil Soup");
        dish8.setDescription("Warm soup with lentils, carrots, and celery.");
        dish8.setLunch(true);
        dish8.setImageUrl("/images/lentil_soup2.jpg");
        dish8.setCena(880);

        Dish dish9 = new Dish();
        dish9.setDishName("Veggie Stir-Fry");
        dish9.setDescription("Assorted vegetables stir-fried with tofu.");
        dish9.setLunch(true);
        dish9.setImageUrl("/images/veggie_stir_fry.jpg");
        dish9.setCena(970);


        Dish dish11 = new Dish();
        dish11.setDishName("Grilled Salmon");
        dish11.setDescription("Salmon fillet served with steamed broccoli.");
        dish11.setDinner(true);
        dish11.setImageUrl("/images/grilled_salmon.jpg");
        dish11.setCena(2200);

        Dish dish12 = new Dish();
        dish12.setDishName("Zucchini Noodles");
        dish12.setDescription("Spiralized zucchini with marinara sauce.");
        dish12.setDinner(true);
        dish12.setImageUrl("/images/zucchini_noodles.jpg");
        dish12.setCena(1350);

        Dish dish13 = new Dish();
        dish13.setDishName("Baked Sweet Potato");
        dish13.setDescription("Sweet potato served with a dollop of Greek yogurt.");
        dish13.setDinner(true);
        dish13.setImageUrl("/images/baked_potato.jpg");
        dish13.setCena(990);

        Dish dish14 = new Dish();
        dish14.setDishName("Mushroom Risotto");
        dish14.setDescription("Creamy risotto with mushrooms and herbs.");
        dish14.setDinner(true);
        dish14.setImageUrl("/images/risotto.jpg");
        dish14.setCena(1440);



        Dish dish16 = new Dish();
        dish16.setDishName("Dark Chocolate Avocado Mousse");
        dish16.setDescription("Creamy mousse made with avocado and dark chocolate.");
        dish16.setDesserts(true);
        dish16.setImageUrl("/images/chocolate_mousse.jpg");
        dish16.setCena(750);

        Dish dish17 = new Dish();
        dish17.setDishName("Fruit Salad");
        dish17.setDescription("A mix of seasonal fresh fruits.");
        dish17.setDesserts(true);
        dish17.setImageUrl("/images/fruit_salad.jpg");
        dish17.setCena(590);

        Dish dish18 = new Dish();
        dish18.setDishName("Almond Flour Brownies");
        dish18.setDescription("Gluten-free brownies made with almond flour.");
        dish18.setDesserts(true);
        dish18.setImageUrl("/images/brownies.jpg");
        dish18.setCena(660);

        dishRepository.saveAll(Arrays.asList(dish1, dish2, dish3, dish4, dish6, dish7, dish8, dish9, dish11, dish12, dish13, dish14, dish16, dish17, dish18));

        Order order1 = new Order();
        order1.setStatus(Order.OrderStatus.DELIVERED);
        order1.setPaymentType(Order.PaymentType.CASH);
        order1.setAddress("Ustanicka 49");
        order1.setActive(false);
        order1.setCreatedBy(u2);
        order1.setDishes(List.of(dish2, dish3));
        order1.setCenaPorudzbine(1300);
        order1.setScheduledFor(null);
        order1.setCreatedAt(LocalDateTime.of(2024, 12, 20, 15, 30));

        Order order2 = new Order();
        order2.setStatus(Order.OrderStatus.DELIVERED);
        order2.setPaymentType(Order.PaymentType.CARD);
        order2.setAddress("Krunska 17");
        order2.setActive(false);
        order2.setCreatedBy(u3);
        order2.setDishes(List.of(dish7, dish13));
        order2.setCenaPorudzbine(2210);
        order2.setScheduledFor(null);
        order2.setCreatedAt(LocalDateTime.of(2024, 12, 29, 12, 35));

        Order order3 = new Order();
        order3.setStatus(Order.OrderStatus.ORDERED);
        order3.setPaymentType(Order.PaymentType.CARD);
        order3.setAddress("Kiridzijska 8");
        order3.setActive(true);
        order3.setCreatedBy(u1);
        order3.setDishes(List.of(dish17));
        order3.setCenaPorudzbine(590);
        order3.setScheduledFor(LocalDateTime.of(2025, 1, 29, 12, 35));
        order3.setCreatedAt(LocalDateTime.of(2025, 1, 20, 12, 35));

        Order order4 = new Order();
        order4.setStatus(Order.OrderStatus.ORDERED);
        order4.setPaymentType(Order.PaymentType.CARD);
        order4.setAddress("Uzun Mirkova 5");
        order4.setActive(true);
        order4.setCreatedBy(u3);
        order4.setDishes(List.of(dish16));
        order4.setCenaPorudzbine(750);
        order4.setScheduledFor(LocalDateTime.of(2025, 1, 30, 14, 50));
        order4.setCreatedAt(LocalDateTime.of(2025, 1, 20, 12, 35));

        orderRepository.saveAll(List.of(order1, order2, order3, order4));


    }


}
