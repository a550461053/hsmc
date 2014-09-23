package com.lammesoft.hsmc.samples;

import com.lammesoft.hsmc.Condition;
import com.lammesoft.hsmc.Machine;
import com.lammesoft.hsmc.Method;
import com.lammesoft.hsmc.State;
import com.lammesoft.hsmc.Transition;

public class Samples {

    public static void main(String[] args) {
        Switch();
       demo1();
        // demo2();
        //demo3();
    }
    
    
    
    
    
    
    
    private static String relpath() {
        return "D:/src/tests/hsmc/src/main/java/com/lammesoft/hsmc/samples";
    }
    private static String relPackage() {
        return "com.lammesoft.hsmc.samples";
    }

    public static void Switch() {
        State s0 = new State();
        State s1 = new State();
        Method ma = new Method("public void hit()");

        Machine m = new Machine();
        m.trans(new Transition(ma, s0, s1, null, null));
        m.trans(new Transition(ma, s1, s0, null, null));

        m.build(s0, relpath(), relPackage(), "Demo0");
    }
    
    

    public static void demo2() {

        State s2 = new State(2);
        State s20 = new State(20);

        State s1 = new State(1);
        State s10 = new State(10);

        State s100 = new State(100);
        State s101 = new State(101);

        s100.setParent(s1);
        s101.setParent(s1);

        s1.setParent(s2);

        Method ma = new Method("public void a()");
        Method mb = new Method("public void b()");
        Method mc = new Method("public void c()");

        Machine m = new Machine();
        m.trans(new Transition(ma, s2, s20, null, null));

        m.trans(new Transition(mb, s2, s10, null, null));
        m.trans(new Transition(mb, s1, s10, null, null));

        m.trans(new Transition(mc, s101, s101, null, null));

        m.build(s2, relpath(),relPackage(), "Demo2");

    }

    public static void demo1() {
        State s1 = new State();
        State s2 = new State();

        State comm = new State();
        State fin = new State();
        s1.setParent(comm);
        s2.setParent(comm);

        State a = new State();
        State b = new State();

        Method m0 = new Method("public void m0()");
        Method m1 = new Method("public void m1()");

        Method exit = new Method("public void exit()");

        Method swap = new Method("public void swap()");

        Machine m = new Machine();
        m.trans(new Transition(m0, s1, s2, new Condition("true", 50), null));
        m.trans(new Transition(m0, s1, s2, null, null));
        m.trans(new Transition(m0, s1, s2, new Condition("true", 6), null));
        m.trans(new Transition(m1, s2, s1, null, null));

        m.trans(new Transition(exit, comm, fin, null, null));

        m.trans(new Transition(swap, a, b, null, null));
        m.trans(new Transition(swap, b, a, null, null));
        //m.logStates();

        m.build(s1, relpath(),relPackage(), "Demo1");

    }

}
