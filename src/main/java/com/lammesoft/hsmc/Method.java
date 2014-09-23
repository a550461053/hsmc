package com.lammesoft.hsmc;

public class Method {
  
    private int id;
    private String signature;

    public Method(String signature) {       
        this.signature = signature;
    }
    
    public String getSignature() {
        return signature;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
}
