package s17kf.checkers.GameLogic;

/**
 * Created by Stefan on 2017-05-19.
 */

import java.util.Vector;


public class Chessboard extends PrimitiveChessboard{

    Vector<Integer> hitsInLastMove;
    private Boolean isHitContinuation;

    public Chessboard(Boolean isPlayer1White) throws Exception{
        super(isPlayer1White);
        activePlayer = 1;
        isHitContinuation = false;
        hitsInLastMove = new Vector<>();
    }

    @Override
    public void updatePossibleMoves() throws Exception{
        int activePlayerShift = activePlayer == 1 ? 0 : 12;
        isPossibleHit=false;
        for(int i = 0 + activePlayerShift; i < 12 + activePlayerShift; i++) {
            if(pawns[i].isPlaying)
                updatePossibleMoves(i);
        }
        if(isPossibleHit){
            int lengthOfLongestHit = 0;
            for(int i = 0 + activePlayerShift; i < 12 + activePlayerShift; i++){
                lengthOfLongestHit = Math.max(lengthOfLongestHit, leaveLongestHits(i));
            }
            for(int i = 0 + activePlayerShift; i < 12 + activePlayerShift; i++){
                if(!pawns[i].getPossibleMoves().isEmpty()){
                    if(pawns[i].getPossibleMoves().get(0).getCountOfPossibleHits() < lengthOfLongestHit)
                        pawns[i].resetPossibleMoves();
                }
            }
        }
    }

    private int leaveLongestHits(int pawnNumber) throws Exception{
        Pawn checkedPawn = pawns[pawnNumber];
        checkedPawn.leaveOnlyHitsAsPossibleMoves();
        Coordinates originalPosition = new Coordinates(checkedPawn.getPosition());
        Vector<PawnPossibleMove> originalPossibleMoves = new Vector<>(checkedPawn.getPossibleMoves());
        for(PawnPossibleMove possibleMove : originalPossibleMoves){
            checkedPawn.setPosition(originalPosition);
            Vector<Integer> hittedPawns = new Vector<>();
            while (checkedPawn.goOneTo(possibleMove)){
                if(board[checkedPawn.getY()][checkedPawn.getX()] > -1){
                    hittedPawns.add(board[checkedPawn.getY()][checkedPawn.getX()]);
                }
            }
            possibleMove.setCountOfPossibleHits(countHits(pawnNumber, checkedPawn, hittedPawns));
        }
        checkedPawn.setPosition(originalPosition);
        int lengthOfLongestHit = 0;
        for(PawnPossibleMove possibleMove : originalPossibleMoves){
            lengthOfLongestHit = Math.max(lengthOfLongestHit, possibleMove.getCountOfPossibleHits());
        }
        Vector<PawnPossibleMove> newPossibleMoves = new Vector<>();
        for(PawnPossibleMove possibleMove : originalPossibleMoves){
            if((possibleMove.getCountOfPossibleHits()) == lengthOfLongestHit)
                newPossibleMoves.add(possibleMove);
        }
        checkedPawn.setPossibleMoves(newPossibleMoves);
        return lengthOfLongestHit;
    }

    private int countHits(int pawnNumber, Pawn checkedPawn, Vector<Integer> hittedPawns) throws Exception{
        updatePossibleMoves(pawnNumber, checkedPawn, true, hittedPawns);
        checkedPawn.leaveOnlyHitsAsPossibleMoves();
        if(checkedPawn.getPossibleMoves().isEmpty())
            return 1;

        int bestResult = 0;
        Coordinates originalPosition = new Coordinates(checkedPawn.getPosition());
        Vector<Coordinates> originalPossibleMoves = new Vector<>(checkedPawn.getPossibleMoves());
        Vector<Integer> originalHittedPawns = new Vector<>(hittedPawns);
        for(Coordinates possibleMove : originalPossibleMoves){
            checkedPawn.setPosition(originalPosition);
            hittedPawns = originalHittedPawns;
            while(checkedPawn.goOneTo(possibleMove)){
                if(board[checkedPawn.getY()][checkedPawn.getX()] > -1){
                    hittedPawns.add((board[checkedPawn.getY()][checkedPawn.getX()]));
                }
            }
            bestResult = Math.max(bestResult, countHits(pawnNumber, checkedPawn,new Vector<>(hittedPawns)));
        }


        return bestResult + 1;

    }


