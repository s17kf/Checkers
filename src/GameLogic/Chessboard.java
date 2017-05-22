package GameLogic;

/**
 * Created by Stefan on 2017-05-19.
 */

import java.util.Arrays;
import java.util.Vector;


public class Chessboard{

    int board[][];	//board[Y][X] !!! ( no [x][y] )
    Pawn pawns[];
    Boolean isPlayer1white;
    Boolean isPossibleHit;
    int activePlayer;
    Vector<Integer> hitsInLastMove;
//	Boolean hitInLastMove;

    public Chessboard(Boolean isPlayer1White) throws Exception{
        this.isPlayer1white = isPlayer1White;
        if(isPlayer1White)
            activePlayer=1;
        else
            activePlayer=2;
        isPossibleHit=false;
//		hitInLastMove=false;
        board = new int[8][8];
        pawns = new Pawn[24];
        hitsInLastMove = new Vector<>();
        for(int i=0;i<64;i++){
            board[i/8][i%8]=-1;
        }
        for(int i=0;i<4;i++){
            pawns[i]=new Pawn(62-i*2);
            pawns[i+4]=new Pawn(55-i*2);
            pawns[i+8]=new Pawn(46-i*2);
            pawns[i+12]=new Pawn(i*2+1);
            pawns[i+16]=new Pawn(8+i*2);
            pawns[i+20]=new Pawn(17+i*2);
        }
        board[7][6] = (board[7][4] = (board[7][2] = (board[7][0] = 3) - 1) - 1) - 1;
        board[6][7] = (board[6][5] = (board[6][3] = (board[6][1] = 7) - 1) - 1) - 1;
        board[5][6] = (board[5][4] = (board[5][2] = (board[5][0] = 11) - 1) - 1) - 1;

        board[0][7] = (board[0][5] = (board[0][3] = (board[0][1] = 12) + 1) + 1) + 1;
        board[1][6] = (board[1][4] = (board[1][2] = (board[1][0] = 16) + 1) + 1) + 1;
        board[2][7] = (board[2][5] = (board[2][3] = (board[2][1] = 20) + 1) + 1) + 1;

        updatePossibleMoves();
    }

    public void updatePossibleMoves() throws Exception{
        isPossibleHit=false;
        for(int i=0;i<24;i++)
            updatePossibleMoves(i);
    }

    private void updatePossibleMoves(int pawnNumber) throws Exception{
        pawns[pawnNumber].resetPossibleMoves();
        Coordinates pawnPosition=pawns[pawnNumber].position;
        if(isQueen(pawnNumber)){
            if(pawnPosition.x>0 && pawnPosition.y>0)
                addQueenPossibleUpLeft(pawnNumber, new Coordinates(pawnPosition.x-1, pawnPosition.y-1), pawns[pawnNumber].possibleMoves);
            if(pawnPosition.x<7 && pawnPosition.y>0)
                addQueenPossibleUpRight(pawnNumber, new Coordinates(pawnPosition.x+1, pawnPosition.y-1), pawns[pawnNumber].possibleMoves);
            if(pawnPosition.x>0 && pawnPosition.y<7)
                addQueenPossibleDownLeft(pawnNumber, new Coordinates(pawnPosition.x-1, pawnPosition.y+1), pawns[pawnNumber].possibleMoves);
            if(pawnPosition.x<7 && pawnPosition.y<7)
                addQueenPossibleDownRight(pawnNumber, new Coordinates(pawnPosition.x+1, pawnPosition.y+1), pawns[pawnNumber].possibleMoves);
        }
        else{
            if(isPlayer1(pawnNumber)){
                if(pawnPosition.y>0){
                    if(pawnPosition.x>0 && !isSquareEngaged(pawnPosition.x-1, pawnPosition.y-1))
                        pawns[pawnNumber].addPossibleMove(pawnPosition.x-1, pawnPosition.y-1, false);
                    if(pawnPosition.x<7 && !isSquareEngaged(pawnPosition.x+1, pawnPosition.y-1))
                        pawns[pawnNumber].addPossibleMove(pawnPosition.x+1, pawnPosition.y-1, false);
                }
            }
            else{
                if(pawnPosition.y<7){
                    if(pawnPosition.x>0 && !isSquareEngaged(pawnPosition.x-1, pawnPosition.y+1))
                        pawns[pawnNumber].addPossibleMove(pawnPosition.x-1, pawnPosition.y+1, false);
                    if(pawnPosition.x<7 && !isSquareEngaged(pawnPosition.x+1, pawnPosition.y+1))
                        pawns[pawnNumber].addPossibleMove(pawnPosition.x+1, pawnPosition.y+1, false);
                }
            }
            if(checkHitUpLeft(pawnNumber)){
                pawns[pawnNumber].addPossibleMove(pawnPosition.x-2, pawnPosition.y-2, true);
                isPossibleHit=true;
            }
            if(checkHitUpRight(pawnNumber)){
                pawns[pawnNumber].addPossibleMove(pawnPosition.x+2, pawnPosition.y-2, true);
                isPossibleHit=true;
            }
            if(checkHitDownLeft(pawnNumber)){
                pawns[pawnNumber].addPossibleMove(pawnPosition.x-2, pawnPosition.y+2, true);
                isPossibleHit=true;
            }
            if(checkHitDownRight(pawnNumber)){
                pawns[pawnNumber].addPossibleMove(pawnPosition.x+2, pawnPosition.y+2, true);
                isPossibleHit=true;
            }
        }
    }

