package com.taurustex.api.utils;
import java.security.SecureRandom;
import java.util.stream.Collectors;

public class GeneratePasswordUtil {


    public static String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789&#!?$%@";
        SecureRandom random = new SecureRandom();
        return random.ints(8, 0, chars.length())
                .mapToObj(chars::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }
}
