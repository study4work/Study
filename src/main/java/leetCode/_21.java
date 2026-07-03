package leetCode;

import java.util.ArrayList;
import java.util.List;

public class _21 {

    public static void main(String[] args) {
        ListNode node = new ListNode(5);
        ListNode node1 = new ListNode(1, new ListNode(2, new ListNode(4)));
        
        mergeTwoLists(node, node1);
    }
    
    public static ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        if (list1 == null && list2 != null) return list2;
        if (list2 == null && list1 != null) return list1;
        if (list1 == null && list2 == null) return null;

        List<Integer> digitStorage = new ArrayList<>();
        digitStorage.add(list1.val);
        digitStorage.add(list2.val);
        
        while (list1.next != null || list2.next != null) {
            ListNode next1 = list1.next;
            ListNode next2 = list2.next;
            
            if (list1.next != null) {
                digitStorage.add(next1.val);
                list1 = list1.next;
            }
            if (list2.next != null) {
                digitStorage.add(next2.val);
                list2 = list2.next;
            }
        }

        List<ListNode> list = digitStorage.stream().sorted().map(ListNode::new).toList();
        ListNode head = null;
        for (int i = 0; i < list.size() - 1; i++) {
            if (i == 0) {
                head = list.get(i);
                list.get(i).next = list.get(i+1);
            } else {
                list.get(i).next = list.get(i + 1);
            }
        }
        
        return head;
    }


//    public static ListNode mergeTwoLists(ListNode list1, ListNode list2) {
//
//        ListNode dummy = new ListNode(-1);
//        ListNode current = dummy;
//
//        while (list1 != null && list2 != null) {
//
//            if (list1.val <= list2.val) {
//                current.next = list1;
//                list1 = list1.next;
//            } else {
//                current.next = list2;
//                list2 = list2.next;
//            }
//
//            current = current.next;
//        }
//
//        if (list1 != null) {
//            current.next = list1;
//        } else {
//            current.next = list2;
//        }
//
//        return dummy.next;
//    }

    public static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }
}
