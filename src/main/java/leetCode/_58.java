package leetCode;

public class _58 {
    public static void main(String[] args) {
         String s = "   fly me   to   the moon  ";
         lengthOfLastWord(s);
    }
    
    public static int lengthOfLastWord(String s) {
        String trimmed = s.trim();
        String[] split = trimmed.split(" ");
        return split[split.length - 1].length();
    }
}
