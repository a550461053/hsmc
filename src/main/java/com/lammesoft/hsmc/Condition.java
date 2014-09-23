
package com.lammesoft.hsmc;


public class Condition {
    private int priority;
    private final String thecond;

    public Condition(String thecond, int priority) {
        this.priority = priority;
        this.thecond = thecond;
    }

    public String getThecond() {
        return thecond;
    }

    public int getPriority() {
        return priority;
    }
    
}