    private void updatePossibleMovesForContinuation(int pawnNumber) throws Exception{
        for(Pawn pawn:pawns){
            pawn.resetPossibleMoves();
        }
        updatePossibleMoves(pawnNumber);
        pawns[pawnNumber].leaveOnlyHitsAsPossibleMoves();
    }

    private void addQueenPossibleUpLeft(int pawnNumber,Coordinates checkedSquare,Vector<PawnPossibleMove> possibleMovesVector) throws Exception{
        if(!isSquareEngaged(checkedSquare)){
            possibleMovesVector.add(new PawnPossibleMove(checkedSquare, false));
            if(checkedSquare.x>0 && checkedSquare.y>0)
                addQueenPossibleUpLeft(pawnNumber, new Coordinates(checkedSquare.x-1, checkedSquare.y-1), possibleMovesVector);
            return;
        }
        if(checkHitUpLeft(pawnNumber, checkedSquare)){
            possibleMovesVector.add(new PawnPossibleMove(new Coordinates(checkedSquare.x-1, checkedSquare.y-1), true));
            if(checkedSquare.x > 1 && checkedSquare.y > 1)
                addQueenPossibleUpLeft(pawnNumber, new Coordinates(checkedSquare.x-2, checkedSquare.y-2), possibleMovesVector);
        }
    }
    private void addQueenPossibleUpRight(int pawnNumber,Coordinates checkedSquare,Vector<PawnPossibleMove> possibleMovesVector) throws Exception{
        if(!isSquareEngaged(checkedSquare)){
            possibleMovesVector.add(new PawnPossibleMove(checkedSquare, false));
            if(checkedSquare.x<7 && checkedSquare.y>0)
                addQueenPossibleUpRight(pawnNumber, new Coordinates(checkedSquare.x+1, checkedSquare.y-1), possibleMovesVector);
            return;
        }
        if(checkHitUpRight(pawnNumber, checkedSquare)){
            possibleMovesVector.add(new PawnPossibleMove(new Coordinates(checkedSquare.x+1, checkedSquare.y-1), true));
            if(checkedSquare.x < 6 && checkedSquare.y > 1)
                addQueenPossibleUpRight(pawnNumber, new Coordinates(checkedSquare.x+2, checkedSquare.y-2), possibleMovesVector);
        }
    }
    private void addQueenPossibleDownLeft(int pawnNumber,Coordinates checkedSquare,Vector<PawnPossibleMove> possibleMovesVector) throws Exception{
        if(!isSquareEngaged(checkedSquare)){
            possibleMovesVector.add(new PawnPossibleMove(checkedSquare, false));
            if(checkedSquare.x>0 && checkedSquare.y<7)
                addQueenPossibleDownLeft(pawnNumber, new Coordinates(checkedSquare.x-1, checkedSquare.y+1), possibleMovesVector);
            return;
        }
        if(checkHitDownLeft(pawnNumber, checkedSquare)){
            possibleMovesVector.add(new PawnPossibleMove(new Coordinates(checkedSquare.x-1, checkedSquare.y+1), true));
            if(checkedSquare.x > 1 && checkedSquare.y < 6)
                addQueenPossibleDownLeft(pawnNumber, new Coordinates(checkedSquare.x-2, checkedSquare.y+2), possibleMovesVector);
        }
    }
    private void addQueenPossibleDownRight(int pawnNumber,Coordinates checkedSquare,Vector<PawnPossibleMove> possibleMovesVector) throws Exception{
        if(!isSquareEngaged(checkedSquare)){
            possibleMovesVector.add(new PawnPossibleMove(checkedSquare, false));
            if(checkedSquare.x<7 && checkedSquare.y<7)
                addQueenPossibleDownRight(pawnNumber, new Coordinates(checkedSquare.x+1, checkedSquare.y+1), possibleMovesVector);
            return;
        }
        if(checkHitDownRight(pawnNumber, checkedSquare)){
            possibleMovesVector.add(new PawnPossibleMove(new Coordinates(checkedSquare.x+1, checkedSquare.y+1), true));
            if(checkedSquare.x <6 && checkedSquare.y <6)
                addQueenPossibleDownRight(pawnNumber, new Coordinates(checkedSquare.x+2, checkedSquare.y+2), possibleMovesVector);
        }
    }


