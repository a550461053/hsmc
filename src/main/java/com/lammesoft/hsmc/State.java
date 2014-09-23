package com.lammesoft.hsmc;

import java.util.ArrayList;
import java.util.List;


public class State {
    private int id=-1;
    private State parent=null;
  
    private List<State> children = new ArrayList<State>();

    public List<State> getChildren() {
        return children;
    }
    
    public State() {
        
    }
    
    public State(int id) {
        setId(id);
    }
    

    public State getParent() {
        return parent;
    }

    public void setParent(State parent) {
        this.parent = parent;
        this.parent.children.add(this);
    }
    
    
    
    
    
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    
}
