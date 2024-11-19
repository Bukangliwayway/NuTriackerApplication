package com.example.NuTriacker.processor;

import com.example.NuTriacker.model.User;
import com.example.NuTriacker.seeder.SeedPrototype;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;


@Component
public class UserProcessor implements ItemProcessor<SeedPrototype, User>{
    @Override
    public User process(SeedPrototype item) throws Exception {
        User user = new User();
        user.setId(item.getUserId());
        user.setFirstName(item.getFirstName());
        user.setLastName(item.getLastName());
        user.setEmail(item.getEmail());
        user.setPassword(item.getPassword());
        return user;
    }
}
