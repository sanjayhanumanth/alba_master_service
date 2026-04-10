package com.alba.master.security;

public final class EmployeeContextHolder {
    private static final ThreadLocal<EmployeeContext> CONTEXT = new ThreadLocal<>();
    private EmployeeContextHolder() {}
    public static void set(EmployeeContext ctx) { CONTEXT.set(ctx); }
    public static EmployeeContext get()         { return CONTEXT.get(); }
    public static void clear()                  { CONTEXT.remove(); }
    public static Long getEmployeeId() { return CONTEXT.get() != null ? CONTEXT.get().getEmployeeId() : null; }
    public static String getEmail()    { return CONTEXT.get() != null ? CONTEXT.get().getEmail() : null; }
    public static String getRoleName() { return CONTEXT.get() != null ? CONTEXT.get().getRoleName() : null; }
}
