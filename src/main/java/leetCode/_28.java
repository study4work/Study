package leetCode;

public class _28 {

    public static void main(String[] args) {
        String haystack = "leetcode";
        String needle = "leeto";
        strStr(haystack, needle);
    }

    public static int strStr(String haystack, String needle) {
       return haystack.indexOf(needle);
    }
}
