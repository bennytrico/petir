package com.example.petir;

public class CheckUpList {
    private Boolean all, electrical, bracking_system, engine, mechanical;
    public CheckUpList () {

    }
    public Boolean getAll() {
        return all;
    }

    public void setAll(Boolean all) {
        this.all = all;
    }

    public Boolean getElectrical() {
        return electrical;
    }

    public void setElectrical(Boolean electrical) {
        this.electrical = electrical;
    }

    public Boolean getBracking_system() {
        return bracking_system;
    }

    public void setBracking_system(Boolean bracking_system) {
        this.bracking_system = bracking_system;
    }

    public Boolean getEngine() {
        return engine;
    }

    public void setEngine(Boolean engine) {
        this.engine = engine;
    }

    public Boolean getMechanical() {
        return mechanical;
    }

    public void setMechanical(Boolean mechanical) {
        this.mechanical = mechanical;
    }
}
