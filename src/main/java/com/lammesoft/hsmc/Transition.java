

package com.lammesoft.hsmc;

public class Transition {

    private State from, to;
    private Method m;
    private Condition cond;
    private Action action;

    public Transition(Method m, State from, State to, Condition cond, Action action) {        
        this.m = m;
        this.from = from;
        this.to = to;
        this.cond = cond;
        this.action = action;
    }

    public Condition getCond() {
        return cond;
    }

    
    public State getFrom() {
        return from;
    }

    public State getTo() {
        return to;
    }

    public Method getM() {
        return m;
    }

    public Action getAction() {
        return action;
    }
    
  
}
