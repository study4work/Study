package leetCode;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class _20 {
    public static void main(String[] args) {
        String s = "(]";
        isValid(s);
    }

    public static boolean isValid(String s) {
        if (s.length() == 1) return false;
        Deque<Character> queue = new LinkedList<>();
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (i == 0 && isClose(c)) return false;
            
            if (isClose(c) && !queue.isEmpty()) {
                if (!matchClosed(queue.removeLast(), c))
                    return false;
            } else queue.add(c);
        }

        if (!queue.isEmpty()) return false;
        return true;
    }

    public static boolean isClose(char close) {
        return close == ')' || close == '}' || close == ']';
    }

    public static boolean matchClosed(char open, char close) {
        return switch (open) {
            case '(' -> close == ')';
            case '{' -> close == '}';
            case '[' -> close == ']';
            default -> false;
        };
    }
}
