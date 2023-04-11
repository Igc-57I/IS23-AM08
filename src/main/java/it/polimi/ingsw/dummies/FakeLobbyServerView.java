package it.polimi.ingsw.dummies;

import it.polimi.ingsw.server.ConnectionInformationRMI;
import it.polimi.ingsw.server.RMILobbyServerInterface;
import it.polimi.ingsw.server.RmiServerInterface;
import it.polimi.ingsw.server.exceptions.ExistentNicknameExcepiton;
import it.polimi.ingsw.server.exceptions.IllegalNicknameException;
import it.polimi.ingsw.server.exceptions.NoGamesAvailableException;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class FakeLobbyServerView {

    public static void main(String[] args) {
        try {
            Integer remoteObjectPort=12345;
            System.out.println("Locating registry...");
            Registry registry= LocateRegistry.getRegistry(remoteObjectPort);
            System.out.println("Registry found...");
            System.out.println("All RMI Registry bindings: ");
            List<String>bindingsList= Arrays.stream(registry.list()).toList();
            for(String binding: bindingsList){
                System.out.println(binding);
            }
            String remoteObjectName="LobbyServer";
            System.out.println("Locating remote object "+remoteObjectName+" ...");
            RMILobbyServerInterface remoteServer= (RMILobbyServerInterface) registry.lookup(remoteObjectName);
            System.out.println("Server located, you can start to send messages to it");

            Scanner scanner=new Scanner(System.in);
            boolean end =false;
            while(!end){
                String command=scanner.nextLine();
                switch (command) {
                    case "name" -> {
                        System.out.println("Inserisci nome");
                        String nome = scanner.nextLine();
                        try {
                            System.out.println(remoteServer.chooseNickname(nome));
                        } catch (IllegalNicknameException e) {
                            System.out.println("Illegal nickname, retry please...");
                        } catch (ExistentNicknameExcepiton e) {
                            System.out.println("Already existent nickname, retry please...");
                        }
                    }
                    case "game" -> {
                        System.out.println("Insert number of players: ");
                        Integer num = scanner.nextInt();
                        ConnectionInformationRMI conn = remoteServer.createGame(num, "Pollo", null);
                        System.out.println("Game created on server, now connecting client side...");
                        Registry gameRegistry = LocateRegistry.getRegistry(conn.getRegistryPort());
                        //RmiServerInterface rsi = (RmiServerInterface) gameRegistry.lookup(conn.getRegistryName());
                        System.out.println("Connected client side...");
                    }
                    case "join" -> {
                        System.out.println("Joining random game...");
                        try {
                            ConnectionInformationRMI conn2 = remoteServer.joinGame("Pollo", null);
                            System.out.println("Game joined on server, now connecting client side...");
                            Registry gameRegistry2 = LocateRegistry.getRegistry(conn2.getRegistryPort());
                            //RmiServerInterface rsi2 = (RmiServerInterface) gameRegistry2.lookup(conn2.getRegistryName());
                            System.out.println("Connected client side...");
                        } catch (NoGamesAvailableException e) {
                            System.out.println("No games available, retry please...");
                        }
                    }
                    case "adios" -> end = true;
                }
            }
        }
        catch (RemoteException e) {
            System.out.println(e.getMessage());
        } catch (NotBoundException e) {
            System.out.println(e.getMessage());
        }
    }

}
