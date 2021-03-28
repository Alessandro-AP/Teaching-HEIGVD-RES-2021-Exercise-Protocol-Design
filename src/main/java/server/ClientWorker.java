package server;

import protocol.Protocol;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ClientWorker class is for the communication bewteen client-server and protocol
 * application.
 *
 * It make the same work as the single thread server.
 */
public class ClientWorker implements Runnable {

    static final Logger LOG = Logger.getLogger(ClientWorker.class.getName());

    private Socket clientSocket = null;
    private InputStream is = null;
    private OutputStream os = null;
    private ServerMultiThread server = null;

    /**
     * Constructor
     * @param clientSocket Client socket assigned in ServerMultiThread class
     * @param server       ServerMultiThread object, use for close server socket
     * @throws IOException
     */
    public ClientWorker(Socket clientSocket, ServerMultiThread server) throws IOException {
        this.clientSocket = clientSocket;
        this.server = server;
        is = clientSocket.getInputStream();
        os = clientSocket.getOutputStream();
    }

    /**
     * Communication bewteen client-server and protocol application
     */
    public void run() {
        try {
            // Initiliaze input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

            //Write welcome message
            writer.println("Hello, enter your operation :");

            // Free the writing flow
            writer.flush();

            //Wait for a message from the client
            String message = reader.readLine();

            while (message != null) {

                // Split string in substring,
                // the delimiter used is blankspace
                String[] tokens = message.split(" ");

                // Check if the command is "QUIT"
                // "QUIT" command stops the server
                if(tokens.length == 1 && tokens[0].equals(Protocol.QUIT)){
                    // Close reader and writer
                    reader.close();
                    writer.close();
                    // Close ClientSocket
                    clientSocket.close();
                    return;
                }
                else if(tokens.length == 3) {// Check that the command is formed by  3 elements

                    //Get each element
                    String operation = tokens[0];

                    //Try to convert string to int and catch exception
                    try{
                        int operand1 = Integer.parseInt(tokens[1]);
                        int operand2 = Integer.parseInt(tokens[2]);

                        // Perform the chosen operation and write the result
                        // If the operation is invalid, an error message returns
                        switch (operation) {
                            case Protocol.ADD : writer.println("Result : " + (operand1 + operand2));break;
                            case Protocol.SUB : writer.println("Result : " + (operand1 - operand2));break;
                            case Protocol.MULT: writer.println("Result : " + (operand1 * operand2));break;
                            default: writer.println("ERROR : Invalid command, try again!");
                        }
                    }
                    catch(NumberFormatException e){
                        writer.println("ERROR : Invalid command, try again!");
                    }
                }else {
                    writer.println("ERROR : Invalid command, try again!");
                }
                writer.flush();// Free the writing flow
                message = reader.readLine();// Wait for a new message
            }
            // Close reader and writer
            reader.close();
            writer.close();
            // Close ClientSocket
            clientSocket.close();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Exception in client handler: {0}", ex.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                LOG.log(Level.INFO, ex.getMessage());
            }
            try {
                is.close();
            } catch (IOException ex) {
                LOG.log(Level.INFO, ex.getMessage());
            }
            try {
                os.close();
            } catch (IOException ex) {
                LOG.log(Level.INFO, ex.getMessage());
            }
        }
    }
}
