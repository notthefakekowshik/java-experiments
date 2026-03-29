package com.kowshik.DailyCodingProblems;

import java.util.Arrays;

/*
    A builder is looking to build a row of N houses that can be of K different colors. He has a goal of minimizing cost while ensuring that no two neighboring houses are of the same color.

    Given an N by K matrix where the nth row and kth column represents the cost to build the nth house with kth color, return the minimum cost which achieves this goal.

*/

/*
My notes : 
1. This is very similar to ninja training seen in TUF DP series.

2. Greedy wont work here
10, 9, 11
12, 1, 13
14, 15, 16


3. DP only.
*/
class Solution {
    /*
     * TC : N * K * K
     */
    public int bruteForceMinCost(int[][] costs) {
        if (costs == null || costs.length == 0)
            return 0;

        int n = costs.length; // Number of houses
        int k = costs[0].length; // Number of colors

        // Iterate through every house starting from the second one (index 1)
        for (int i = 1; i < n; i++) {

            // For the current house 'i', try every color 'j'
            for (int j = 0; j < k; j++) {

                int minPrevCost = Integer.MAX_VALUE;

                // THE BOTTLENECK LOOP
                // We look at the entire previous row to find the best compatible cost
                for (int prevColor = 0; prevColor < k; prevColor++) {

                    // Constraint: Neighboring houses cannot have the same color
                    if (prevColor != j) {
                        minPrevCost = Math.min(minPrevCost, costs[i - 1][prevColor]);
                    }
                }

                // Add the minimum valid previous cost to the current cell
                costs[i][j] += minPrevCost;
            }
        }

        // The answer is the minimum value in the last row
        int finalMin = Integer.MAX_VALUE;
        for (int j = 0; j < k; j++) {
            finalMin = Math.min(finalMin, costs[n - 1][j]);
        }

        return finalMin;
    }

    public int minCost(int[][] costs) {
        if (costs == null || costs.length == 0)
            return 0;

        int n = costs.length; // Number of houses
        int k = costs[0].length; // Number of colors

        // EDGE CASE: If there are houses but no colors to paint them? Impossible.
        // Or if N > 1 and K = 1, we can't paint adjacent houses differently.
        // However, usually K >= 1. If N>1 and K=1, the problem is unsolvable
        // unless we assume "cost" is infinity. But let's stick to standard logic.

        // Step 1: Initialize min1, min2, and min1Index for the "previous" row.
        // We can treat the "0-th" imaginary row as having 0 cost.
        int prevMin1 = 0;
        int prevMin2 = 0;
        int prevMin1Index = -1;

        // Iterate through every house
        for (int i = 0; i < n; i++) {

            // Current row's minimums
            int currMin1 = Integer.MAX_VALUE;
            int currMin2 = Integer.MAX_VALUE;
            int currMin1Index = -1;

            // Iterate through every color
            for (int j = 0; j < k; j++) {

                // DECISION: Which previous cost do we add?
                // If the current color 'j' is NOT the same color as the previous min,
                // we can safely take the cheapest path (prevMin1).
                // Otherwise, we must take the second cheapest path (prevMin2).
                int cost = costs[i][j] + (j != prevMin1Index ? prevMin1 : prevMin2);

                // Update current row's min1 and min2
                if (cost < currMin1) {
                    currMin2 = currMin1; // Demote the old min1 to min2
                    currMin1 = cost; // Set new min1
                    currMin1Index = j; // Record the color index
                } else if (cost < currMin2) {
                    currMin2 = cost;
                }
            }

            // Move current values to previous for the next iteration
            prevMin1 = currMin1;
            prevMin2 = currMin2;
            prevMin1Index = currMin1Index;
        }

        // The answer is the cheapest cost found for the last house
        return prevMin1;
    }

    public int mySolution(int[][] costs) {
        if (costs == null || costs.length == 0)
            return 0; // Safety check

        int m = costs.length;
        int n = costs[0].length;

        int most_min_value = Integer.MAX_VALUE;
        int most_min_index = -1;
        int next_min_value = Integer.MAX_VALUE;

        for (int i = 0; i < n; i++) {
            int val = costs[0][i];
            if (val < most_min_value) {
                next_min_value = most_min_value; // Demote current min to 2nd best
                most_min_value = val; // New min
                most_min_index = i;
            } else if (val < next_min_value) {
                next_min_value = val;
            }
        }

        // MAIN LOOP: Start from Row 1
        for (int i = 1; i < m; i++) {

            // Reset current row trackers
            int curr_most_min_value = Integer.MAX_VALUE;
            int curr_most_min_index = -1;
            int curr_next_min_value = Integer.MAX_VALUE;

            for (int j = 0; j < n; j++) {
                int curr_cost = costs[i][j];

                if (j != most_min_index) {
                    curr_cost += most_min_value;
                } else {
                    curr_cost += next_min_value;
                }

                if (curr_cost < curr_most_min_value) {
                    curr_next_min_value = curr_most_min_value; // Demote old King
                    curr_most_min_value = curr_cost; // New King
                    curr_most_min_index = j;
                } else if (curr_cost < curr_next_min_value) {
                    curr_next_min_value = curr_cost; // New Prince
                }
            }

            most_min_value = curr_most_min_value;
            most_min_index = curr_most_min_index;
            next_min_value = curr_next_min_value;
        }

        return most_min_value;
    }
}

public class Medium893MinCostToPaintHouse {
    public static void main(String[] args) {
        int costs[][] = {
                { 10, 9, 11 },
                { 12, 1, 13 },
                { 14, 15, 16 }
        };

        System.out.println(new Solution().bruteForceMinCost(costs));

        int costs2[][] = {
                { 10, 9, 11 },
                { 12, 1, 13 },
                { 14, 15, 16 }
        };

        System.out.println(new Solution().minCost(costs2));
    }
}
