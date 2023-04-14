package it.polimi.ingsw.server;

import com.google.gson.annotations.Expose;

/**
 * This class contains all the information for the correct setup of the server
 * It can also be loaded from file
 * Note that it is immutable
 */
public class LobbyServerConfig {

    /**
     * Integer containing the port that the lobby server will be open on RMI
     */
    @Expose
    private final Integer serverPortRMI;
    /**
     * Integer containing the port that the lobby server will be open on TCP
     */
    @Expose
    private final Integer serverPortTCP;
    /**
     * String containing the server name that the client will refer to
     */
    @Expose
    private final String serverName;
    /**
     * Integer containing the first port that can be used for the creation of a game
     */
    @Expose
    private final Integer startingPort;
    /**
     * String containing the prefix used for the creaton of the game (in the form of startingName+numberOfGames)
     */
    @Expose
    private final String startingName;


    /**
     * Constructor that takes all the parameters of the class
     * @param serverPortTCP integer containing the information of the server port
     * @param serverPortRMI integer containing the information of the server port
     * @param serverName string containing the information of the server name
     * @param startingPort integer containing the information of the starting port
     * @param startingName string containing the information of the starting name
     */
    public LobbyServerConfig( Integer serverPortRMI, Integer serverPortTCP, String serverName, Integer startingPort, String startingName){
        this.serverName=serverName;
        this.serverPortRMI=serverPortRMI;
        this.serverPortTCP=serverPortRMI;
        this.startingPort=startingPort;
        this.startingName=startingName;
    }

    /**
     * Getter of the server port in RMI
     * @return an integer
     */
    public Integer getServerPortRMI() {
        return this.serverPortRMI;
    }
    /**
     * Getter of the server port in TCP
     * @return an integer
     */
    public Integer getServerPortTCP() {
        return this.serverPortTCP;
    }
    /**
     * Getter of the server name
     * @return a string
     */
    public String getServerName() {
        return this.serverName;
    }

    /**
     * Getter of the starting port of the server
     * @return an integer
     */
    public Integer getStartingPort() {
        return this.startingPort;
    }
    public String getStartingName(){
        return this.startingName;
    }
}