    private Boolean checkHitUpLeft(int pawnNumber) throws Exception{
        Coordinates pawnPosition=pawns[pawnNumber].position;
        if(pawnPosition.x>1 && pawnPosition.y>1){
            if(isSquareEngaged(pawnPosition.x-1, pawnPosition.y-1) && isOtherPlayer(board[pawnPosition.y-1][pawnPosition.x-1],pawnNumber)){
                if(!isSquareEngaged(pawnPosition.x-2, pawnPosition.y-2)){
                    return true;
                }
            }
        }
        return false;
    }
    private Boolean checkHitUpRight(int pawnNumber) throws Exception{
        Coordinates pawnPosition=pawns[pawnNumber].position;
        if(pawnPosition.x<6 && pawnPosition.y>1){
            if(isSquareEngaged(pawnPosition.x+1, pawnPosition.y-1) && isOtherPlayer(board[pawnPosition.y-1][pawnPosition.x+1],pawnNumber)){
                if(!isSquareEngaged(pawnPosition.x+2, pawnPosition.y-2)){
                    return true;
                }
            }
        }
        return false;
    }
    private Boolean checkHitDownLeft(int pawnNumber) throws Exception{
        Coordinates pawnPosition=pawns[pawnNumber].position;
        if(pawnPosition.x>1 && pawnPosition.y<6){
            if(isSquareEngaged(pawnPosition.x-1, pawnPosition.y+1) && isOtherPlayer(board[pawnPosition.y+1][pawnPosition.x-1],pawnNumber)){
                if(!isSquareEngaged(pawnPosition.x-2, pawnPosition.y+2)){
                    return true;
                }
            }
        }
        return false;
    }
    private Boolean checkHitDownRight(int pawnNumber) throws Exception{
        Coordinates pawnPosition=pawns[pawnNumber].position;
        if(pawnPosition.x<6 && pawnPosition.y<6){
            if(isSquareEngaged(pawnPosition.x+1, pawnPosition.y+1) && isOtherPlayer(board[pawnPosition.y+1][pawnPosition.x+1],pawnNumber)){
                if(!isSquareEngaged(pawnPosition.x+2, pawnPosition.y+2)){
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean checkHitUpLeft(int pawnNumber, Coordinates hittedSquare) throws Exception{
        if(hittedSquare.x>0 && hittedSquare.y>0){
            if(isSquareEngaged(hittedSquare.x, hittedSquare.y) && isOtherPlayer(board[hittedSquare.y][hittedSquare.x],pawnNumber)){
                if(!isSquareEngaged(hittedSquare.x-1, hittedSquare.y-1)){
                    return true;
                }
            }
        }
        return false;
    }
    private Boolean checkHitUpRight(int pawnNumber, Coordinates hittedSquare) throws Exception{
        if(hittedSquare.x<7 && hittedSquare.y>0){
            if(isSquareEngaged(hittedSquare.x, hittedSquare.y) && isOtherPlayer(board[hittedSquare.y][hittedSquare.x],pawnNumber)){
                if(!isSquareEngaged(hittedSquare.x+1, hittedSquare.y-1)){
                    return true;
                }
            }
        }
        return false;
    }
    private Boolean checkHitDownLeft(int pawnNumber, Coordinates hittedSquare) throws Exception{
        if(hittedSquare.x>0 && hittedSquare.y<7){
            if(isSquareEngaged(hittedSquare.x, hittedSquare.y) && isOtherPlayer(board[hittedSquare.y][hittedSquare.x],pawnNumber)){
                if(!isSquareEngaged(hittedSquare.x-1, hittedSquare.y+1)){
                    return true;
                }
            }
        }
        return false;
    }
    private Boolean checkHitDownRight(int pawnNumber, Coordinates hittedSquare) throws Exception{
        if(hittedSquare.x<7 && hittedSquare.y<7){
            if(isSquareEngaged(hittedSquare.x, hittedSquare.y) && isOtherPlayer(board[hittedSquare.y][hittedSquare.x],pawnNumber)){
                if(!isSquareEngaged(hittedSquare.x+1, hittedSquare.y+1)){
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean isSquareEngaged(int x,int y){
        if(board[y][x]==-1)
            return false;
        return true;
    }

    private Boolean isSquareEngaged(Coordinates coordinates){
        return isSquareEngaged(coordinates.x, coordinates.y );
    }

    public Boolean isSquareEngaged(int squareNumber) throws Exception{
        return isSquareEngaged(new Coordinates(squareNumber));

    }

    public Vector<Integer> possibleMoves(int x,int y) throws Exception{
        updatePossibleMoves(board[y][x]);
        if(isSquareEngaged(x, y))
            return pawns[board[y][x]].getPossibleMovesAssIntegers();
        throw new Exception("Try to get possible moves of empty square!");
    }

    public Vector<Integer> possibleMoves(Coordinates coordinates) throws Exception{
        return possibleMoves(coordinates.x,coordinates.y);
    }

    public Vector<Integer> possibleMoves(int pawnNumber) throws Exception{
        return possibleMoves(pawns[pawnNumber].position.x,pawns[pawnNumber].position.y);
    }

    void movePawnTo(int pawnNumber, Coordinates destination) throws Exception{
        hitsInLastMove = new Vector<>();
        Boolean isHitInMove=false;
        for( PawnPossibleMove possibleMove: pawns[pawnNumber].possibleMoves){
            if(possibleMove.destination.equals(destination)) {
                isHitInMove = possibleMove.isHit;

                break;
            }
        }
        Coordinates source=pawns[pawnNumber].position;

        board[source.y][source.x]=-1;
        board[destination.y][destination.x]=pawnNumber;
        pawns[pawnNumber].setPosition(destination);
        if(isHitInMove) {
            hit(source, destination);
            if(!isQueen(pawnNumber) && (destination.y == 0 || destination.y == 7)){
                setQueen(pawnNumber);
                updatePossibleMoves();
            }
            else {
                updatePossibleMovesForContinuation(pawnNumber);
                if (pawns[pawnNumber].getPossibleMoves().isEmpty())
                    updatePossibleMoves();
            }
        }
        else{
            if(!isQueen(pawnNumber)){
                if(isPlayer1(pawnNumber)){
                    if(destination.y == 0) {
                        setQueen(pawnNumber);
                    }
                }
                else{
                    if(destination.y == 7){
                        setQueen(pawnNumber);
                    }
                }
            }
            updatePossibleMoves();
        }




//        return true;

    }

    public void moveFromSquareToSquare(int sourceSquareNumber, int destinationSquareNumber)throws Exception{
        movePawnTo(getPawnNumber(sourceSquareNumber),new Coordinates(destinationSquareNumber));
    }

    private void hit(Coordinates source, Coordinates destination) throws Exception{
        if(destination.x < source.x){
            if(destination.y < source.y)
                hitUpLeft(source, destination);
            else
                hitDownLeft(source, destination);
        }
        else{
            if(destination.y < source.y)
                hitUpRight(source, destination);
            else
                hitDownRight(source, destination);
        }
    }

    private void hitUpLeft(Coordinates source, Coordinates destination) throws Exception{
        for(int x = source.x - 1, y = source.y - 1; x > destination.x; x--, y--){
            if(board[y][x] > -1){
                pawns[board[y][x]].isPlaying = false;
                board[y][x] = -1;
                hitsInLastMove.add(new Coordinates(x,y).toInt());
            }
        }
    }
    private void hitUpRight(Coordinates source, Coordinates destination) throws Exception{
        for(int x = source.x + 1, y = source.y - 1; x < destination.x; x++, y--){
            if(board[y][x] > -1){
                pawns[board[y][x]].isPlaying = false;
                board[y][x] = -1;
                hitsInLastMove.add(new Coordinates(x,y).toInt());
            }
        }
    }
    private void hitDownLeft(Coordinates source, Coordinates destination) throws Exception{
        for(int x = source.x - 1, y = source.y + 1; x > destination.x; x--, y++){
            if(board[y][x] > -1){
                pawns[board[y][x]].isPlaying = false;
                board[y][x] = -1;
                hitsInLastMove.add(new Coordinates(x,y).toInt());
            }
        }
    }
    private void hitDownRight(Coordinates source, Coordinates destination) throws Exception{
        for(int x = source.x + 1, y = source.y + 1; x < destination.x; x++, y++){
            if(board[y][x] > -1){
                pawns[board[y][x]].isPlaying = false;
                board[y][x] = -1;
                hitsInLastMove.add(new Coordinates(x,y).toInt());
            }
        }
    }

    @Override
    public String toString() {
        String toReturn= new String();
        for(int i=0;i<8;i++){
            toReturn+=(Arrays.toString(board[i]) + "\n");
        }
        toReturn+="Pawns:";
        for(int i=0;i<pawns.length;i++)
            toReturn+="("+i+"="+pawns[i].position.toString()+")";
        //toReturn+=Arrays.toString(pawns);
        return toReturn;

//		return "Chessboard [board=" + Arrays.toString(board) + "]";
    }

    Boolean isPlayer1(int pawnNumber)throws Exception{
        if(pawnNumber<0 || pawnNumber>23)
            throw new Exception("Bad pawn number!");
        if(pawnNumber<12){
            //	if(isPlayer1white)
            return true;
            //		else
            //		return false;
        }
        else{
            //		if(isPlayer1white)
            return false;
//			else
//				return true;
        }
    }
    Boolean isPlayer2(int pawnNumber) throws Exception{
        return !isPlayer1(pawnNumber);
    }
    Boolean isOtherPlayer(int pawnNumber1,int pawnNumber2)throws Exception{
        if(isPlayer1(pawnNumber1)){
            if(isPlayer2(pawnNumber2))
                return true;
            return false;
        }
        else{
            if(isPlayer1(pawnNumber2))
                return true;
            return false;
        }

    }

    public Boolean isPawnWhite(int pawnNumber) throws Exception{
        if(isPlayer1white){
            if(isPlayer1(pawnNumber))
                return true;
            return false;
        }

        if(isPlayer1(pawnNumber))
            return false;
        return true;
    }


    Boolean isActivePlayersPawn(int pawnNumber) throws Exception{
        if(isPlayer1(pawnNumber)){
            if(activePlayer==1)
                return true;
            return false;
        }
        if(activePlayer==2)
            return true;
        return false;
    }

    Boolean isQueen(int pawnNumber){
        return pawns[pawnNumber].isQueen;
    }

    void setQueen(int pawnNumber){
        pawns[pawnNumber].setAsQuuen();
    }

    public int getPawnNumber(int squareNumber) throws Exception{
        Coordinates squarePosition = new Coordinates(squareNumber);

        return board[squarePosition.y][squarePosition.x];
    }

    public Pawn getPawn(int pawnNumber) throws Exception {
        return pawns[pawnNumber];
    }

    public Vector<Integer> getHitsInLastMove() {
        return hitsInLastMove;
    }
}