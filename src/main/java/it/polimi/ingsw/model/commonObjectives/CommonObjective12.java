package it.polimi.ingsw.model.commonObjectives;

import it.polimi.ingsw.model.Library;
import it.polimi.ingsw.model.Position;
import it.polimi.ingsw.model.constants.AppConstants;

/**
 * This class implements the twelve common objective: ladder in the card
 */
public class CommonObjective12 extends CommonObjective{
    /**
     * This method needs to find the ladder in the library.
     * It starts with the search of the starting point, which has to be in the first two rows, because if not the ladder will not be full
     * Then it goes in one direction or the other (depending on the starting column) and it checks if there is effectively a ladder
     * The ladder is when i have one tile and the other (left/right depending on the moving direction) is empty
     * @param x library of the player
     * @return true if the ladder is found
     */
    @Override
    public boolean evaluate(Library x) {
        int startingRow=0;
        int startingCol=0;

        if(x.getCard(new Position(startingCol,startingRow)).isEmpty()){
            //next row, if empty too then change starting position
            startingRow++;
            if(x.getCard(new Position(startingCol,startingRow)).isEmpty()){
                //change column position
                startingRow--;
                startingCol= AppConstants.COLS_NUMBER-1;
                if(x.getCard(new Position(startingCol,startingRow)).isEmpty()){
                    //if empty too then go next row
                    startingRow++;
                    if(x.getCard(new Position(startingCol,startingRow)).isEmpty()){
                        //if arrived here then it is impossible to do the ladder
                        return false;
                    }
                }
            }
        }
        //now we have our starting row and column, we need to check if the ladder is done
        int j=startingRow;
        if(startingCol==0){
            //save the starting row
            //move to the right
            for(int i=startingCol;i<AppConstants.COLS_NUMBER-1;i++,j++){
                //for every column if the one to the right of i is not empty then the column is not done
                if(!x.getCard(new Position(i+1,j)).isEmpty()) return false;
                //and we also need to check that the current position is not empty
                if(x.getCard(new Position(i,j)).isEmpty()) return false;
            }
            //if everything passed correctly then it means that only the last row has to be checked, so we need to control the last bit on the bottom right
            return !x.getCard(new Position(AppConstants.COLS_NUMBER - 1, j)).isEmpty();
        }
        //do the same but mirrored
        for(int i=startingCol;i>0;i--,j++){
            if(!x.getCard(new Position(i-1,j)).isEmpty()) return false;
            if(x.getCard(new Position(i,j)).isEmpty()) return false;
        }

        return !x.getCard(new Position(0, j)).isEmpty();

    }
}