package GameLogic;

/**
 * Created by Stefan on 2017-05-19.
 */

import java.util.Vector;

public class Pawn{
    Coordinates position;
    Boolean isPlaying;
    Boolean isQueen;
    Vector <PawnPossibleMove> possibleMoves;


    public Pawn(int squareNumber) throws Exception{
        position=new Coordinates(squareNumber);
        isPlaying=true;
        isQueen=false;
        possibleMoves=new Vector<PawnPossibleMove>();
    }

    void setPosition(Coordinates newPosition){
        position= new Coordinates(newPosition);
    }

    public Coordinates getPosition() {
        return position;
    }

    public int getX(){
        return position.getX();
    }

    public int getY(){
        return position.getY();
    }

    Boolean goOneTo(Coordinates destination) throws Exception{
        if( Math.abs(destination.x - position.getX()) != Math.abs(destination.y - position.getY()))
            throw new Exception("Destination is not in stright line move! source=" + toString() + "; destination=" + destination.toString());
        if(destination.y == position.getY())
            return false;
        if(destination.y < position.getY()){
            if(destination.x < position.getX())
                position.goUpLeft();
            else
                position.goUpRight();
        }
        else {
            if (destination.x < position.getX())
                position.goDownLeft();
            else
                position.goDownRight();
        }

        return destination.y != position.getY();
    }

    void addPossibleMove(Coordinates destination, Boolean isHit) throws Exception {
        possibleMoves.add(new PawnPossibleMove(destination, isHit));
    }

    void addPossibleMove(int x,int y,Boolean isHit)throws Exception{
        addPossibleMove(new Coordinates(x, y), isHit);
    }

    void resetPossibleMoves(){
        possibleMoves=new Vector<PawnPossibleMove>();
    }

    void leaveOnlyHitsAsPossibleMoves(){
        Vector<PawnPossibleMove> onlyHits = new Vector<PawnPossibleMove>();
        for(PawnPossibleMove possibleMove:possibleMoves){
            if(possibleMove.getHit()){
                onlyHits.add(possibleMove);
            }
        }

        possibleMoves=onlyHits;
    }

    int isPossibleMove(Coordinates destination) throws Exception{
		/*
		 * Meaning of returned value:
		 * 			0 -> this pawn can't move to destination
		 * 			1 -> this pawn can move to destination with NO hit
		 * 			2 -> this pawn can move to destination with hit
		 */
        if(possibleMoves.contains(new PawnPossibleMove(destination, false)))
            return 1;
        if(possibleMoves.contains(new PawnPossibleMove(destination, true)))
            return 2;
        return 0;
    }

    Vector<PawnPossibleMove> getPossibleMoves(){
        return possibleMoves;
    }

    public void setPossibleMoves(Vector<PawnPossibleMove> possibleMoves) {
        this.possibleMoves = possibleMoves;
    }

    public Vector<Integer> getPossibleMovesAssIntegers(){
        Vector<Integer> result=new Vector<Integer>();
        for(PawnPossibleMove possibleMove:possibleMoves){
            result.add(possibleMove.toInt());
        }
        return result;
    }

    @Override
    public String toString() {
        return position.toInt().toString();
    }

    void setAsQueen(){
        isQueen=true;
    }


}

class PawnPossibleMove extends Coordinates{
    protected Boolean isHit;
    int countOfPossibleHits;

    public PawnPossibleMove(Coordinates destination,Boolean isHit){
        super(destination);
        this.isHit=new Boolean(isHit);
        countOfPossibleHits = 0;
    }

    public void setCountOfPossibleHits(int countOfPossibleHits) {
        this.countOfPossibleHits = countOfPossibleHits;
    }

    public int getCountOfPossibleHits() {
        return countOfPossibleHits;
    }

    public Boolean getHit() {
        return isHit;
    }

}