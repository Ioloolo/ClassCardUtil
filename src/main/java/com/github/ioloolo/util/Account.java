package com.github.ioloolo.util;

public enum Account {
    private String id;
    private String pw;

    Account(String id, String pw) {
        this.id = id;
        this.pw = pw;
    }

    public String getId() {
        return id;
    }

    public String getPw() {
        return pw;
    }
}
