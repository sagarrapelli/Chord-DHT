import java.util.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;


public class FileStoreHandler implements FileStore.Iface{

	public int port;
	public String ip;
	List <NodeID> fingertable;

	public FileStoreHandler(String ip, int port){
		this.port = port;
		this.ip = ip;
		fingertable = new ArrayList<>();
	}

	public void setFingertable(List<NodeID> node_list) throws TException {
		fingertable = node_list;
		System.out.print(port + " " + fingertable);
	}

	public void writeFile(RFile rFile){
	}

	public RFile readFile(String filename){
		return new RFile();
	}
	public NodeID findSucc(String key){
		return new NodeID();
	}
	public NodeID findPred(String key){
		return new NodeID();
	}
	public NodeID getNodeSucc(){
		return new NodeID();
	}
}
