package it.polimi.ingsw.network.messages.serverMessages;

import it.polimi.ingsw.network.messages.Message;

/**
 * This class represents the recover game response message
 */
public class RecoverGameResponse extends Message {

    /**
     * true if no game are available
     */
    private boolean noGameToRecover;

    /**
     * Constructor
     * @param sender : the one who sends the message
     * @param noGameToRecover : true if no game are available
     */
    public RecoverGameResponse(String sender, boolean noGameToRecover) {
        super(sender);

        this.noGameToRecover=noGameToRecover;
        setMessageType("RecoverGameResponse");
    }

    /**
     * Method to check if no games are available
     * @return true if no game are available
     */
    public boolean isNoGameToRecover() {
        return noGameToRecover;
    }
}
