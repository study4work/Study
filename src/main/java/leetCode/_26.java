package leetCode;

public class _26 {
    public static void main(String[] args) {
        int [] nums = {0,0,1,1,1,2,2,3,3,4};
        removeDuplicates(nums);
    }

    public static int removeDuplicates(int[] nums) {
        int result = 1; // Указывает на позицию для записи следующего уникального элемента
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != nums[i - 1]) {
                nums[result] = nums[i]; // Переносим уникальный элемент вперед
                result++;
            }
        }
       return result;
    }
}
