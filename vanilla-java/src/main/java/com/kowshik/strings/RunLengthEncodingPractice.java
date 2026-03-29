package com.kowshik.strings;

public class RunLengthEncodingPractice {

    public static void main(String[] args) {
        System.out.println("--- Starting Tests ---");

        // 1. The Happy Path (Example provided in prompt)
        assertEncoding("AAAABBBCCDAA", "4A3B2C1D2A");

        // 2. Edge Case: No repetitions
        assertEncoding("ABC", "1A1B1C");

        // 3. Edge Case: Single character
        assertEncoding("A", "1A");

        // 4. Edge Case: All same characters
        assertEncoding("BBBBB", "5B");

        // 5. Edge Case: Repeating groups separated (check for state reset)
        assertEncoding("AAABBAA", "3A2B2A");

        // 6. Edge Case: Empty String (Ask interviewer: return "" or null?)
        // Assuming "" for now.
        assertEncoding("", "");

        // 7. Edge Case: Multi-digit counts (Often missed!)
        // Does your logic handle it if there are 12 'A's?
        assertEncoding("AAAAAAAAAAAA", "12A");

        System.out.println("--- All Tests Finished ---");
    }

    /**
     * Helper method to run assertions and print clear Pass/Fail status.
     */
    private static void assertEncoding(String input, String expected) {
        String actual = encode(input);
        boolean pass = expected.equals(actual);

        if (pass) {
            System.out.println("✅ PASS: Input [" + input + "]");
        } else {
            System.out.println("❌ FAIL: Input [" + input + "]");
            System.out.println("   Expected: " + expected);
            System.out.println("   Actual:   " + actual);
        }
    }

    // ---------------------------------------------------------
    // YOUR CODE GOES HERE
    // ---------------------------------------------------------
    public static String encode(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        int n = s.length();
        int i = 0;

        while (i < n) {
            // 1. Capture the character we are currently counting
            char currentChar = s.charAt(i);
            int count = 0;

            // 2. Run forward as long as characters match the current one
            // We verify i < n first to avoid IndexOutOfBounds
            while (i < n && s.charAt(i) == currentChar) {
                count++;
                i++;
            }

            // 3. Append the result for this group
            sb.append(count).append(currentChar);

            // Note: We do NOT need to increment 'i' here manually.
            // The inner while loop has already moved 'i' to the start
            // of the NEXT group of characters.
        }

        return sb.toString();
    }
}