package code.branchless;

public final class BranchlessOperations {

    private static final int BIT_SHIFT_BY_31 = 31;

    private BranchlessOperations() { }

    /**
     * Branchless method for 32bit integers to perform the function [return x == y ? a : b].
     *
     * @param x 1st argument we wish to compare equality with the 2nd argument
     * @param y 2nd argument we wish to compare equality with the 1st argument
     * @param a Argument we wish to return if the first 2 arguments were equal
     * @param b Argument we wish to return if the first 2 arguments were not equal
     * @return Returns [x == y ? a : b]
     */
    public static int xEqualsYReturnAElseB(int x, int y, int a, int b) {
        int tmp = ((x - y) - 1) >> BIT_SHIFT_BY_31;

        int mask = (((x - y) >> BIT_SHIFT_BY_31) ^ tmp) & tmp;

        return (a & mask) | (b & (~mask));
    }

    /**
     * Branchless method for 32bit integers to perform the function [return a < 0 ? b : a;].
     *
     * @param a Argument we wish to check if it's less than 0, and return it if false
     * @param b Argument we wish to return if 1st argument was less than 0
     * @return Returns 2nd argument if 1st argument was less than 0, returns 1st argument otherwise
     */
    public int aLessThanZeroReturnBElseA(int a, int b) {
        int mask = a >> BIT_SHIFT_BY_31;

        return (b & mask) | ((~mask) & a);
    }

}
