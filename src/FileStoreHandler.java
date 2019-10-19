import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	Map <String, RFile> data;

	public FileStoreHandler(String ip, int port){
		this.port = port;
		this.ip = ip;
		String key = getSHA(ip + ":" + Integer.toString(port));
		node = new NodeID(key, ip, port);
		fingertable = new ArrayList<>();
		data = new HashMap<String, RFile>();
	}

	public void setFingertable(List<NodeID> node_list) throws TException {
		fingertable = node_list;
		System.out.println("succ is --: " + node_list.get(0).getPort());
	}

	public void writeFile(RFile rFile) throws TException {
		RFileMetadata fileMetadata = rFile.getMeta();
		String fileName = fileMetadata.getFilename();
		String content = rFile.getContent();
		String id = getSHA(fileName);

		NodeID fileSucc = findSucc(id);
		System.out.println("succ is: "+fileSucc.getPort());
		if(fileSucc.getId().compareTo(node.getId()) == 0) {

			if(data.containsKey(id)){
				int ver = data.get(id).getMeta().getVersion();
				data.get(id).getMeta().setVersion(ver + 1);
				data.get(id).setContent(content);
			}
			else{
				RFileMetadata mdata = new RFileMetadata();
				RFile rfile = new RFile();
				mdata.setFilename(fileName);
				mdata.setVersion(0);

				try{

					File file = new File(fileName);
					FileWriter fileWriter = new FileWriter(file);
					fileWriter.write(content);
					fileWriter.close();
				}catch(IOException ioe){
					ioe.printStackTrace();
					System.exit(0);
				}

				String contentHash = getSHA(content);
				mdata.setContentHash(contentHash);

				rfile.setContent(content);
				rfile.setMeta(mdata);

				data.put(id, rfile);
			}
		}
		else{
			SystemException exception = new SystemException();
			exception.setMessage("Server does not own given file ID");
			throw exception;
		}
	}

	public RFile readFile(String filename) throws TException {
		RFile rfile;
		String id = getSHA(filename);
		NodeID fileSucc = findSucc(id);
		if(fileSucc.getId().compareTo(node.getId()) == 0 && data.containsKey(id)) 
				rfile = data.get(id);
		else{
			SystemException exception = new SystemException();
			exception.setMessage("Server does not own given file ID");
			throw exception;
		}
		return rfile;
	}

	public NodeID findSucc(String key) throws TException {
		NodeID nodeSucc;
		if(key.compareTo(node.getId())==0)
			return node;
		else{
			nodeSucc = findPred(key);
			if(nodeSucc.getId().compareTo(node.getId()) == 0)
				return getNodeSucc();
			else{
				if(nodeSucc!= null) 
					nodeSucc = sendrpc(nodeSucc, key, "getNodeSucc");
			}
		}
		return nodeSucc;
	}

	public NodeID findPred(String key) throws TException {

		//if(key.compareTo(node.getId())==0)
		//	return node;
		//else{
		NodeID next = null;
		next = getNodeSucc();
		NodeID pred = null;
		if(isClosest(key, node.getId(), next.getId()) != null)
			return node;
		if(pred == null)
			for (int i = fingertable.size() - 1; i > 0; i--){
				//next = fingertable.get(i);
				if(isClosest(fingertable.get(i).getId(), node.getId(), key)!=null)
					return sendrpc(next, key, "findPred");
			}
		else{
			SystemException se = new SystemException();
			se.setMessage("Pred does not exist");
			throw se;
		}
		NodeID n = null;
		return n;
		//}
	}
	public NodeID getNodeSucc() throws TException {
		NodeID succnode;
		if(fingertable.size() > 0){
			succnode = fingertable.get(0);
		}
		else{
			SystemException exception = new SystemException();
			exception.setMessage("The node does not have finger table");
			throw exception;
		}
		return succnode;
	}

	public String isClosest(String key, String curr, String next){
		if((key.compareTo(curr) > 0) && (key.compareTo(next) < 0))
			return next;
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
	
	public NodeID sendrpc (NodeID cnode, String key, String method){
		NodeID temp_node = null;
		try{
			/**
			 * ref = "https://people.apache.org/~thejas/thrift-0.9/javadoc/org/apache/thrift/transport/TSocket.html"
			 *
			 */
			TTransport socket = new TSocket(cnode.getIp(), cnode.getPort());
			socket.open();
			
			/**
			 * ref = "https://people.apache.org/~thejas/thrift-0.9/javadoc/org/apache/thrift/protocol/TBinaryProtocol.html"
			 *
			 */
			TProtocol protocol = new TBinaryProtocol(socket);
			FileStore.Client client = new FileStore.Client(protocol);
			if(method.equals("findPred"))
				return client.findPred(key);
			else if(method.equals("getNodeSucc"))
				return client.getNodeSucc();
		}
		catch(TException te){
			System.err.println("Exception occured in rpc : " + te.getMessage());
			System.exit(0);
		}
		return temp_node;
	}

}
