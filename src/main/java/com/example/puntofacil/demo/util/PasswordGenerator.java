package com.example.puntofacil.demo.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("admin123 => " + encoder.encode("admin123"));
        System.out.println("cajera123 => " + encoder.encode("cajera123"));
        System.out.println("vendedor123 => " + encoder.encode("vendedor123"));
    }
}
