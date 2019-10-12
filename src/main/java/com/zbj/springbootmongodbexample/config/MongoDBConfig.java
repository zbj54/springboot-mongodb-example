package com.zbj.springbootmongodbexample.config;

import com.zbj.springbootmongodbexample.repository.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackageClasses = UserRepository.class)
@Configuration
public class MongoDBConfig {


    /*@Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository) {
        return strings -> {
            userRepository.save(new Users(1, "Peter", "Development", 30000L));
            userRepository.save(new Users(2, "Sam", "Operations", 20000L));
        };
    }*/

}
