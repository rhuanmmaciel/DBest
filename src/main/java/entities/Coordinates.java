package entities;

public record Coordinates(int x, int y) {

    @Override
    public String toString(){

        return "(x = " + x + ", y = " + y + ")";

    }

}
