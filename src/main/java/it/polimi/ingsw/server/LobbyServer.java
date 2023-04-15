package it.polimi.ingsw.server;

import it.polimi.ingsw.client.RmiClientInterface;
import it.polimi.ingsw.model.GameModel;
import it.polimi.ingsw.model.constants.AppConstants;
import it.polimi.ingsw.model.utilities.JsonWithExposeSingleton;
import it.polimi.ingsw.server.constants.ServerConstants;
import it.polimi.ingsw.server.exceptions.*;

import java.io.*;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class sets up the main server which will make the player set his name and choose a game to join
 */
public class LobbyServer extends UnicastRemoteObject implements RMILobbyServerInterface {
    /**
     * Set that contains all the nicknames of every client connected
     */
    private final Set<String> nicknamesPool;
    /**
     * Set that contains all the nicknames of every client that is currently playing a game
     */
    private final Set<String> nicknamesInGame;
    private final Map<String, ConnectionInformationRMI> potentialPlayers;
    /**
     * List of all the games currently active in the application
     */
    private final List<RmiServer> serverList;
    /**
     * List of all the game information present in the application
     */
    private final List<ConnectionInformationRMI> serverInformation;
    /**
     * List of all the game registries present in the application
     */
    private final List<Registry> serverRegistries;
    /**
     * Setup information of the server
     * Can be loaded from file
     */
    private final LobbyServerConfig config;
    /**
     * List loaded from file that contains all the words that cannot be used as nicknames for the players
     * Useful for avoiding ambiguites when calling some commands (especially from cli)
     */
    private final List<String> banList;

    /**
     * Object used as a lock for the insertion of the name in the list of chosen nicknames
     */
    private final Object lockChooseNickName;
    /**
     * Object used as a lock for the creation and joining of a game
     */
    private final Object lockCreateGame;
    /**
     * Registry containing the main part of LobbyServer
     */
    private Registry registry;


    /**
     * Constructor that loads the initial configuration of the server from file
     * @throws RemoteException exception of RMI
     */
    public LobbyServer() throws RemoteException{
        super();
        this.config = loadInitialConfig();
        this.nicknamesPool = new HashSet<>();
        this.nicknamesInGame = new HashSet<>();
        this.potentialPlayers = new HashMap<>();
        this.serverList = new ArrayList<>();
        this.serverInformation = new ArrayList<>();
        this.serverRegistries = new ArrayList<>();
        this.banList = new ArrayList<>();
        this.banList.addAll(loadBanList());
        lockChooseNickName=new Object();
        lockCreateGame=new Object();
    }


    /**
     * Constructor that loads the initial configuration from the object in input
     * @param config configuration of the server
     * @throws RemoteException exception of RMI
     */
    public LobbyServer(LobbyServerConfig config) throws RemoteException{
        super();
        this.config = config;
        this.nicknamesPool = new HashSet<>();
        this.nicknamesInGame = new HashSet<>();
        this.potentialPlayers = new HashMap<>();
        this.serverList = new ArrayList<>();
        this.serverInformation = new ArrayList<>();
        this.serverRegistries = new ArrayList<>();
        this.banList = new ArrayList<>();
        this.banList.addAll(loadBanList());
        lockChooseNickName=new Object();
        lockCreateGame=new Object();
    }


    /**
     * Constructor that receives in input the parameters and creates a configuration manually
     * @param serverPort integer containing the information of the server port in RMI (the TCP will be automatically be +1)
     * @param serverName string containing the information of the server name
     * @param startingPort integer containing the information of the starting port
     * @param startingName string containing the information of the starting name
     * @throws RemoteException exception of RMI
     */
    public LobbyServer(int serverPort, String serverName, int startingPort, String startingName) throws RemoteException{
        this(new LobbyServerConfig(serverPort, serverPort+1, serverName, startingPort, startingName));
    }

    /**
     * Constructor that receives in input the parameters and creates a configuration manually
     * @param serverPortRMI integer containing the information of the server port in RMI
     * @param serverPortTCP integer containing the information of the server port in RMI
     * @param serverName string containing the information of the server name
     * @param startingPort integer containing the information of the starting port
     * @param startingName string containing the information of the starting name
     * @throws RemoteException exception of RMI
     */
    public LobbyServer(int serverPortRMI, int serverPortTCP, String serverName, int startingPort, String startingName) throws RemoteException{
        this(new LobbyServerConfig(serverPortRMI, serverPortTCP, serverName, startingPort, startingName));
    }


