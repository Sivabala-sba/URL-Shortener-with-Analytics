package com.example.UrlShortener.codec;

import java.util.Arrays;

public class Base62Codec {

    private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    private static final int BASE = ALPHABET.length;
    private static final int MAX_DIGITS = 11;

    private static final int[] DIGIT_VALUES = new int[128];

    static{
        Arrays.fill(DIGIT_VALUES, -1);
        for(int i = 0; i < ALPHABET.length; i++){
            DIGIT_VALUES[ALPHABET[i]] = i;
        }
    }

    private Base62Codec(){
    }

    public static String encode(long value, int minLength){
        if(value < 0){
            throw new IllegalArgumentException("value must be non-negative but was " + value);
        }
        if(minLength < 1 || minLength > MAX_DIGITS){
            throw new IllegalArgumentException("minLength must be within 1.." + MAX_DIGITS);
        }

        char[] buffer = new char[MAX_DIGITS];
        int position = buffer.length;
        long remaining = value;
        do{
            buffer[--position] = ALPHABET[(int) (remaining % BASE)];
            remaining /= BASE;
        } while(remaining > 0);

        while(buffer.length - position < minLength){
            buffer[--position] = ALPHABET[0];
        }
        return new String(buffer, position, buffer.length - position);
    }

    public static String encode(long value){
        return encode(value, 1);
    }

    public static long decode(String text){
        if(text == null || text.isEmpty()){
            throw new IllegalArgumentException("code must not be empty");
        }
        if(text.length() > MAX_DIGITS){
            throw new IllegalArgumentException("code is longer than the 64-bit keyspace allows");
        }

        long value = 0;
        for(int i = 0; i < text.length(); i++){
            int digit = digitValue(text.charAt(i));
            if(digit < 0){
                throw new IllegalArgumentException("illegal base62 character '" + text.charAt(i) + "' at index " + i);
            }
            value = value * BASE + digit;
            if(value < 0){
                throw new IllegalArgumentException("code overflows a signed 64-bit value");
            }
        }
        return value;
    }

    public static boolean isBase62(String text){
        if(text == null || text.isEmpty()){
            return false;
        }
        for(int i = 0; i < text.length(); i++){
            if(digitValue(text.charAt(i)) < 0){
                return false;
            }
        }
        return true;
    }

    private static int digitValue(char c){
        return c < DIGIT_VALUES.length ? DIGIT_VALUES[c] : -1;
    }
}
