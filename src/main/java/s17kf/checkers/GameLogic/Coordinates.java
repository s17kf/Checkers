package s17kf.checkers.GameLogic;

/**
 * Created by Stefan on 2017-05-19.
 */

public class Coordinates{

    int x;
    int y;

    public Coordinates(Coordinates other){
        x = new Integer(other.x);
        y = new Integer(other.y);
    }

    public Coordinates(int x, int y) throws Exception {
        if(x>7 || y>7 || x<0 || y<0)
            throw new Exception("Bad coordinates:" + x + y);
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    Boolean goUpLeft(){
        if(y == 0 || x == 0)
            return false;
        y--;
        x--;
        return true;
    }
    Boolean goUpRight(){
        if(y == 0 || x == 7)
            return false;
        y--;
        x++;
        return true;
    }
    Boolean goDownLeft(){
        if(y == 7 || x == 0)
            return false;
        y++;
        x--;
        return true;
    }
    Boolean goDownRight(){
        if(y == 7 || x == 7)
            return false;
        y++;
        x++;
        return true;
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