package com.example.demo.service;

import com.example.demo.model.Authority;
import com.example.demo.model.Roles;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UserServicee implements IServicee<User,Long>, UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserServicee(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public <S extends User> S save(S user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return (List<User>)userRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       Optional<User> optionalUser = this.userRepository.findByEmail(email);
       if(!optionalUser.isPresent()){
           throw new UsernameNotFoundException("User with email "+ email +" not found");
       }
        User myUser = optionalUser.get();

        return new org.springframework.security.core.userdetails.User(myUser.getEmail(), myUser.getPassword(),getUserAuthorities(myUser));
    }
    public List<Authority> getUserAuthorities(User user){

        List<Authority> authorities = new ArrayList<>();
        Roles roles = user.getRoles();

        if(roles.getCan_create_users())
            authorities.add(new Authority("can_create_users"));

        if(roles.getCan_read_users())
            authorities.add(new Authority("can_read_users"));

        if(roles.getCan_update_users())
            authorities.add(new Authority("can_update_users"));

        if(roles.getCan_delete_users())
            authorities.add(new Authority("can_delete_users"));

        if(roles.getCan_place_order())
            authorities.add(new Authority("can_place_order"));

        if(roles.getCan_cancel_order())
            authorities.add(new Authority("can_cancel_order"));

        if(roles.getCan_schedule_order())
            authorities.add(new Authority("can_schedule_order"));

        if(roles.getCan_search_order())
            authorities.add(new Authority("can_search_order"));

        if(roles.getCan_track_order())
            authorities.add(new Authority("can_track_order"));

        return authorities;

    }
    public Optional<User> updateUser(User user, Long id) {

        Optional<User> userOptional = userRepository.findById(id);

        if(!userOptional.isPresent())
            return userOptional;

        User returnedUser = userOptional.get();
        returnedUser.setFirstName(user.getFirstName());
        returnedUser.setLastName(user.getLastName());
        returnedUser.setEmail(user.getEmail());

        returnedUser.getRoles().setCan_read_users(user.getRoles().getCan_read_users());
        returnedUser.getRoles().setCan_create_users(user.getRoles().getCan_create_users());
        returnedUser.getRoles().setCan_update_users(user.getRoles().getCan_update_users());
        returnedUser.getRoles().setCan_delete_users(user.getRoles().getCan_delete_users());

        returnedUser.getRoles().setCan_place_order(user.getRoles().getCan_place_order());
        returnedUser.getRoles().setCan_cancel_order(user.getRoles().getCan_cancel_order());
        returnedUser.getRoles().setCan_search_order(user.getRoles().getCan_search_order());
        returnedUser.getRoles().setCan_track_order(user.getRoles().getCan_track_order());
        returnedUser.getRoles().setCan_schedule_order(user.getRoles().getCan_schedule_order());


        return Optional.ofNullable(userRepository.save(returnedUser));

    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findUserByEmail(String email){
        Optional<User> optionalUser = this.userRepository.findByEmail(email);
        if(!optionalUser.isPresent()){
            throw new UsernameNotFoundException("User with email "+ email +" not found");
        }
        User myUser = optionalUser.get();

        return myUser;
    }

}
