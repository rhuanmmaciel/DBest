package lib.booleanexpression.entities.elements;

import java.util.Arrays;

public class Variable extends Element{

    private final String[] name;

    public Variable(String name){

    	this.name = name.split("\\.");
    	
    }

    public String[] getNames(){
        return name;
    }

    @Override
    public String toString(){
        return Arrays.stream(name).reduce((s, s2) -> s+'.'+s2).get();
    }

}
