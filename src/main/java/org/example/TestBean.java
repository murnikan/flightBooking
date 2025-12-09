package org.example;

import org.springframework.stereotype.Component;

@Component
public class TestBean {
    public void sayHello() {
        System.out.println("Spring работает!");
    }
}
