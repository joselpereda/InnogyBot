package com.yukinohara.innogybot.utils;

import org.apache.commons.lang3.text.StrBuilder;

import java.util.Random;

/**
 * Created by YukiNoHara on 6/14/2017.
 */

public class DataUtils {
    public static String getDefaultPassword(){
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ch++) {
            tmp.append(ch);
        }
        for (char ch = 'a'; ch <= 'z'; ch++) {
            tmp.append(ch);
        }
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            tmp.append(ch);
        }
        StringBuilder s = new StringBuilder();
        int min = 0;
        int max = tmp.length();
        for (int i = 0; i < 8; i++){
            Random random = new Random();
            int rand = random.nextInt(max + min - 1) - min;
            s.append(tmp.charAt(rand));
        }
        return s.toString();
    }

    public static String convertDefaultPasswordToFinishedPassword(String password){
        String newPassword = "";
        for (int i = 0; i < password.length(); i++){
            if (Character.isUpperCase(password.charAt(i))){
                newPassword += "großes " + password.charAt(i) + ", ";
            } else {
                newPassword += password.charAt(i) + ", ";
            }
        }
        return newPassword.substring(0, newPassword.length() - 2);
    }

    public static boolean isCorrect(String pass){
        StringBuilder mUpperStringBuilder = new StringBuilder();
        for (char ch = 'A'; ch <= 'Z'; ch++){
            mUpperStringBuilder.append(ch);
        }
        StringBuilder mLowerStringBuilder = new StringBuilder();
        for (char ch = 'a'; ch <= 'z'; ch++){
            mLowerStringBuilder.append(ch);
        }
        StringBuilder mNumberStringBuilder = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ch++){
            mNumberStringBuilder.append(ch);
        }
        String mUpperString = mUpperStringBuilder.toString();
        String mLowerString = mLowerStringBuilder.toString();
        String mNumberString = mNumberStringBuilder.toString();
        boolean isHasUpperCharacter = false;
        boolean isHasLowerCharacter = false;
        boolean isHasNumberCharacter = false;
        for (int i = 0; i < pass.length(); i++){
            if (mUpperString.indexOf(pass.charAt(i)) != -1){
                isHasUpperCharacter = true;
            }
            if (mLowerString.indexOf(pass.charAt(i)) != -1){
                isHasLowerCharacter = true;
            }
            if (mNumberString.indexOf(pass.charAt(i)) != -1){
                isHasNumberCharacter = true;
            }
        }
        if (isHasUpperCharacter && isHasLowerCharacter && isHasNumberCharacter){
            return true;
        } else {
            return false;
        }
    }

    public static String getConfigNumberStringFromResponse(String output){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < output.length(); i++){
            if (Character.isDigit(output.charAt(i))){
                builder.append(output.charAt(i) + ", ");
            } else {
                builder.append(output.charAt(i));
            }
        }
        return builder.toString();
    }

    public static String getNumberFromString(String input){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < input.length(); i++){
            if (Character.isDigit(input.charAt(i))){
                builder.append(input.charAt(i));
            }
        }
        return builder.toString();
    }

    public static String convertResponseFromDataByCondition(String status, String cost){
        String result;
        if (status.equalsIgnoreCase("GenKoSt")){
            result = " Ihre Bestellung liegt noch bei " + cost + " zur Kostenfreigabe vor";
        } else if (status.equalsIgnoreCase("GenIM")){
            result = "Ihre Bestellung liegt noch zur Genehmigung bei  ihrem IM vor";
        } else if (status.equalsIgnoreCase("Genehmigt")){
            result = "Ihre Bestellung wurde genehmigt und befindet sich in der Bearbeitung";
        } else {
            result = "Ihre Bestellung wurde nicht freigegeben";
        }
        return result;

    }

}
