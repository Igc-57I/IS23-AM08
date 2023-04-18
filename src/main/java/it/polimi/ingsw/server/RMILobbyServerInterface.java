package it.polimi.ingsw.server;

import it.polimi.ingsw.client.RmiClientInterface;
import it.polimi.ingsw.server.exceptions.*;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface contains the methods that can be called on the client at the start of the application
 */
public interface RMILobbyServerInterface extends Remote {

    public boolean chooseNickname(String nickname) throws RemoteException, ExistentNicknameExcepiton, IllegalNicknameException;

    public RmiServer createGame(Integer numPlayers, String nickname, RmiClientInterface client) throws RemoteException, AlreadyInGameException, NonExistentNicknameException;

    public RmiServer joinGame(String nickname, RmiClientInterface client) throws RemoteException, NoGamesAvailableException, AlreadyInGameException, NonExistentNicknameException;

}