    /**
     * Method used for the loading of the initial configuration from the file saved in "config/server"
     * @return a LobbyServerConfig object
     */
    private LobbyServerConfig loadInitialConfig() {
        try {
            Reader r= new FileReader(ServerConstants.SERVER_INITIAL_CONFIG);
            return JsonWithExposeSingleton.getJsonWithExposeSingleton().fromJson(r, LobbyServerConfig.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method used for the loading of the ban list from the file contained in "config/server"
     * @return list of banned words
     */
    private List<String> loadBanList(){
        try {
            Reader r= new FileReader(ServerConstants.SERVER_BAN_LIST);
            return JsonWithExposeSingleton.getJsonWithExposeSingleton().fromJson(r, ArrayList.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * This method puts the server online in the RMI registry, and it waits for someone to acquire it
     */
    public void start(){
        try {
            System.out.println("Initializing server...");
            System.out.println("Cleaning the directory "+ AppConstants.PATH_SAVED_MATCHES+" ...");
            this.cleanMatchDirectory();
            System.out.println("Cleaning done...");
            this.registry = LocateRegistry.createRegistry(this.config.getServerPortRMI());

            System.out.println("Registry acquired...");
            this.registry.bind(this.config.getServerName(), this);

            System.out.println("RMI Server online...");
            System.out.println("Name: "+this.config.getServerName()+" Port: "+this.config.getServerPortRMI());
        }catch (RemoteException e){
            System.out.println(e.getMessage());
        } catch (AlreadyBoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This method starts the game by putting it online in the RMI registry
     * The port and the name of the game are chosen automatically and are incremental
     * @param rs reference to the RMI server that needs to be put online
     * @param info reference to the information (port and name) that is useful for the setup of the game
     */
    private void startGame(RmiServer rs, ConnectionInformationRMI info){
        try {
            System.out.println("Initializing game...");
            Registry r = LocateRegistry.createRegistry(info.getRegistryPort());
            this.serverRegistries.add(r);

            System.out.println("Registry acquired...");

            r.bind(info.getRegistryName(), rs);

            System.out.println("RMI Server online...");
            System.out.println("Name: "+info.getRegistryName()+" Port: "+info.getRegistryPort());
        }catch (RemoteException e){
            System.out.println(e.getMessage());
        } catch (AlreadyBoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This method loads all games currently saved in the directory "savedMatches" and removes the ones which are ended already
     */
    private void cleanMatchDirectory(){
        Arrays.stream(Objects.requireNonNull(new File(AppConstants.PATH_SAVED_MATCHES).list()))
                .filter(match -> {
                    try {
                        return JsonWithExposeSingleton.getJsonWithExposeSingleton()
                                .fromJson(new FileReader(AppConstants.PATH_SAVED_MATCHES+match), GameModel.class)
                                .isGameOver();
                    } catch (FileNotFoundException e) {
                        System.out.println(e.getMessage());
                        return false;
                    }
                })
                .forEach((match) -> new File(AppConstants.PATH_SAVED_MATCHES+match).delete());
    }


    /**
     * Method that the client can call to get a nickname assigned on the server
     * @param nickname string containing the nickname of the client
     * @return false if the nickname is either banned or already present
     * @throws RemoteException exception of RMI
     * @throws ExistentNicknameExcepiton if the nickname is already present in the list
     * @throws IllegalNicknameException if the nickname is one of the banned words (or contains one)
     */
    @Override
    public boolean chooseNickname(String nickname) throws RemoteException, ExistentNicknameExcepiton, IllegalNicknameException {
        synchronized (lockChooseNickName) {
            if (this.banList.stream().anyMatch(nickname::matches)) throw new IllegalNicknameException();
            if (!this.nicknamesPool.add(nickname)) throw new ExistentNicknameExcepiton();
            return true;
        }
    }

    /**
     * This method checks if a nickname has been asked to the server (is registered)
     * and is not currently in game
     * @param nickname nickname of the player to be checked
     * @throws AlreadyInGameException if the player is already in a different game
     * @throws NonExistentNicknameException if the player's nickname is not in the server's list
     */
    private void checkCredentialsIntegrity(String nickname) throws AlreadyInGameException, NonExistentNicknameException {
        if(!this.nicknamesPool.contains(nickname)) throw new NonExistentNicknameException();
        if(this.nicknamesInGame.contains(nickname)) throw new AlreadyInGameException();
    }

    /**
     * This method lets you create a game and it automatically puts it in the RMI registries
     * @param numPlayers number of players that the client has chosen
     * @param nickname nickname of the player that calls the method
     * @param client reference to the methods of the client that can be called by the server using RMI
     * @return the information useful for the connection to the game
     * @throws RemoteException exception of RMI
     * @throws AlreadyInGameException if the player is already in a different game
     * @throws NonExistentNicknameException if the player's nickname is not in the server's list
     */
    @Override
    public ConnectionInformationRMI createGame(Integer numPlayers, String nickname, RmiClientInterface client) throws RemoteException, AlreadyInGameException, NonExistentNicknameException {
        synchronized (lockCreateGame) {
            this.checkCredentialsIntegrity(nickname);
            this.nicknamesInGame.add(nickname);
            RmiServer rs = new RmiServer(numPlayers, this);
            rs.addPlayer(nickname, client);
            this.serverList.add(rs);
            ConnectionInformationRMI info = new ConnectionInformationRMI(this.config.getStartingName()+(this.serverList.size()), this.config.getStartingPort()+(this.serverList.size()*2-1));
            this.serverInformation.add(info);
            this.startGame(rs, info);
            return info;
        }
    }


    /**
     * This method lets you join the first game available in the list of all games active
     * Also if a player is in a potential game (recovered by persistance) it will automatically be added to it
     * @param nickname nickname of the player that calls the method
     * @param client reference to the methods of the client that can be called by the server using RMI
     * @return the information useful for the connection to the game
     * @throws RemoteException exception of RMI
     * @throws NoGamesAvailableException if every game is full or there is no game currently ongoing in the server
     * @throws AlreadyInGameException if the player is already in a different game
     * @throws NonExistentNicknameException if the player's nickname is not in the server's list
     */
    @Override
    public ConnectionInformationRMI joinGame(String nickname, RmiClientInterface client) throws RemoteException, NoGamesAvailableException, AlreadyInGameException, NonExistentNicknameException {
        synchronized (lockCreateGame) {
            if (this.potentialPlayers.containsKey(nickname)) {
                ConnectionInformationRMI toReturn= this.potentialPlayers.get(nickname);
                this.potentialPlayers.remove(nickname);
                return toReturn;
            }
            this.checkCredentialsIntegrity(nickname);
            int gameFound = 0;
            ConnectionInformationRMI conn;

            while(gameFound < this.serverRegistries.size()){
                if(this.serverList.get(gameFound).getFreeSpaces()>0){
                    this.serverList.get(gameFound).addPlayer(nickname, client);
                    this.nicknamesInGame.add(nickname);
                    return this.serverInformation.get(gameFound);
                }
                gameFound++;
            }

            if(gameFound == this.serverRegistries.size())
                throw new NoGamesAvailableException();

            //Should never arrive here
            return null;
        }
    }

    /**
     * This method cheks if in all the saved matches there is a saved game (currently ongoing) with the player whose nickname is inserted in input
     * @param nickname nickname of the player to be checked
     * @return true if in the folder "savedMatches" there is a file that contains the nickname
     */
    @Override
    public boolean isGameExistent(String nickname) throws RemoteException{
        //Should never be null
        return Arrays.stream(Objects.requireNonNull(new File(AppConstants.PATH_SAVED_MATCHES).list()))
                .filter(fileName -> fileName.endsWith(ServerConstants.JSON_EXTENSION))
                .anyMatch(fileName -> fileName.contains(nickname+ServerConstants.REGEX));
    }

    /**
     * This method lets you recover a game from where it has been stopped
     * It takes the information from the file inferring it by your name
     * Also adds the players to the potential players list (inferred from the file name)
     * @param nickname nickname of the player who asks to recover a game
     * @param client his client infromation
     * @return the information useful for the connection to the game
     * @throws RemoteException exception of RMI
     * @throws AlreadyInGameException if the player is already in a different game
     * @throws NonExistentNicknameException if the player's nickname is not in the server's list or if the player has not a saved game
     */
    @Override
    public ConnectionInformationRMI recoverGame(String nickname, RmiClientInterface client) throws RemoteException, AlreadyInGameException, NonExistentNicknameException {
        this.checkCredentialsIntegrity(nickname);
        if (!this.isGameExistent(nickname)) throw new NonExistentNicknameException();
        synchronized (lockCreateGame) {
            this.nicknamesInGame.add(nickname);

            //load filename
            String fileName = Arrays.stream(Objects.requireNonNull(new File(AppConstants.PATH_SAVED_MATCHES).list()))
                    .filter(file -> file.contains(nickname+ServerConstants.REGEX))
                    .findFirst()
                    .orElse("shouldneverenterhere");

            try {
                //create a game with the GameModel as parameter
                GameModel gm = new GameModel(JsonWithExposeSingleton.getJsonWithExposeSingleton().fromJson(new FileReader(AppConstants.PATH_SAVED_MATCHES + fileName), GameModel.class));
                RmiServer rs = new RmiServer(gm, this);
                rs.addPlayer(nickname, client);
                this.serverList.add(rs);
                ConnectionInformationRMI info = new ConnectionInformationRMI(this.config.getStartingName() + (this.serverList.size()), this.config.getStartingPort() + this.serverList.size());
                this.serverInformation.add(info);
                //add the potential players to the list
                this.addPotentialPlayers(fileName, info, nickname);
                this.startGame(rs, info);
                return info;
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }
            //Should never arrive here
            return null;
        }
    }

    private void addPotentialPlayers(String fileName, ConnectionInformationRMI conn, String firstPlayer){
        Arrays.stream(fileName
                .substring(0,fileName.length()-ServerConstants.JSON_EXTENSION.length())
                .split(ServerConstants.REGEX))
                .filter(name -> !name.equals(firstPlayer))
                .forEach(match -> this.potentialPlayers.put(match,conn));
    }

    /**
     * This method takes in input the list of nicknames and it removes all the players
     * from the current pool of players in game
     * @param playersList file name where the game was stored
     */
    public void removePlayersFromGame(List<String> playersList){

    }

}
