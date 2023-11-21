import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * An RMI Server to implement the remote RMI interface and extend the abstract
 * server implementation class.
 */
public class Server {
  public static void main(String[] args) {
    // Starting the server.
    try  {
      Coordinator coordinator = new CoordinatorImpl(
              Arrays.asList("localhost", "localhost", "localhost", "localhost", "localhost"),
              Arrays.asList(5001, 5002, 5003, 5004, 5005)
      );
      Registry coordinatorRegistry = LocateRegistry.createRegistry(3200);
      coordinatorRegistry.bind("Coordinator", coordinator);
      System.out.println("Coordinator is listening on port " + 3200);

      // Start the participants
      for (int i = 0; i < 5; i++) {
        RMIServer participant = new AbstractServerFunctionClass("localhost", 3200);
        int port = 5001 + i;
        Registry participantRegistry = LocateRegistry.createRegistry(port);
        participantRegistry.bind("RMIServer", participant);
        participant.connectToCoordinator();
        System.out.println("Participant is listening on port " + port);
      }
      coordinator.connectParticipants();
      System.out.println("Servers ready ");
    }catch (Exception e) {
      e.printStackTrace();
    }
  }
}
