<<<<<<< HEAD
[toc]

# Leetcode动态规划【简单题】

**动态规划**（Dynamic programming，简称DP），是一种把原问题分解为相对简单的子问题的方式求解复杂问题的方法。动态规划相较于递归，拥有更少的计算量。

## 53. 最大子序和

### 题目描述

给定一个整数数组 `nums` ，找到一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。

### 思路分析

- 遍历数组，定义当前最大连续子序列的和为sum，返回的结果为target。
- 如果`sum>0`，表示sum对结果有增益效果，则保留sum并加上当前遍历数字。
- 反之，如果`sum<=0`，表示无增益，那么直接舍弃，让sum更新为当前遍历数字。
- 比较sum和target的大小，将最大值置为target，结束遍历。

```java
    public static int maxSubArray(int[] nums){
        int sum = 0;
        int target = nums[0];
        for(int num:nums){
            //sum>0 增益效果，保留sum并加上当前遍历数字
            if(sum>0){
                sum+=num;
            //sum<=0 无增益效果，直接舍弃，并将sum更新为当前遍历数字
            }else{
                sum = num;
            }
            //比较sum和target的大小，将最大值设为target
            target = Math.max(target, sum);
        }

        return target;
    }
```

### 复杂度分析

- 时间复杂度：O(n) ：只遍历了一次数组。
- 空间复杂度：O(1)：只用了常熟的空间。



## 70.爬楼梯

### 题目描述

假设你正在爬楼梯。需要 *n* 阶你才能到达楼顶。

每次你可以爬 1 或 2 个台阶。你有多少种不同的方法可以爬到楼顶呢？

**注意：**给定 *n* 是一个正整数。

### 思路分析

- 我们可以想，每次只能爬一个或者两个台阶意味着，假设现在有i阶楼梯，想要达到有两种情况：
- 在第（i-1）阶后再爬1阶，或者在第（i-2）阶后再爬2阶。
- 那么到达第i阶的方法总数就是到第（i-1）阶和第（i-2）阶方法数的和。

```java
    public static int climbStairs(int n) {
        if (n == 1) return 1;
        int[] dp = new int[n + 1];
        dp[1] = 1;
        dp[2] = 2;
        for (int i = 3; i < n + 1; i++) {
            //
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }
```

### 复杂度分析

- 时间复杂度：O(n) ，for循环到达n。
- 空间复杂度：O(n)，创建的dp数组用了n的空间。

## 121.买卖股票的最佳时机

### 题目描述

给定一个数组，它的第 i 个元素是一支给定股票第 i 天的价格。

如果你最多只允许完成一笔交易（即买入和卖出一支股票），设计一个算法来计算你所能获取的最大利润。

注意你不能在买入股票前卖出股票。

### 思路分析

假设当前在第i天，令minPrice存储前i-1天的最低价格，令maxProfit存储前i-1天的最大收益，那么在第i天的情况：

- 在第i天卖出，说明如果想要获得最大收益，一定要在前i-1天的最低价格时买入，此时最大收益为`prices[i]-minPrice`。
- 不在第i天卖出，说明第i天的最大收益就是前i-1天的最大收益。
- 可得状态转移方程：第i天的最大收益=max（在第i天卖出所得的收益，前i-1天的最大收益）。

```java
    public static int maxProfit(int[] prices){
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;
        for(int i = 0;i<prices.length;i++){
            minPrice = Math.min(minPrice,prices[i]);
            maxProfit = Math.max(prices[i]-minPrice,maxProfit);
        }
        return maxProfit;

    }
```

### 复杂度分析

- 时间复杂度：O(n)，只需遍历一次
- 空间复杂度：O(1),只使用了两个变量

## 303.区域和检索-数组不可变

### 题目描述

给定一个整数数组  *nums*，求出数组从索引 *i* 到 *j* (*i* ≤ *j*) 范围内元素的总和，包含 *i, j* 两点。

**说明:**

1. 你可以假设数组不可变。
2. 会多次调用 *sumRange* 方法。

### 思路分析

- 如果使用暴力求解，肯定会超时，因为题目说明：会调用多次sumRange方法，每次都会消耗O(n)的时间。
- 那么，如何优化呢？可以预先计算从0到k的累计和，存入一个新的数组。这种记忆化存储的方式，以便于在查询时直接查表。
- 为了避免额外的条件检查，可以让新数组的第一个元素为0，存放的和可以从索引1开始。
- 举个例子：原数组nums:[1,3,5,7,9] --> 新数组sums:[0,1,4,9,16,25]

```java
public class NumArray {
    private int[] sums;
    public NumArray(int[] nums){
        sums = new int[nums.length+1];
        for(int i = 0;i<nums.length;i++){
            //创建从索引为1开始的数组，数组中存储的是原数组第一位到索引前一位的总和            
            sums[i+1] = sums[i]+nums[i];
        }
    }
    public int sumRange(int i,int j){
        return sums[j+1]-sums[i];
    }
    /**
     * Your NumArray object will be instantiated and called as such:
     * NumArray obj = new NumArray(nums);
     * int param_1 = obj.sumRange(i,j);
     */
}
```



### 复杂度分析

- 时间复杂度：预计算时间为O(n)，但是实际查询只要O(1)。

- 空间复杂度：创建了新数组的空间，O(n)。



参考链接：

