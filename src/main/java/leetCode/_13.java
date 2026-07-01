package leetCode;

import java.util.*;

public class _13 {

    public static void main(String[] args) {
        String s = "III";
        romanToInt(s);
    }

    public static int romanToInt(String s) {
        if (s == null || s.isEmpty()) return 0;
        Map<String, Integer> storage = Map.of(
                "I", 1,
                "V", 5,
                "X", 10,
                "L", 50,
                "C", 100,
                "D", 500,
                "M", 1000
        );


        ListIterator<String> romanDigitIterator = Arrays.stream(s.split("")).toList().listIterator();
        int result = 0;

        while (romanDigitIterator.hasNext()) {
            String romanDigit = romanDigitIterator.next();
            if (romanDigitIterator.hasNext()) {
                String nextRomanDigit = romanDigitIterator.next();
                if (romanDigit.equals("I") && (nextRomanDigit.equals("V") || nextRomanDigit.equals("X"))
                        || romanDigit.equals("X") && (nextRomanDigit.equals("L") || nextRomanDigit.equals("C"))
                        || romanDigit.equals("C") && (nextRomanDigit.equals("D") || nextRomanDigit.equals("M")))
                    result = result + storage.get(nextRomanDigit) - storage.get(romanDigit);
                else {
                    result = result + storage.get(romanDigit);
                    romanDigitIterator.previous();
                }
            } else result = result + storage.get(romanDigit);
        }

        return result;
    }
}

/*
class Solution {
    public int romanToInt(String s) {
        int result = 0;
        int prevValue = 0;
        
        for (int i = s.length() - 1; i >= 0; i--) {
            int currentValue = getValue(s.charAt(i));
            
            if (currentValue < prevValue) {
                result -= currentValue;
            } else {
                result += currentValue;
            }
            prevValue = currentValue;
        }
        
        return result;
    }
    
    private int getValue(char c) {
        switch (c) {
            case 'I': return 1;
            case 'V': return 5;
            case 'X': return 10;
            case 'L': return 50;
            case 'C': return 100;
            case 'D': return 500;
            case 'M': return 1000;
            default: return 0;
        }
    }
}
 */
