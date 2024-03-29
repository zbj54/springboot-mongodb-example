package com.zbj.springbootmongodbexample.resource;

import com.zbj.springbootmongodbexample.document.Users;
import com.zbj.springbootmongodbexample.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/users")
public class UsersResource {

    private UserRepository userRepository;

    public UsersResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/all")
    public List<Users> getAll() {
        return userRepository.findAll();
    }
    @GetMapping("/one")
    public Users getOne() {
        return userRepository.findOne(1);
    }
    @GetMapping("/save")
    public Users save() {
        return userRepository.save(new Users(3, "zbj", "zbjs", 33333L));
    }
}
