##CS557 Chord Distributed Hash Table

#Programming language used
  Java

#Steps to compile and run
  1. Go to the directory cs457-557-f19-pa2-sagarrapelli
  2. Enter 'make' to compile the project
  3. Start the server using './server.sh xxxx'
      --xxxx will be the port number you want to start the server on.
  4. Repeat the above step n number of times for n servers
  5. Now enter the command './init node.txt' 
      --This creates a chord of servers

  6. Run the client program now './client.sh xxxxxx'
      --This command runs the client program which is Client.java and xxxxxx will be the address of server to which you want to send request.

#Completion status
  All the functions have been implemented and tested.


Note
1. I have been getting some warnings on server side I have tried but I couldn't get rid of them.
2. The case where findPred(key) where key is equal to server id is not been implemented assuming that if a key does not belong to a server then only its predecessor is found to find the successor for it.
