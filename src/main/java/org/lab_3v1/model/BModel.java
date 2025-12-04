package org.lab_3v1.model;

public class BModel {
    private static Model m;

    public  static Model build() {
        if (m == null) {
            m = new Model();
        }
        return m;
    }

    public  void reset() {
        m = new Model();
    }

}