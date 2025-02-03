package vn.hoidanit.jobhunter.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) throws IdInvalidException {
        if (id >= 1500) {
            throw new IdInvalidException("Id khong lon hown 1501");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getUserById(id));
    }

    @GetMapping()
    public ResponseEntity<List<User>> getUsers() {

        // return
        // ResponseEntity.status(HttpStatus.OK).body(this.userService.getAllUsers());
        return ResponseEntity.ok(this.userService.getAllUsers());
    }

    @PostMapping()
    public ResponseEntity<User> createUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.createUser(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> createUser(@PathVariable Long id) {
        this.userService.deleteUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body("ok !");
    }
}
