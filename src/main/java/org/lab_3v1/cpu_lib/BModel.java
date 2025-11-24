package org.lab_3v1.cpu_lib;

public class BModel {
    private Model m;

    public  Model build() {
        if (m == null) {
            m = new Model();
        }
        return m;
    }

    public  void reset() {
        m = new Model();
    }

    public  Model getInstance() {
        return build();
    }
}