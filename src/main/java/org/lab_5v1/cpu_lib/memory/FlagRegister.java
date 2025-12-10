package org.lab_5v1.cpu_lib.memory;

public class FlagRegister {
    boolean ZF;// zero
    boolean SF; //negative
    boolean OF; //overflow
    public FlagRegister(){
        ZF = false;
        SF = false;
        OF = false;
    }

    public boolean isOF() {
        return OF;
    }

    public boolean isSF() {
        return SF;
    }

    public boolean isZF() {
        return ZF;
    }

    public void setOF(boolean OF) {
        this.OF = OF;
    }

    public void setSF(boolean SF) {
        this.SF = SF;
    }

    public void setZF(boolean ZF) {
        this.ZF = ZF;
    }
    public void reset() {
        ZF = false;
        SF = false;
        OF = false;
    }

    @Override
    public String toString() {
        return String.format("ZF=%b, SF=%b, OF=%b", ZF, SF, OF);
    }

}
