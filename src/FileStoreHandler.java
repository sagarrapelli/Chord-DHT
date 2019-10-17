import java.util.*;
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;


public class FileStoreHandler implements FileStore.Iface{

	public int port;
	public String ip;
	NodeID node;
	List <NodeID> fingertable;

	public FileStoreHandler(String ip, int port){
		this.port = port;
		this.ip = ip;
		String key = getSHA(ip + ":" + Integer.toString(port));
		node = new NodeID(key, ip, port);
		fingertable = new ArrayList<>();
	}

	public void setFingertable(List<NodeID> node_list) throws TException {
		fingertable = node_list;
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

		NodeID next = getNodeSucc();
		NodeID pred = null;
		if(isClosest(key, node.getId(), next.getId()) != null);
			pred = node;
		if(pred == null)
			for (int i = fingertable.size() - 1; i > 0; i--){
				next = fingertable.get(i);
				if(isClosest(next.getId(), node.getId(), key)!=null)
					return sendrpc(next, key);
			}
		return pred;
	}
	public NodeID getNodeSucc(){
		return fingertable.get(0);
	}

	public String isClosest(String key, String curr, String next){
		if((key.compareTo(curr) > 0) && (key.compareTo(next) < 0))
			return curr;
		else if(curr.compareTo(next) > 0){
			if((curr.compareTo(key) > 0) && (next.compareTo(key) > 0))
				return next;
		}
		return null;
	}


	public String getSHA(String input) {
		StringBuffer hexString = new StringBuffer();
		try {	
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
		}
		catch (NoSuchAlgorithmException e){
			System.err.println("Exception occured while calculating SHA-256:" + e.getMessage());
			System.exit(0);
		}
		return hexString.toString();
	}	
	
	public NodeID sendrpc (NodeID node, String key){
		NodeID temp_node = null;
		try{
			/**
			 * ref = "https://people.apache.org/~thejas/thrift-0.9/javadoc/org/apache/thrift/transport/TSocket.html"
			 *
			 */
			TTransport socket = new TSocket(node.getIp(), node.getPort());
			socket.open();
			
			/**
			 * ref = "https://people.apache.org/~thejas/thrift-0.9/javadoc/org/apache/thrift/protocol/TBinaryProtocol.html"
			 *
			 */
			TProtocol protocol = new TBinaryProtocol(socket);
			FileStore.Client client = new FileStore.Client(protocol);
			temp_node = client.findPred(key);
		}
		catch(TException te){
			System.err.println("Exception occured in rpc : " + te.getMessage());
			System.exit(0);
		}
		return temp_node;
	}

}
