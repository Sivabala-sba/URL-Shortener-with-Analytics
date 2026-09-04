package com.example.UrlShortener.codec;

import java.math.BigInteger;

public class ShortCodeGenerator {

    private static final double PHI = (1 + Math.sqrt(5)) / 2;

    private final int codeLength;
    private final long keyspace;
    private final BigInteger modulus;
    private final BigInteger multiplier;
    private final BigInteger inverse;

    public ShortCodeGenerator(int codeLength){
        this(codeLength, 0L);
    }

    public ShortCodeGenerator(int codeLength, long multiplier){
        if(codeLength < 4 || codeLength > 10){
            throw new IllegalArgumentException("codeLength must be within 4..10 but was " + codeLength);
        }
        this.codeLength = codeLength;
        this.modulus = BigInteger.valueOf(62).pow(codeLength);
        this.keyspace = modulus.longValueExact();
        this.multiplier = multiplier == 0
                ? coprimeNear(modulus, BigInteger.valueOf((long) (keyspace / PHI)))
                : validated(BigInteger.valueOf(multiplier), modulus);
        this.inverse = this.multiplier.modInverse(modulus);
    }

    public String codeFor(long id){
        if(id < 0 || id >= keyspace){
            throw new IllegalArgumentException("id" + id + " is outside the " + codeLength + "-character keyspace of " + keyspace);
        }
        long scrambled = BigInteger.valueOf(id).multiply(multiplier).mod(modulus).longValueExact();
        return Base62Codec.encode(scrambled, codeLength);
    }

    public long idFor(String code){
        if(code == null || code.length() != codeLength){
            throw new IllegalArgumentException("code must be exactly " + codeLength + " characters");
        }
        long scrambled = Base62Codec.decode(code);
        if(scrambled >= keyspace){
            throw new IllegalArgumentException("code is outside the keyspace");
        }
        return BigInteger.valueOf(scrambled).multiply(inverse).mod(modulus).longValueExact();
    }

    public boolean couldHaveIssued(String code){
        try{
            idFor(code);
            return true;
        }catch (IllegalArgumentException e){
            return false;
        }
    }

    public int codelength(){
        return codeLength;
    }

    public long keyspace(){
        return keyspace;
    }

    public long multiplier(){
        return multiplier.longValueExact();
    }

    private static BigInteger coprimeNear(BigInteger modulus, BigInteger start){
        BigInteger candidate = start.testBit(0) ? start : start.add(BigInteger.ONE);
        while (!candidate.gcd(modulus).equals(BigInteger.ONE)){
            candidate = candidate.add(BigInteger.TWO);
        }
        return candidate;
    }

    private static BigInteger validated(BigInteger multiplier, BigInteger modulus){
        if(multiplier.signum() <= 0 || multiplier.compareTo(modulus) >= 0){
            throw new IllegalArgumentException("multiplier must be within 1..modulus-1");
        }
        if(!multiplier.gcd(modulus).equals(BigInteger.ONE)){
            throw new IllegalArgumentException(
                    "multiplier " + multiplier + " is not coprime with the keyspace, so the mapping would not be one-to-one"
            );
        }
        return multiplier;
    }
}
