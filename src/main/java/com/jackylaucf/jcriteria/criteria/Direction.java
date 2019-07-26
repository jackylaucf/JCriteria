package com.jackylaucf.jcriteria.criteria;

public enum Direction {
    ASC(1),
    DSC(-1);

    private int i;

    Direction(int i){
        this.i = i;
    }

    public static Direction getDirection(int i){
        if(i==-1){
            return Direction.DSC;
        }else{
            return Direction.ASC;
        }
    }

    public int intVal() {
        return this.i;
    }
}