    private void updatePossibleMoves(int pawnNumber, Pawn pawn, Boolean isItForCountHits, Vector<Integer> hittedPawns) throws Exception{
        pawn.resetPossibleMoves();
        Coordinates pawnPosition = new Coordinates(pawn.getPosition());
        if(isQueen(pawnNumber)){
            if(pawnPosition.goUpLeft()) {
                addQueenPossibleUpLeft(pawnNumber, new Coordinates(pawnPosition), false, isItForCountHits, hittedPawns);

                pawnPosition.goDownRight();
            }
            if(pawnPosition.goUpRight()) {
                addQueenPossibleUpRight(pawnNumber, new Coordinates(pawnPosition), false, isItForCountHits, hittedPawns);

                pawnPosition.goDownLeft();
            }
            if(pawnPosition.goDownLeft()) {
                addQueenPossibleDownLeft(pawnNumber, new Coordinates(pawnPosition), false, isItForCountHits, hittedPawns);

                pawnPosition.goUpRight();
            }
            if(pawnPosition.goDownRight()) {
                addQueenPossibleDownRight(pawnNumber, new Coordinates(pawnPosition), false, isItForCountHits, hittedPawns);

                pawnPosition.goUpLeft();
            }
        }
        else{
            if(isPlayer1(pawnNumber)){
                if(pawnPosition.goUpLeft()){
                    if(!isSquareEngaged(pawnPosition))
                        pawn.addPossibleMove(pawnPosition, false);
                    pawnPosition.goDownRight();
                }
                if(pawnPosition.goUpRight()){
                    if(!isSquareEngaged(pawnPosition))
                        pawn.addPossibleMove(pawnPosition, false);
                    pawnPosition.goDownLeft();
                }
            }
            else{
                if(pawnPosition.goDownLeft()){
                    if(!isSquareEngaged(pawnPosition))
                        pawn.addPossibleMove(pawnPosition, false);
                    pawnPosition.goUpRight();
                }
                if(pawnPosition.goDownRight()){
                    if(!isSquareEngaged(pawnPosition))
                        pawn.addPossibleMove(pawnPosition, false);
                    pawnPosition.goUpLeft();
                }
            }
            if(!isItForCountHits) {
                if (checkHitUpLeft(pawnNumber)) {
                    pawn.addPossibleMove(pawnPosition.x - 2, pawnPosition.y - 2, true);
                    isPossibleHit = true;
                }
                if (checkHitUpRight(pawnNumber)) {
                    pawn.addPossibleMove(pawnPosition.x + 2, pawnPosition.y - 2, true);
                    isPossibleHit = true;
                }
                if (checkHitDownLeft(pawnNumber)) {
                    pawn.addPossibleMove(pawnPosition.x - 2, pawnPosition.y + 2, true);
                    isPossibleHit = true;
                }
                if (checkHitDownRight(pawnNumber)) {
                    pawn.addPossibleMove(pawnPosition.x + 2, pawnPosition.y + 2, true);
                    isPossibleHit = true;
                }
            }
            else{
                Coordinates possibleHittedSquare = new Coordinates(pawnPosition);
                if(possibleHittedSquare.goUpLeft()) {
                    if (checkHitUpLeft(pawnNumber, possibleHittedSquare, true, hittedPawns)) {
                        pawn.addPossibleMove(pawnPosition.x - 2, pawnPosition.y - 2, true);
                    }
                    possibleHittedSquare.goDownRight();
                }
                if(possibleHittedSquare.goUpRight()) {
                    if (checkHitUpRight(pawnNumber, possibleHittedSquare, true, hittedPawns)) {
                        pawn.addPossibleMove(pawnPosition.x + 2, pawnPosition.y - 2, true);
                    }
                    possibleHittedSquare.goDownLeft();
                }
                if(possibleHittedSquare.goDownLeft()) {
                    if (checkHitDownLeft(pawnNumber, possibleHittedSquare, true, hittedPawns)) {
                        pawn.addPossibleMove(pawnPosition.x - 2, pawnPosition.y + 2, true);
                    }
                    possibleHittedSquare.goUpRight();
                }
                if(possibleHittedSquare.goDownRight()) {
                    if (checkHitDownRight(pawnNumber, possibleHittedSquare, true, hittedPawns)) {
                        pawn.addPossibleMove(pawnPosition.x + 2, pawnPosition.y + 2, true);
                    }
                    possibleHittedSquare.goUpLeft();
                }
            }
        }
    }

