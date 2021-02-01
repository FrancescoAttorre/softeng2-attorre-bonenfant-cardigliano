package it.polimi.se2.clup.building;

import it.polimi.se2.clup.data.BuildingDataAccess;
import it.polimi.se2.clup.data.entities.Building;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class AccessCodeManager {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int CODELENGTH = 16;
    private static final char[] chars = {
            'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
            '0','1','2','3','4','5','6','7','8','9',
            'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};

    static BuildingDataAccess dataAccess = new BuildingDataAccess();

    static String generateAccessCode(){
        StringBuilder code = new StringBuilder();

        int randomIndex;
        for (int i=0; i < CODELENGTH; i++){
            randomIndex = secureRandom.nextInt(chars.length);
            code.append(chars[randomIndex]);
        }
        if(checkAccessCodeAvailability(code.toString()))
            return code.toString();
        else
            return null;
    }

    private static boolean checkAccessCodeAvailability(String code){

        List<String> accessCodes = new ArrayList<>();

        for (Building b : dataAccess.retrieveBuildings()){
            accessCodes.add(b.getAccessCode());
        }

        return accessCodes.contains(code);

    }

}
