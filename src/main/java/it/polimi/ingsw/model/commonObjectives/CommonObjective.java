package it.polimi.ingsw.model.commonObjectives;

import it.polimi.ingsw.model.Library;

import java.util.Stack;

// Who works on what:  exemple (12: X)  (1: O)                  GabriCarr == X
// COLUMN 1) --  1: O  2: O  3: O  4:  O  5: O   6: O           GabriCarm == O
// COLUMN 2) --  7: O  8: O  9: X  10: X  11: Z  12: Z          MatteCenz == Z


/**
 * This is the class that represents the common objective
 * It is abstract since every objective will be an extension of this class following the pattern Strategy, overriding the metod for the evaluation of the objective
 */
public abstract class CommonObjective {

    /**
     * This Stack is the data structure of the points available for grabbing in the common objective
     */
    private final Stack<Integer> pointStack;

    /**
     * The constructor just initializes the stack, since the addition of the numbers will be done in the GameBoard class
     */
    public CommonObjective(){
        pointStack=new Stack<>();
    }

    /**
     * This method is the push function for a usual stack
     * @param i integer to push in the stack
     */
    public void push(Integer i){
        this.pointStack.push(i);
    }

    /**
     * This method is the push function for a usual stack
     * @return pop of the integer from the stack
     */
    public Integer pop(){
        return this.pointStack.pop();
    }

    /**
     * This method has to be overrided in its subclasses, with each implementation being a different algorithm to calculate the objective
     * @param library Library of the current player
     * @return true if the objective has been satisfied
     */
    public abstract boolean evaluate(Library library);

}