    @Override
    protected void updatePossibleMovesForContinuation(int pawnNumber) throws Exception{
        for(Pawn pawn:pawns){
            pawn.resetPossibleMoves();
        }
        updatePossibleMoves(pawnNumber);
        leaveLongestHits(pawnNumber);
        if(!pawns[pawnNumber].getPossibleMoves().isEmpty())
            isHitContinuation = true;
    }


    private void addQueenPossibleUpLeft(int pawnNumber,Coordinates checkedSquare, Boolean isHit, Boolean isItForCountHits, Vector<Integer> hittedPawns) throws Exception{
        if(!isSquareEngaged(checkedSquare)){
            pawns[pawnNumber].addPossibleMove(checkedSquare, isHit);
            if(checkedSquare.goUpLeft())
                addQueenPossibleUpLeft(pawnNumber, new Coordinates(checkedSquare), isHit, isItForCountHits, hittedPawns);
            return;
        }
        if(!isItForCountHits) {
            if (checkHitUpLeft(pawnNumber, checkedSquare)) {
                isPossibleHit = true;
                checkedSquare.goUpLeft();
                pawns[pawnNumber].addPossibleMove(checkedSquare, true);
                if (checkedSquare.goUpLeft())
                    addQueenPossibleUpLeft(pawnNumber, new Coordinates(checkedSquare), true, false, hittedPawns);
            }
        }
        else{
            if(checkHitUpLeft(pawnNumber,checkedSquare, true, hittedPawns) ){
                checkedSquare.goUpLeft();
                pawns[pawnNumber].addPossibleMove(checkedSquare, true);
                if(checkedSquare.goUpLeft())
                    addQueenPossibleUpLeft(pawnNumber, new Coordinates(checkedSquare), true, true, hittedPawns);
            }
        }
    }
    private void addQueenPossibleUpRight(int pawnNumber,Coordinates checkedSquare, Boolean isHit, Boolean isItForCountHits, Vector<Integer> hittedPawns) throws Exception{
        if(!isSquareEngaged(checkedSquare)){
            pawns[pawnNumber].addPossibleMove(checkedSquare, isHit);
            if(checkedSquare.goUpRight())
                addQueenPossibleUpRight(pawnNumber, new Coordinates(checkedSquare), isHit, isItForCountHits, hittedPawns);
            return;
        }
        if(!isItForCountHits) {
            if (checkHitUpRight(pawnNumber, checkedSquare)) {
                isPossibleHit = true;
                checkedSquare.goUpRight();
                pawns[pawnNumber].addPossibleMove(checkedSquare, true);
                if (checkedSquare.goUpRight())
                    addQueenPossibleUpRight(pawnNumber, new Coordinates(checkedSquare), true, false, hittedPawns);
            }
        }
        else{
            if(checkHitUpRight(pawnNumber,checkedSquare, true, hittedPawns)){
                checkedSquare.goUpRight();
                pawns[pawnNumber].addPossibleMove(checkedSquare, true);
                if(checkedSquare.goUpRight())
                    addQueenPossibleUpRight(pawnNumber,new Coordinates(checkedSquare), true, true, hittedPawns);
            }
        }
    }
    private void addQueenPossibleDownLeft(int pawnNumber,Coordinates checkedSquare, Boolean isHit, Boolean isItForCountHits, Vector<Integer> hittedPawns) throws Exception{
        if(!isSquareEngaged(checkedSquare)){
            pawns[pawnNumber].addPossibleMove(checkedSquare, isHit);
            if(checkedSquare.goDownLeft())
                addQueenPossibleDownLeft(pawnNumber, new Coordinates(checkedSquare), isHit, isItForCountHits, hittedPawns);
            return;
        }
        if(!isItForCountHits) {
            if (checkHitDownLeft(pawnNumber, checkedSquare)) {
                isPossibleHit = true;
                checkedSquare.goDownLeft();
                pawns[pawnNumber].addPossibleMove(checkedSquare, true);
                if (checkedSquare.goDownLeft())
                    addQueenPossibleDownLeft(pawnNumber, new Coordinates(checkedSquare), true, false, hittedPawns);
            }
        }
        else{
            if(checkHitDownLeft(pawnNumber, checkedSquare, true, hittedPawns)){
                checkedSquare.goDownLeft();
                pawns[pawnNumber].addPossibleMove(checkedSquare, true);
                if(checkedSquare.goDownLeft())
                    addQueenPossibleDownLeft(pawnNumber,new Coordinates(checkedSquare), true, true, hittedPawns);
            }
        }
    }
    private void addQueenPossibleDownRight(int pawnNumber,Coordinates checkedSquare, Boolean isHit, Boolean isItForCountHits, Vector<Integer> hittedPawns) throws Exception{
        if(!isSquareEngaged(checkedSquare)){
            pawns[pawnNumber].addPossibleMove(checkedSquare, isHit);
            if(checkedSquare.goDownRight())
                addQueenPossibleDownRight(pawnNumber, new Coordinates(checkedSquare), isHit, isItForCountHits, hittedPawns);
            return;
        }
        if(!isItForCountHits) {
            if (checkHitDownRight(pawnNumber, checkedSquare)) {
                isPossibleHit = true;
                checkedSquare.goDownRight();
                pawns[pawnNumber].addPossibleMove(checkedSquare, true);
                if (checkedSquare.goDownRight())
                    addQueenPossibleDownRight(pawnNumber, new Coordinates(checkedSquare), true, false, hittedPawns);
            }
        }
        else{
            if (checkHitDownRight(pawnNumber, checkedSquare, true, hittedPawns)) {
                checkedSquare.goDownRight();
                pawns[pawnNumber].addPossibleMove(checkedSquare, true);
                if (checkedSquare.goDownRight())
                    addQueenPossibleDownRight(pawnNumber, new Coordinates(checkedSquare), true, true, hittedPawns);
            }
        }
    }


