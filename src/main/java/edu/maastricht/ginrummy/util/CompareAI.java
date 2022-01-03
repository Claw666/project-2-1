package edu.maastricht.ginrummy.util;

public class CompareAI {
    private static int wins;
    private static int pointsAgent;
    private static int pointsOpponent;
    private static int [] result = new int[3];
    private static int runs = 1;

    //give in as parameters the algorithm, run it 'runs' times , and put the results in the compare method.

    public static void getResult() {
            pointsAgent = 0;
            pointsOpponent = 0;
            for (int i = 0; i < runs; i++) {
                //run algorithm a, so a.compare()
                //example below:
                compare(true,15,5);
            }
    }

    public static int [] compare(boolean winOrLose, int pointsWinner, int pointsLoser){
        // result for 1 run of algorithm a{
        if(winOrLose){
            wins = wins +1;
            pointsAgent = pointsAgent + pointsWinner;
            pointsOpponent = pointsOpponent + pointsLoser;
        }
        else{
            pointsAgent = pointsAgent + pointsLoser;
            pointsOpponent = pointsOpponent + pointsWinner;

        }
        result[0]= wins;
        result[1]= pointsAgent;
        result[2]= pointsOpponent;
        for(int i =0; i<result.length; i++){
            System.out.println(result[i]);
        }
        return result;

    }
    public static  void main(String args[]){
        getResult();
    }
}






