package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.exceptions.InvalidIdException;
import it.polimi.ingsw.controller.exceptions.InvalidMoveException;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.Position;
import it.polimi.ingsw.model.observers.GameStateObserver;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private final GameModel model;
    private final List<String> playersList;
    private final int numPlayers;

    private final GameStateObserver gameStateObserver;

    public GameController(List<String> nicknames, int numPlayers){
        playersList = new ArrayList<>(nicknames);
        this.numPlayers = numPlayers;
        this.model = new GameModel(numPlayers, nicknames);
        this.gameStateObserver = new GameStateObserver();
        this.model.addObserver(gameStateObserver);

    }

    public GameController(GameController controller){
        this.playersList = controller.playersList;
        this.numPlayers = controller.numPlayers;
        this.model = new GameModel(controller.model);
        this.gameStateObserver = new GameStateObserver();
        this.model.addObserver(gameStateObserver);
    }

    private boolean verifyPlayerNickname(String nick){
        return nick.equals(this.model.getPlayer().getNickname());
    }

    private boolean evaluationMove(List<Position> pos, int col){

        return model.checkValidMove(pos) && model.checkValidColumn(col, pos.size());
    }

    public void makeMove(List<Position> pos, int col, String nick) throws InvalidIdException, InvalidMoveException {
        if(!verifyPlayerNickname(nick)){
            throw new InvalidIdException();
        }
        if(!evaluationMove(pos, col)){
           throw new InvalidMoveException();
        }
        model.makeMove(pos, col);
        model.nextTurn();
    }

    public int getCurrentPlayer(){
        return this.model.getCurrentPlayer();
    }

    /*public State getGameState(){
        return gameStateObserver.getCurrentState();
    }

     */

    /*public GameInfo getNewInfo(){
        return new GameInfo(this.model);
    }

     */




}
