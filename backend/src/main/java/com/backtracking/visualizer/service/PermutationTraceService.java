package com.backtracking.visualizer.service;

import com.backtracking.visualizer.dto.TraceStep;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class PermutationTraceService {
    private List<TraceStep> trace;
    private int stepCounter;

    public List<TraceStep> generatePermutationTrace(int[] nums) {
        trace = new ArrayList<>();
        stepCounter = 1;
        permute(nums, 0);
        return trace;
    }

    private void permute(int[] nums, int start) {
        // Record entering recursion
        trace.add(new TraceStep(
            stepCounter++,
            "permute",
            Map.of("nums", Arrays.copyOf(nums, nums.length), "start", start),
            -1,
            "recurse",
            "Entering recursion with start=" + start + ", nums=" + Arrays.toString(nums)
        ));

        if (start == nums.length - 1) {
            // Record base case
            trace.add(new TraceStep(
                stepCounter++,
                "permute",
                Map.of("nums", Arrays.copyOf(nums, nums.length), "start", start),
                -1,
                "base_case",
                "Base case reached: " + Arrays.toString(nums)
            ));
            return;
        }

        for (int i = start; i < nums.length; i++) {
            // Record swap
            trace.add(new TraceStep(
                stepCounter++,
                "permute",
                Map.of("nums", Arrays.copyOf(nums, nums.length), "start", start, "i", i),
                -1,
                "swap",
                "Swapping nums[" + start + "] and nums[" + i + "]"
            ));
            swap(nums, start, i);

            permute(nums, start + 1);

            // Record backtrack (swap back)
            trace.add(new TraceStep(
                stepCounter++,
                "permute",
                Map.of("nums", Arrays.copyOf(nums, nums.length), "start", start, "i", i),
                -1,
                "backtrack",
                "Backtracking (swapping back) nums[" + start + "] and nums[" + i + "]"
            ));
            swap(nums, start, i);
        }
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
} 