package com.petesitemmanager.pim.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.petesitemmanager.pim.domain.User;
import com.petesitemmanager.pim.repository.UserRepository;

@RestController
public class UserResource {

    @Autowired
    private UserRepository userRepository;

    // @PostMapping("/user")
    // User newUser(@RequestBody User newUser) {
    // return userRepository.save(newUser);
    // }

    // @GetMapping("/users")
    // List<User> getAllUsers() {
    // return userRepository.findAll();
    // }

    // @GetMapping("/user/{id}")
    // User getUserById(@PathVariable Long id) {
    // return userRepository.findById(id)
    // .orElseThrow(() -> new UserNotFoundException(id));
    // }

    // @DeleteMapping("/user/{id}")
    // String deleteUser(@PathVariable Long id) {
    // if (!userRepository.existsById(id)) {
    // throw new UserNotFoundException(id);
    // }
    // userRepository.deleteById(id);
    // return "User with id " + id + " has been deleted success.";
    // }

}
