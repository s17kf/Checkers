package GameLogic;

/**
 * Created by Stefan on 2017-05-19.
 */


public class LogicTest{

    public static void main(String[] args)throws Exception{
        Chessboard cb=new Chessboard(false);
        System.out.println(cb.toString());
//		System.out.println(cb.numberOfHitsToString());

        cb.updatePossibleMoves();

        System.out.println(cb.possibleMoves(3, 2).toString());

        System.out.println(cb.possibleMoves(0, 5).toString());
        System.out.println(cb.possibleMoves(1, 2).toString());
        System.out.println(cb.possibleMoves(2, 5).toString());
        System.out.println(cb.possibleMoves(4, 5).toString());
        System.out.println(cb.possibleMoves(6, 5).toString());
//		System.out.println(cb.possibleMoves(3, 6).toString());
//		System.out.println(cb.possibleMoves(7, 2).toString());
//		System.out.println(cb.possibleMoves(3, 0).toString());
//
//		Pawn pawn=new Pawn(10);
//		System.out.println(pawn.position);


        System.out.println(cb.possibleMoves(3, 2).toString());
        cb.movePawnTo(10, new Coordinates(28));

        System.out.println(cb.toString());
        System.out.println(cb.possibleMoves(3, 2).toString());

        cb.movePawnTo(9, new Coordinates(37));
        System.out.println(cb.possibleMoves(3, 2).toString());

        cb.movePawnTo(20, new Coordinates(26));
        cb.movePawnTo(23, new Coordinates(30));
        System.out.println(cb.possibleMoves(3, 2).toString());

        System.out.println(cb.toString());

        cb.setQueen(23);
        cb.setQueen(20);
        System.out.println(cb.possibleMoves(23));
        System.out.println(cb.possibleMoves(20));
        cb.movePawnTo(5, new Coordinates(44));
        System.out.println(cb.possibleMoves(20));

        System.out.println(cb.toString());


//		System.out.println(cb.numberOfHitsToString());


    }


}