package it.polimi.se2.clup.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Class used to hash passwords.
 */
class Hash {

    private static final String algorithm = "SHA-512";
    private static final String randomNumberGenerator = "SHA1PRNG";
    private static final int saltLength = 16;

    private static String hash(String password, byte[] salt) {
        String generatedPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);

            md.update(salt);

            byte[] bytes = md.digest(password.getBytes());

            return Base64.getEncoder().encodeToString(bytes) + ":" + Base64.getEncoder().encodeToString(salt);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String simpleHash(String code) {
        String generatedHash;

        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = md.digest(code.getBytes());

            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String hash(String password) {
        return hash(password, getSalt());
    }

    private static byte[] getSalt() {
        SecureRandom sr;
        byte[] salt = new byte[saltLength];

        try {
            sr = SecureRandom.getInstance(randomNumberGenerator);

            sr.nextBytes(salt);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return salt;
    }

    public static boolean verifyHash(String password, String hashWithSalt) {

        byte[] salt = Base64.getDecoder().decode(hashWithSalt.split(":")[1]);

        return hash(password, salt).equals(hashWithSalt);
    }

}