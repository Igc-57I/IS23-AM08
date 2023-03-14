package it.polimi.ingsw;

/**
 *
 */
public class GameBoard4Player extends GameBoard{
    /**
     *
     */
    public GameBoard4Player(){
        super();
    }
    /**
     *
     */
    @Override
    protected int[][] createValidPositions(){
        return new int[][]
                {
                        { 0, 0, 0, 1, 1, 0, 0, 0, 0 },
                        { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
                        { 0, 0, 1, 1, 1, 1, 1, 0, 0 },
                        { 0, 1, 1, 1, 1, 1, 1, 1, 1 },
                        { 1, 1, 1, 1, 1, 1, 1, 1, 1 },
                        { 1, 1, 1, 1, 1, 1, 1, 1, 0 },
                        { 0, 0, 1, 1, 1, 1, 1, 0, 0 },
                        { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
                        { 0, 0, 0, 0, 1, 1, 0, 0, 0 },
                };
    }

    /**
     *
     */
    @Override
    protected void fillPointStack() {
        for(CommonObjective co: super.commonObjectives){
            co.push(2);
            co.push(4);
            co.push(6);
            co.push(8);
        }
    }

    /**
     *
     */
    @Override
    protected int[][] createInvalidPositions() {
        return new int[][]
                {
                        { 1, 1, 1, 0, 0, 1, 1, 1, 1 },
                        { 1, 1, 1, 0, 0, 0, 1, 1, 1 },
                        { 1, 1, 0, 0, 0, 0, 0, 1, 1 },
                        { 1, 0, 0, 0, 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 0, 1 },
                        { 1, 1, 0, 0, 0, 0, 0, 1, 1 },
                        { 1, 1, 1, 0, 0, 0, 1, 1, 1 },
                        { 1, 1, 1, 1, 0, 0, 1, 1, 1 },
                };
    }
}

