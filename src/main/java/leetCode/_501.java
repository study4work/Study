package leetCode;

import java.util.*;

/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */
class _501 {
    public static void main(String[] args) {
     TreeNode root = new TreeNode(1, null, new TreeNode(2, new TreeNode(2, null, null), null));
        findMode2(root);
    }
    
    // обход в ширину
    public static int[] findMode(TreeNode root) {
        if (root == null) return new int[0];

        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        Map<Integer, Integer> map = new HashMap<>();

        while (!q.isEmpty()) {
            TreeNode poll = q.poll();
            int curr = poll.val;

            if (poll.left != null) 
                q.add(poll.left);
            
            if (poll.right != null) 
                q.add(poll.right);
            
            map.put(curr, map.getOrDefault(curr, 0) + 1);
        }

        Integer i = map.values().stream().max(Integer::compareTo).get();
        List<Integer> res = new ArrayList<>();
        for (Integer integer : map.keySet()) {
            if (map.get(integer).equals(i))
                res.add(integer);
        }
        
        return res.stream().mapToInt(Integer::intValue).toArray();
    }

    //dfs обход в глубину
    public static int[] findMode2(TreeNode root) {
        if (root == null) return new int[0];
        
        Map<Integer, Integer> map = new HashMap<>();
        dfs(root, map);

        Integer i = map.values().stream().max(Integer::compareTo).get();
        List<Integer> res = new ArrayList<>();
        for (Integer integer : map.keySet()) {
            if (map.get(integer).equals(i))
                res.add(integer);
        }

        return res.stream().mapToInt(Integer::intValue).toArray();
    }
    
    public static void dfs(TreeNode root, Map<Integer, Integer> map) {
        if (root == null) return;
        
        map.put(root.val, map.getOrDefault(root.val, 0) + 1);
        dfs(root.left, map);
        dfs(root.right, map);
    }

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }
}
