package GameLogic;

/**
 * Created by Stefan on 2017-05-19.
 */

public class Coordinates{

    int x;
    int y;

    public Coordinates(int x, int y) throws Exception {
        if(x>7 || y>7 || x<0 || y<0)
            throw new Exception("Bad coordinates!");
        this.x = x;
        this.y = y;
    }

    public Coordinates(int value)throws Exception{
        if(value<0 || value>63)
            throw new Exception("Bad coordinates value!");
        this.x = value%8;
        this.y = value/8;
    }

    public Integer toInt(){
        return y*8+x;
    }

    static Coordinates toCoordinates(int value) throws Exception{
        return new Coordinates(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinates that = (Coordinates) o;

        if (x != that.x) return false;
        return y == that.y;
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }




}