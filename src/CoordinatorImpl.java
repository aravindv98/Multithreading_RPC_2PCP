import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class CoordinatorImpl extends UnicastRemoteObject implements Coordinator{

  private List<RMIServer> participants;
  private List<String> participantHosts;
  private List<Integer> participantPorts;
  public CoordinatorImpl(List<String> participantHosts, List<Integer> participantPorts) throws RemoteException {
    this.participantHosts = participantHosts;
    this.participantPorts = participantPorts;
  }

  public void connectParticipants() throws RemoteException{
    participants = new ArrayList<>();
    for (int i = 0; i < participantHosts.size(); i++) {
      try {
        String serverURL = "rmi://" + participantHosts.get(i) + ":" + participantPorts.get(i) + "/RMIServer";
        RMIServer participant = (RMIServer) Naming.lookup(serverURL);
        participants.add(participant);
      } catch (Exception e) {
        throw new RemoteException("Unable to connect to participant", e);
      }
    }
  }

  public synchronized void prepareTransaction(String clientMessage, String serverResponse, String clientAddress, String clientPort) throws RemoteException {
    for (RMIServer participant : participants) {
      if (!participant.prepare(clientMessage, serverResponse, clientAddress, clientPort)) {
        return;
      }
    }
    for (RMIServer participant : participants) {
      participant.commit(clientMessage,serverResponse,clientAddress,clientPort);
    }
  }
}