    private Boolean checkHitUpLeft(int pawnNumber, Coordinates hittedSquare, Boolean isItForCountHits, Vector<Integer> hittedPawns) throws Exception{
        if(hittedSquare.getX() > 0 && hittedSquare.getY() > 0){
            if(isSquareEngaged(hittedSquare) && isOtherPlayer(board[hittedSquare.getY()][hittedSquare.getX()],pawnNumber)){
                if(isItForCountHits){
                    if(hittedPawns.contains(getPawnNumber(hittedSquare.toInt())))
                        return false;
                    if(!isSquareEngaged(hittedSquare.getX() - 1 , hittedSquare.getY() - 1) || (board[hittedSquare.getY() - 1][hittedSquare.getX() - 1] == pawnNumber))
                        return true;
                    return false;
                }
                if(!isSquareEngaged(hittedSquare.getX() - 1, hittedSquare.getY() - 1)){
                    return true;
                }
            }
        }
        return false;
    }
    private Boolean checkHitUpRight(int pawnNumber, Coordinates hittedSquare, Boolean isItForCountHits, Vector<Integer> hittedPawns) throws Exception{
        if(hittedSquare.getX() < 7 && hittedSquare.getY() > 0){
            if(isSquareEngaged(hittedSquare) && isOtherPlayer(board[hittedSquare.getY()][hittedSquare.getX()],pawnNumber)){
                if(isItForCountHits){
                    if(hittedPawns.contains(getPawnNumber(hittedSquare.toInt())))
                        return false;
                    if(!isSquareEngaged(hittedSquare.getX() + 1 , hittedSquare.getY() - 1) || (board[hittedSquare.getY() - 1][hittedSquare.getX() + 1] == pawnNumber))
                        return true;
                    return false;
                }
                if(!isSquareEngaged(hittedSquare.getX() + 1, hittedSquare.getY() - 1)){
                    return true;
                }
            }
        }
        return false;
    }
    private Boolean checkHitDownLeft(int pawnNumber, Coordinates hittedSquare, Boolean isItForCountHits, Vector<Integer> hittedPawns) throws Exception{
        if(hittedSquare.getX() > 0 && hittedSquare.getY() < 7){
            if(isSquareEngaged(hittedSquare) && isOtherPlayer(board[hittedSquare.getY()][hittedSquare.getX()],pawnNumber)){
                if(isItForCountHits){
                    if(hittedPawns.contains(getPawnNumber(hittedSquare.toInt())))
                        return false;
                    if(!isSquareEngaged(hittedSquare.getX() - 1 , hittedSquare.getY() + 1) || (board[hittedSquare.getY() + 1][hittedSquare.getX() - 1] == pawnNumber))
                        return true;
                    return false;
                }
                if(!isSquareEngaged(hittedSquare.getX() - 1, hittedSquare.getY() + 1)){
                    return true;
                }
            }
        }
        return false;
    }
    private Boolean checkHitDownRight(int pawnNumber, Coordinates hittedSquare, Boolean isItForCountHits, Vector<Integer> hittedPawns) throws Exception{
        if(hittedSquare.getX() < 7 && hittedSquare.getY() < 7){
            if(isSquareEngaged(hittedSquare) && isOtherPlayer(board[hittedSquare.getY()][hittedSquare.getX()],pawnNumber)){
                if(isItForCountHits){
                    if(hittedPawns.contains(getPawnNumber(hittedSquare.toInt())))
                        return false;
                    if(!isSquareEngaged(hittedSquare.getX() + 1 , hittedSquare.getY() + 1)  || (board[hittedSquare.getY() + 1][hittedSquare.getX() + 1] == pawnNumber))
                        return true;
                    return false;
                }
                if(!isSquareEngaged(hittedSquare.getX() + 1, hittedSquare.getY() + 1)){
                    return true;
                }
            }
        }
        return false;
    }


