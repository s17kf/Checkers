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

    void setPosition(int squareNumber) throws Exception{
        position=new Coordinates(squareNumber);
    }
    void setPosition(Coordinates newPosition){
        position=newPosition;
    }

    public Coordinates getPosition() {
        return position;
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
//        Vector<Coordinates> result=new Vector<Coordinates>();
//        for(PawnPossibleMove possibleMove:possibleMoves){
//            result.add(possibleMove.destination);
//        }
        return possibleMoves;
    }

    public Vector<Integer> getPossibleMovesAssIntegers(){
        Vector<Integer> result=new Vector<Integer>();
        for(PawnPossibleMove possibleMove:possibleMoves){
            result.add(possibleMove.destination.toInt());
        }
        return result;
    }

    @Override
    public String toString() {
        return position.toInt().toString();
    }



}

class PawnPossibleMove{
    protected Coordinates destination;
    protected Boolean isHit;

    public PawnPossibleMove(Coordinates destination,Boolean isHit) throws Exception {
//		this.destination=destination;
        this.destination=new Coordinates(destination.toInt());
        this.isHit=new Boolean(isHit);
    }

    public Coordinates getDestination() {
        return destination;
    }

    public Boolean getHit() {
        return isHit;
    }
}