package com.restaurant.erp.common.context;

public class BranchContext {

    private static final ThreadLocal<Integer> CURRENT_BRANCH_ID = new ThreadLocal<>();

    public static void setCurrentBranchId(Integer branchId) {
        CURRENT_BRANCH_ID.set(branchId);
    }

    public static Integer getCurrentBranchId() {
        return CURRENT_BRANCH_ID.get();
    }

    public static void clear() {
        CURRENT_BRANCH_ID.remove();
    }
}
