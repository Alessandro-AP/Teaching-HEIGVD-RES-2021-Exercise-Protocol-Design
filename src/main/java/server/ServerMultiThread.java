package server;

import client.Client;
import protocol.Protocol;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * For the server multi-thread version, we create a server's thread for listening,
 * every time a connection is accepted from a client, the server create à new thread
 * for each client.
 *
 * This thread has a ClientWorker parameter, ClientWorker is basically the single
 * thread server part who care about Input/Output Stream for communication between
 * client and server
 *
 * In resume, this class is for listening, create new thread and assign it to client
 * ClientWorker class is for the communication bewteen client-server and protocol
 * application
 */
public class ServerMultiThread {

    //Stop server listneing if an error occured
    boolean shouldRun = false;

    //Logger object
    final static Logger logger = Logger.getLogger(Client.class.getName());

    /**
     * Server listening, create new thread and assign it to client
     * @throws IOException
     */
    public void waitForIncomingClient() throws IOException {

        //Initialize Server socket and waiting for client
        ServerSocket receptionistSocket = new ServerSocket(Protocol.PORT);

        //Create new server thread for listening
        Thread serverThread = new Thread(new Runnable() {
            public void run() {
                shouldRun = true;
                while (shouldRun) {
                    try {
                        logger.log(Level.INFO, "Listening for client connection");

                        //Create new socket for Server-Client connection
                        Socket workerSocket = receptionistSocket.accept();
                        logger.info("New client has arrived...");

                        //Create CLientWorker who manage server's response for the client
                        ClientWorker worker = new ClientWorker(workerSocket, ServerMultiThread.this);
                        logger.info("Delegating work to client worker...");

                        //Create a thread for the clientworker
                        Thread clientThread = new Thread(worker);
                        clientThread.start();
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, "IOException in main server thread, exit: {0}", ex.getMessage());
                        shouldRun = false;
                    }
                }
            }
        });
        //Start server main thread
        serverThread.start();
    }

    public static void main( String[] args ) throws IOException
    {   // Start server
        ServerMultiThread serverMultiThread = new ServerMultiThread();
        while (true) {
            serverMultiThread.waitForIncomingClient();
        }
    }
}