    public Vector<Integer> possibleMoves(int x,int y) throws Exception{
        updatePossibleMoves(board[y][x]);
        if(isSquareEngaged(x, y))
            return pawns[board[y][x]].getPossibleMovesAssIntegers();
        throw new Exception("Try to get possible moves of empty square!");
    }

    public Vector<Integer> possibleMoves(int pawnNumber) throws Exception{
        return possibleMoves(pawns[pawnNumber].position.x,pawns[pawnNumber].position.y);
    }

    @Override
    public void movePawnTo(int pawnNumber, Coordinates destination) throws Exception{
        hitsInLastMove = new Vector<>();
        isHitContinuation = false;

        Boolean isHitInMove=false;
        for( PawnPossibleMove possibleMove: pawns[pawnNumber].possibleMoves){
            if(possibleMove.getX() == destination.getX() && possibleMove.getY() == destination.getY()){
                isHitInMove = possibleMove.isHit;
                break;
            }
        }
        Coordinates source=pawns[pawnNumber].getPosition();

        board[source.y][source.x]=-1;
        board[destination.y][destination.x]=pawnNumber;
        pawns[pawnNumber].setPosition(destination);
        resetPossibleMoves();

        realizeHitsAndSetQueens(isHitInMove, source, destination, pawnNumber);


    }

    public void moveFromSquareToSquare(int sourceSquareNumber, int destinationSquareNumber)throws Exception{
        movePawnTo(getPawnNumber(sourceSquareNumber),new Coordinates(destinationSquareNumber));
    }

    @Override
    protected void hitUpLeft(Coordinates source, Coordinates destination) throws Exception{
        for(int x = source.x - 1, y = source.y - 1; x > destination.x; x--, y--){
            if(board[y][x] > -1){
                pawns[board[y][x]].isPlaying = false;
                board[y][x] = -1;
                hitsInLastMove.add(new Coordinates(x,y).toInt());
            }
        }
    }
    @Override
    protected void hitUpRight(Coordinates source, Coordinates destination) throws Exception{
        for(int x = source.x + 1, y = source.y - 1; x < destination.x; x++, y--){
            if(board[y][x] > -1){
                pawns[board[y][x]].isPlaying = false;
                board[y][x] = -1;
                hitsInLastMove.add(new Coordinates(x,y).toInt());
            }
        }
    }
    @Override
    protected void hitDownLeft(Coordinates source, Coordinates destination) throws Exception{
        for(int x = source.x - 1, y = source.y + 1; x > destination.x; x--, y++){
            if(board[y][x] > -1){
                pawns[board[y][x]].isPlaying = false;
                board[y][x] = -1;
                hitsInLastMove.add(new Coordinates(x,y).toInt());
            }
        }
    }
    @Override
    protected void hitDownRight(Coordinates source, Coordinates destination) throws Exception{
        for(int x = source.x + 1, y = source.y + 1; x < destination.x; x++, y++){
            if(board[y][x] > -1){
                pawns[board[y][x]].isPlaying = false;
                board[y][x] = -1;
                hitsInLastMove.add(new Coordinates(x,y).toInt());
            }
        }
    }

    public Vector<Integer> getHitsInLastMove() {
        return hitsInLastMove;
    }

    public int getPawnPosition(int pawnNumber) throws Exception{
        return getPawn(pawnNumber).getPosition().toInt();
    }

    public Boolean getHitContinuation() {
        return isHitContinuation;
    }
}