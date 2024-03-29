package it.polimi.ingsw.network;

import it.polimi.ingsw.gameInfo.GameInfo;
import it.polimi.ingsw.gameInfo.State;
import it.polimi.ingsw.view.View;

/**
 * This class is a fake view used for testing purposes
 */
public class FakeView extends View {
    /**
     * This method updates the view
     *
     * @param newState  the new state of the game
     * @param newInfo the new info of the game
     */
    public void update(State newState, GameInfo newInfo){
        System.out.println("Received view update");
    }

    /**
     * This method displays a message
     *
     * @param message the message to display
     */
    public void displayChatMessage(String message){
        System.out.println(message);
    }

    /**
     * This method is called by update to display the game
     */
    @Override
    protected void display() {

    }

    /**
     * This method is called by getUserInput to welcome the player
     */
    @Override
    protected void welcome() {

    }

    /**
     * This method is called by getUserInput to wait for other players to join the game
     */
    @Override
    protected void waitForGameStart() {

    }

    /**
     * This method is called by start to wait for a command from the player
     *
     * @return the command represented as a string
     */
    @Override
    protected String waitCommand() {
        return null;
    }

    /**
     * This method is called by waitCommand to parse the command and call the right method
     *
     * @param command the command to parse
     */
    @Override
    protected void parseCommand(String command) {

    }

    /**
     * This method is called by start to ask the player if he wants to connect via rmi or socket
     */
    @Override
    protected void chooseConnectionType() {

    }

    /**
     * This method is called by start to ask the player his nickname and send it to the server
     */
    @Override
    protected void askNickname() {

    }

    /**
     * This method is called by start to ask the player if he wants to create a new game or join an existing one
     */
    @Override
    protected void createOrJoinGame() {

    }

    /**
     * This method is called by start to ask the player if he wants to play again
     *
     * @return true if the player wants to play again, false otherwise
     */
    @Override
    protected boolean askIfWantToPlayAgain() {
        return false;
    }

    /**
     * This method is called by close to notify the player that the client is shutting down
     *
     * @param message the message to display
     */
    @Override
    protected void notifyClose(String message) {

    }
}
