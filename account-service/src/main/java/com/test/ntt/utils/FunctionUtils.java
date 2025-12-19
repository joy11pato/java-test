package com.test.ntt.utils;

import java.security.SecureRandom;

public class FunctionUtils {

    public static String generateRandomNumber() {
        SecureRandom secureRandom = new SecureRandom();

        int numero = secureRandom.nextInt(1000000);

        return String.format("%06d", numero);
    }

}
