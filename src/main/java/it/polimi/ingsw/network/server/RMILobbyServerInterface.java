package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.client.RmiClientInterface;
import it.polimi.ingsw.network.server.exceptions.*;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface contains the methods that can be called on the client at the start of the application
 */
public interface RMILobbyServerInterface extends Remote {

    public boolean chooseNickname(String nickname) throws RemoteException, ExistentNicknameExcepiton, IllegalNicknameException;

    public String createGame(Integer numPlayers, String nickname, RmiClientInterface client) throws RemoteException, AlreadyInGameException, NonExistentNicknameException;

    public String joinGame(String nickname, RmiClientInterface client) throws RemoteException, NoGamesAvailableException, AlreadyInGameException, NonExistentNicknameException;

}