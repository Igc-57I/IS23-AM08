package it.polimi.ingsw.network.messages.clientMessages;

import it.polimi.ingsw.network.messages.Message;

/**
 * Message used to ask the server for the list of lobbies
 */
public class GetLobbiesMessage extends Message {
    /**
     * Constructor
     *
     * @param sender : the one who sends the message
     */
    public GetLobbiesMessage(String sender) {
        super(sender);
        setMessageType("GetLobbiesMessage");
    }
}
