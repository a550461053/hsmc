package com.lammesoft.hsmc;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Machine {

    private final List<State> states = new ArrayList<>();
    private final List<Method> methods = new ArrayList<>();
    private final List<Transition> transitions = new ArrayList<>();

   

    private void storeMethods(Method m) {
        if (m == null || methods.contains(m)) {
            return;
        }
        int maxId = Integer.MIN_VALUE;
        for (Method mt : methods) {
            if (mt.getId() > maxId) {
                maxId = mt.getId();
            }
        }
        m.setId(Math.max(0, maxId + 1));
        methods.add(m);
    }

    private void storeState(State s) {

        if (s == null || states.contains(s)) {
            return;
        }

        if (s.getId() <= -1) {
            int maxId = Integer.MIN_VALUE;
            for (State state : states) {
                if (state.getId() > maxId) {
                    maxId = state.getId();
                }
            }
            s.setId(Math.max(0, maxId + 1));
        }

        states.add(s);

        State parent = s.getParent();
        while (parent != null) {
            storeState(parent);
            parent = parent.getParent();
        }

        for (State child : s.getChildren()) {
            storeState(child);
        }

    }

    public void trans(Transition t) {

        transitions.add(t);
    }

    private void reduce() {

        // recuperation des states qui ne sont parent de personne
        List<State> statesParentsDePersonne = new ArrayList<>();
        for (State s : states) {
            // test si S est parent de qq
            boolean isParentOfSO = false;
            for (State si : states) {
                if (!si.equals(s) && si.getParent() != null
                        && si.getParent().equals(s)) {
                    System.out.println(s.getId() + " est en autre parent de " + si.getId());
                    isParentOfSO = true;
                    break;
                }
            }

            if (isParentOfSO == false) {
                statesParentsDePersonne.add(s);
                System.out.println("> Parent de personne  : " + s.getId());
            }
        }

        for (State ps : statesParentsDePersonne) {

            State s = ps;

            while (s != null) { // on remonte la chaine de 1 en 1
                // reopie dans transitions non existantes 
                System.out.println("Recopie a parir de " + s.getId());

                State par = s.getParent();
                while (par != null) {

                    System.out.println("\t du parent parents de " + par.getId());
                    List<Transition> tranistionsToAdd = new ArrayList<>();

                    List<Transition> pTransitions = getTransitionsFrom(par);
                    System.out.println("\t\t" + pTransitions.size() + " trs");

                    for (Transition n : pTransitions) {
                        if (transExist(s, n.getM()) == null) {
                            tranistionsToAdd.add(new Transition(n.getM(), s, n.getTo(), n.getCond(), n.getAction()));
                        }
                    }

                    for (Transition tnew : tranistionsToAdd) {
                        transitions.add(tnew);
                    }

                    //cur = par;
                    par = par.getParent();
                }
                s = s.getParent();
            }

        }

    }

    private List<Transition> getTransitionsFrom(State from) {
        List<Transition> list = new ArrayList<>();
        for (Transition t : transitions) {
            if (t.getFrom().equals(from)) {
                list.add(t);
            }
        }
        return list;
    }

    private Transition transExist(State s, Method m) {
        for (Transition t : transitions) {
            if (t.getFrom().equals(s) && t.getM().equals(m)) {
                return t;
            }
        }
        return null;
    }

    private void buildGraph(String path) {

        try {

            StringBuilder b = new StringBuilder();
            b.append("digraph G {\n");

            for (Transition tr : transitions) {
                b.append("s").append(tr.getFrom().getId()).append(" -> s").append(tr.getTo().getId()).append("[ label=\"m").append(tr.getM().getId()).append("\"];\n");
            }

            b.append("}\n");
            try (PrintWriter pw = new PrintWriter(new FileOutputStream("D:\\src\\tests\\States\\src\\states\\Demo.dot"))) {
                pw.println(b.toString());
            }
            
      
            Process pr = Runtime.getRuntime().exec("\"C:\\Program Files (x86)\\Graphviz2.38\\bin\\dot.exe\" -Tpng D:\\src\\tests\\States\\src\\states\\Demo.dot -o D:\\src\\tests\\States\\src\\states\\Demo.png");
            
            pr.waitFor();
            
            
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    public void build(State startup, String path) {

        // on recupere tout
        for (Transition tr : transitions) {
            storeState(tr.getFrom());
            storeState(tr.getTo());
            storeMethods(tr.getM());
        }
        
        
        

        reduce();

        StringBuilder b = new StringBuilder();

        String stateIdName = "state";

        b.append("package states;\n");
        b.append("public class Demo {\n");

        b.append("private int ").append(stateIdName).append("=").append(startup.getId()).append(";\n");

        for (Method mt : methods) {

            b.append(mt.getSignature()).append(" { // debut methode\n");

            int stcount = 0;
            for (State ss : states) {
                // recherche des transitions qui commencent par cette methode
                List<Transition> transitionsWithCurrentMethodAndCurrentState = new ArrayList<>();
                for (Transition tr : transitions) {
                    if (tr.getM().equals(mt) && tr.getFrom().equals(ss)) {
                        transitionsWithCurrentMethodAndCurrentState.add(tr);
                    }
                }

                if (transitionsWithCurrentMethodAndCurrentState.size() > 0) {
                    if (stcount > 0) {
                        b.append(" else ");
                    }
                    stcount++;
                    b.append("if(").append(stateIdName).append("==").append(ss.getId()).append(") {");

                    Collections.sort(transitionsWithCurrentMethodAndCurrentState, new Comparator<Transition>() {
                        @Override
                        public int compare(Transition o1, Transition o2) {

                            if (o1.getCond() == null) {
                                return 1;
                            }
                            if (o2.getCond() == null) {
                                return -1;
                            }

                            return o1.getCond().getPriority() > o2.getCond().getPriority() ? -1 : 1;

                        }
                    });

                    int k = 0;
                    for (Transition tr : transitionsWithCurrentMethodAndCurrentState) {
                        if (k > 0) {
                            b.append("\telse");
                        }

                        if (tr.getCond() != null) {
                            b.append("\tif (").append(tr.getCond().getThecond()).append(") \n");
                        }

                        if (transitionsWithCurrentMethodAndCurrentState.size() > 1) {
                            b.append("\t{ //start\n");
                        }

                        b.append("\t\t").append(stateIdName).append("=").append(tr.getTo().getId()).append(";" + "      //from ").append(tr.getFrom().getId()).append(" to ").append(tr.getTo().getId()).append("\n");

                        if (transitionsWithCurrentMethodAndCurrentState.size() > 1) {
                            b.append("\t} //fin\n");
                        }

                        k++;
                    }

                    b.append("} // fin si state\n");
                }

            }
            b.append("} // fin methode\n");
        }
        b.append("}\n");

        PrintWriter pw;
        try {
            pw = new PrintWriter(new FileOutputStream("D:\\src\\tests\\States\\src\\states\\Demo.java"));
            pw.print(b.toString());
            pw.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Machine.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        
        
        buildGraph(path);
        
        
        System.out.println(b.toString());
    }
}
