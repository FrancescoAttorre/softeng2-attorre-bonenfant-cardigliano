package it.polimi.se2.clup.auth;


import java.security.SecureRandom;

public abstract class AccessCode {

    private static final int CODELENGTH = 16;
    private static final char[] chars = {
            'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
            '0','1','2','3','4','5','6','7','8','9',
            'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};


    /**
     * Creates an alphanumeric sequence
     * @return
     */
    public static String generate() {
        StringBuilder code = new StringBuilder();
        SecureRandom sr = new SecureRandom();

        int randomIndex;
        for (int i=0; i < CODELENGTH; i++){
            randomIndex = sr.nextInt(chars.length);
            code.append(chars[randomIndex]);
        }

        return code.toString();
    }
}
