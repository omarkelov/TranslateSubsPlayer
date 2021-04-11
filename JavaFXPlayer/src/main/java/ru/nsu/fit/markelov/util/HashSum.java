package ru.nsu.fit.markelov.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashSum {

    public static String md5(String message) {
        if (message == null) {
            return null;
        }

        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        messageDigest.update(message.getBytes());

        return toHex(messageDigest.digest());
    }

    private static String toHex(byte[] bytes) {
        return String.format("%0" + (bytes.length << 1) + "X", new BigInteger(1, bytes));
    }
}
