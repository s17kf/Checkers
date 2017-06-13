package s17kf.checkers.Client;

/**
 * Created by stefan on 04.06.17.
 */
public class SquareIndex {
    Character columnIndex;
    Integer rowIndex;

    public SquareIndex(int squareNumber, Boolean isPlayer1White) {
        String characters = new String("ABCDEFGH");
        this.columnIndex = isPlayer1White ? Character.valueOf(characters.charAt(squareNumber%8)) : Character.valueOf(characters.charAt(7 - squareNumber%8));
        this.rowIndex = isPlayer1White ? new Integer(8 - squareNumber/8) : new Integer(squareNumber/8 + 1);
    }

    @Override
    public String toString() {
        return columnIndex.toString() + rowIndex.toString();
    }
}
