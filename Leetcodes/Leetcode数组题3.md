# Leetcode数组题*3
[toc]
## 66.加一
### 题目描述
- 给定一个由整数组成的非空数组所表示的非负整数，在该数的基础上加一。
- 最高位数字存放在数组的首位， 数组中每个元素只存储单个数字。
- 你可以假设除了整数 0 之外，这个整数不会以零开头。

示例：
```java
输入: [1,2,3]
输出: [1,2,4]
解释: 输入数组表示数字 123。
```
链接：[https://leetcode-cn.com/problems/plus-one](https://leetcode-cn.com/problems/plus-one)


### 思路分析
参考题解大神的巧妙解法：[Java数学解题](https://leetcode-cn.com/problems/plus-one/solution/java-shu-xue-jie-ti-by-yhhzw/)
一个数用数组存储，那么加上1之后只有两种情况
- 最后一位不是9，直接让数组的最后一位加1后返回该数组。
- 最后一位是9的情况，需要在循环中判断，因为它的前面一位也可能是9，首先让9变为0，然后前一位加1。
- 如果最高位为9，可以创建一个新的数组，长度比原数组长度大一，首位置0。
```java
    /**
     * 将一个用表示数字的数组传入，加上1之后，返回新数组
     * @param digits  传入的数组
     * @return  返回加一之后的新数组
     */
    public static int[] plusOne(int[] digits){
        int len = digits.length;
        for(int i = len-1;i>=0;i--){
            digits[i]++;
            digits[i] = digits[i]%10;
            //末位不是9，直接返回
            if(digits[i]!=0) return digits;
        }
        //最高位为9的情况，创建一个比原数组长1的数组
        int[] newDigits = new int[len+1];
        //首位置1，其余初始化为0
        newDigits[0] = 1;
        return newDigits;
    }
```

## 88.合并两个有序数组
### 题目描述

- 给定两个有序整数数组 nums1 和 nums2，将 nums2 合并到 nums1 中，使得 num1 成为一个有序数组。

**说明:**

- 初始化 nums1 和 nums2 的元素数量分别为 m 和 n。
- 你可以假设 nums1 有足够的空间（空间大小大于或等于 m + n）来保存 nums2 中的元素。

**示例：**
```java
    输入:
    nums1 =[1,2,3,0,0,0],m =3
    nums2 =[2,5,6],n =3
    输出: [1,2,2,3,5,6]
```
**链接：**[https://leetcode-cn.com/problems/merge-sorted-array](https://leetcode-cn.com/problems/merge-sorted-array)

### 思路分析
参考题解：[画解算法](https://leetcode-cn.com/problesorted-array/solution/hua-jie-suan-fa-88-he-bing-liang-ge-you-xu-shu-zu-/ms/merge-)
- 由于nums1有足够的空间撑下两个数组，可以**从后向前处理排序加合并**。
- 设置指向两个数组数字尾部的指针p1和p2，和一个指向刚好是合并数组的最后一位的指针p。
- 从后向前比较两个数组中的值，将大的放入nums1的后面，依次向前填充。
- **当nums1中没有数的时候**，将nums2全部拷贝到nums1的前面。
```java
    /**
     * 合并并排序到第一个数组
     *
     * @param nums1 传入的第一个数组
     * @param m     第一个数组的长度
     * @param nums2 传入的第二个数组
     * @param n     第二个数组的长度
     */
    public static void merge(int[] nums1, int m, int[] nums2, int n) {
        int p1 = m - 1;//指向nums1的有数字的最后一位
        int p2 = n - 1;//指向nums2的有数字的最后一位
        int p = m + n - 1;
        //从有数字的最后一位向前，一一对比，两者较大的数存到nums1的p位置，分别向前
        while (p1 >= 0 && p2 >= 0) {
            nums1[p--] = nums1[p1] > nums2[p2] ? nums1[p1--] : nums2[p2--];
        }
        //由于是存到nums1上的，所以保证最后p1无元素可指的时候，把nums2上的元素按顺序拷贝到nums1
        System.arraycopy(nums2, 0, nums1, 0, p2 + 1);
    }
```

## 167.两数之和Ⅱ-输入有序数组 

### 题目描述

- 给定一个已按照**升序排列** 的有序数组，找到两个数使得它们相加之和等于目标数。

- 函数应该返回这两个下标值 index1 和 index2，其中 index1 必须小于 index2。

**说明:**

返回的下标值（index1 和 index2）**不是从零开始的**。
你可以假设每个输入只对应唯一的答案，而且你不可以重复使用相同的元素。
**示例：**
```java
输入: numbers = [2, 7, 11, 15], target = 9
输出: [1,2]
解释: 2 与 7 之和等于目标数 9 。因此 index1 = 1, index2 = 2 。
```
链接：[https://leetcode-cn.com/problems/two-sum-ii-input-array-is-sorted](https://leetcode-cn.com/problems/two-sum-ii-input-array-is-sorted)

### 思路分析
参考题解：[双指针求解两数之和](https://leetcode-cn.com/problems/two-sum-ii-input-array-is-sorted/solution/shuang-zhi-zhen-on-shi-jian-fu-za-du-by-cyc2018/)
- 注意是已排序数组，可以利用这一点求解。
- **双指针求解**，一指针指向前，一指针指向后。
- 如果两个指针对应的和刚好是target，返回两个指针对应索引+1。
- **如果和大于target，就将大数指针向前移动**。反之，小数指针向后移动。

```java
    /**
     * 得到传入数组中两个元素和刚好是目标数字的位置，用数组返回
     * @param numbers   传入的目标数组
     * @param target    传入的目标数字
     * @return   返回两元素和为目标数字的对应位置
     */
    public static int[] twoSum(int[] numbers,int target){
        int i = 0;
        int j = numbers.length-1;
        if(numbers == null) return null;
        while(i<j){
            int sum = numbers[i]+numbers[j];
            if(sum==target) return new int[]{i+1,j+1};
            else if(sum>target){
                j--;
            }else{
                i++;
            }
        }
        return null;
    }
```