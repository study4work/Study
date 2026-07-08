package leetCode;

public class _35 {

    public static void main(String[] args) {
        int[] nums = {1,3,5,6};
        searchInsert(nums, 5);
    }

    public static int searchInsert(int[] nums, int target) {
        int leftPointer = 0, rightPointer = nums.length - 1;
        
        while (leftPointer <= rightPointer) {
            int middlePointer = leftPointer + (rightPointer - leftPointer) / 2;
            if (nums[middlePointer] == target) 
                return middlePointer;
            if (target < nums[middlePointer])
                rightPointer = middlePointer - 1;
            else leftPointer = middlePointer + 1;
        }
        return leftPointer;
    }
}
