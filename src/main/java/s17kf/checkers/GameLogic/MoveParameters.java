package s17kf.checkers.GameLogic;

/**
 * Created by stefan on 26.05.17.
 */
public class MoveParameters {
    int movedPawnNumber;
    int moveDestination;
    Boolean isHitContinuation;

    public MoveParameters(int playerNumber, String message){
        int i = 0;
        String part1 = new String(), part2 = new String(), part3 = new String();
        for (; message.charAt(i) != ';'; i++){
            part1 += message.charAt(i);
        }
        for(i++; message.charAt(i) != ';'; i++){
            part2 += message.charAt(i);
        }
        for(i++; i < message.length(); i++){
            part3 += message.charAt(i);
        }
        movedPawnNumber = Integer.parseInt(part1);
        moveDestination = Integer.parseInt(part2);
        isHitContinuation = Boolean.parseBoolean(part3);
        if(playerNumber == 2){
            playerChange();
        }
    }

    public MoveParameters(int movedPawnNumber, int moveDestination, Boolean isHitContinuation) {
        this.movedPawnNumber = movedPawnNumber;
        this.moveDestination = moveDestination;
        this.isHitContinuation = isHitContinuation;
    }

    public void playerChange(){
        movedPawnNumber = movedPawnNumber < 12 ? movedPawnNumber + 12 : movedPawnNumber -12;
        moveDestination = 63 - moveDestination;
    }

    public int getMovedPawnNumber() {
        return movedPawnNumber;
    }

    public int getMoveDestination() {
        return moveDestination;
    }

    public Boolean getHitContinuation() {
        return isHitContinuation;
    }

    @Override
    public String toString() {
        return movedPawnNumber + ";" + moveDestination + ";" + isHitContinuation;
    }
}
