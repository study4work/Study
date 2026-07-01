package leetCode;

public class _14 {

    public static void main(String[] args) {
        String[] strings = {"blower", "trixsa", "flow", "trix", "feight"};
        
        longestCommonPrefix1(strings);
    }

    public static String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) return "";

        String prefix = strs[0];
        for(int index=1;index<strs.length;index++){
            while(strs[index].indexOf(prefix) != 0){
                prefix=prefix.substring(0,prefix.length()-1);
            }
        }
        return prefix;
    }

    public static String longestCommonPrefix1(String[] strs) {
        if (strs.length == 0) {
            return "";
        }
        if (strs.length == 1) {
            return strs[0];
        }

        StringBuilder result = new StringBuilder();
        int charIndex = 0;

        for (int j = 0; j < strs[0].length(); j++) {
            char comparisonChar = strs[0].charAt(j);
            for (int i = 0; i < strs.length; i++) {
                String currentStr = strs[i];
                if (currentStr.length() - 1 >= charIndex && comparisonChar == currentStr.charAt(charIndex)) {
                    if (i == strs.length - 1) 
                        result.append(comparisonChar);
                } else {
                    return result.toString();
                }
            }
            charIndex++;
        }
        return result.toString();
    }
}
