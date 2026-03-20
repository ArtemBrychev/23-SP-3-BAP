package org.example.services;

public final class Base62Codec {

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = ALPHABET.length();

    private Base62Codec() {}

    public static String encode(long number) {
        if (number < 0) {
            throw new IllegalArgumentException("number must be positive");
        }

        if (number == 0) {
            return String.valueOf(ALPHABET.charAt(0));
        }

        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            sb.append(ALPHABET.charAt(remainder));
            number /= BASE;
        }

        return sb.reverse().toString();
    }

    public static long decode(String str) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("string is empty");
        }

        long result = 0;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            int value = ALPHABET.indexOf(c);

            if (value == -1) {
                throw new IllegalArgumentException("invalid character: " + c);
            }

            result = result * BASE + value;
        }

        return result;
    }
}