[画解算法](https://leetcode-cn.com/problems/maximum-subarray/solution/hua-jie-suan-fa-53-zui-da-zi-xu-he-by-guanpengchn/)

[动态规划套路详解](https://leetcode-cn.com/problems/coin-change/solution/dong-tai-gui-hua-tao-lu-xiang-jie-by-wei-lai-bu-ke/)

=======
[toc]

# Leetcode动态规划【简单题】

**动态规划**（Dynamic programming，简称DP），是一种把原问题分解为相对简单的子问题的方式求解复杂问题的方法。动态规划相较于递归，拥有更少的计算量。

## 53. 最大子序和

### 题目描述

给定一个整数数组 `nums` ，找到一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。

### 思路分析

- 遍历数组，定义当前最大连续子序列的和为sum，返回的结果为target。
- 如果`sum>0`，表示sum对结果有增益效果，则保留sum并加上当前遍历数字。
- 反之，如果`sum<=0`，表示无增益，那么直接舍弃，让sum更新为当前遍历数字。
- 比较sum和target的大小，将最大值置为target，结束遍历。

```java
    public static int maxSubArray(int[] nums){
        int sum = 0;
        int target = nums[0];
        for(int num:nums){
            //sum>0 增益效果，保留sum并加上当前遍历数字
            if(sum>0){
                sum+=num;
            //sum<=0 无增益效果，直接舍弃，并将sum更新为当前遍历数字
            }else{
                sum = num;
            }
            //比较sum和target的大小，将最大值设为target
            target = Math.max(target, sum);
        }

        return target;
    }
```

### 复杂度分析

- 时间复杂度：O(n) ：只遍历了一次数组。
- 空间复杂度：O(1)：只用了常熟的空间。



## 70.爬楼梯

### 题目描述

假设你正在爬楼梯。需要 *n* 阶你才能到达楼顶。

每次你可以爬 1 或 2 个台阶。你有多少种不同的方法可以爬到楼顶呢？

**注意：**给定 *n* 是一个正整数。

### 思路分析

- 我们可以想，每次只能爬一个或者两个台阶意味着，假设现在有i阶楼梯，想要达到有两种情况：
- 在第（i-1）阶后再爬1阶，或者在第（i-2）阶后再爬2阶。
- 那么到达第i阶的方法总数就是到第（i-1）阶和第（i-2）阶方法数的和。

```java
    public static int climbStairs(int n) {
        if (n == 1) return 1;
        int[] dp = new int[n + 1];
        dp[1] = 1;
        dp[2] = 2;
        for (int i = 3; i < n + 1; i++) {
            //
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }
```

### 复杂度分析

- 时间复杂度：O(n) ，for循环到达n。
- 空间复杂度：O(n)，创建的dp数组用了n的空间。

## 121.买卖股票的最佳时机

### 题目描述

给定一个数组，它的第 i 个元素是一支给定股票第 i 天的价格。

如果你最多只允许完成一笔交易（即买入和卖出一支股票），设计一个算法来计算你所能获取的最大利润。

注意你不能在买入股票前卖出股票。

### 思路分析

假设当前在第i天，令minPrice存储前i-1天的最低价格，令maxProfit存储前i-1天的最大收益，那么在第i天的情况：

- 在第i天卖出，说明如果想要获得最大收益，一定要在前i-1天的最低价格时买入，此时最大收益为`prices[i]-minPrice`。
- 不在第i天卖出，说明第i天的最大收益就是前i-1天的最大收益。
- 可得状态转移方程：第i天的最大收益=max（在第i天卖出所得的收益，前i-1天的最大收益）。

```java
    public static int maxProfit(int[] prices){
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;
        for(int i = 0;i<prices.length;i++){
            minPrice = Math.min(minPrice,prices[i]);
            maxProfit = Math.max(prices[i]-minPrice,maxProfit);
        }
        return maxProfit;

    }
```

### 复杂度分析

- 时间复杂度：O(n)，只需遍历一次
- 空间复杂度：O(1),只使用了两个变量

## 303.区域和检索-数组不可变

### 题目描述

给定一个整数数组  *nums*，求出数组从索引 *i* 到 *j* (*i* ≤ *j*) 范围内元素的总和，包含 *i, j* 两点。

**说明:**

1. 你可以假设数组不可变。
2. 会多次调用 *sumRange* 方法。

### 思路分析

- 如果使用暴力求解，肯定会超时，因为题目说明：会调用多次sumRange方法，每次都会消耗O(n)的时间。
- 那么，如何优化呢？可以预先计算从0到k的累计和，存入一个新的数组。这种记忆化存储的方式，以便于在查询时直接查表。
- 为了避免额外的条件检查，可以让新数组的第一个元素为0，存放的和可以从索引1开始。
- 举个例子：原数组nums:[1,3,5,7,9] --> 新数组sums:[0,1,4,9,16,25]

```java
public class NumArray {
    private int[] sums;
    public NumArray(int[] nums){
        sums = new int[nums.length+1];
        for(int i = 0;i<nums.length;i++){
            //创建从索引为1开始的数组，数组中存储的是原数组第一位到索引前一位的总和            
            sums[i+1] = sums[i]+nums[i];
        }
    }
    public int sumRange(int i,int j){
        return sums[j+1]-sums[i];
    }
    /**
     * Your NumArray object will be instantiated and called as such:
     * NumArray obj = new NumArray(nums);
     * int param_1 = obj.sumRange(i,j);
     */
}
```



### 复杂度分析

- 时间复杂度：预计算时间为O(n)，但是实际查询只要O(1)。

- 空间复杂度：创建了新数组的空间，O(n)。



参考链接：

[画解算法](https://leetcode-cn.com/problems/maximum-subarray/solution/hua-jie-suan-fa-53-zui-da-zi-xu-he-by-guanpengchn/)

[动态规划套路详解](https://leetcode-cn.com/problems/coin-change/solution/dong-tai-gui-hua-tao-lu-xiang-jie-by-wei-lai-bu-ke/)

>>>>>>> fc85d690c29d36fea84eea11ae981a1d2cc98112
[官方题解爬楼梯](https://leetcode-cn.com/problems/climbing-stairs/solution/pa-lou-ti-by-leetcode/)