/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import javax.swing.JTextArea;

/**
 * @author Nick Leeman
 * Created: 4/8/14
 * Modified: 4/15/14
 * Purpose: ClientThread provides the multi-threaded functionality of class Server.
 *    This allows the server to send message between as many clients as the server can handle,
 *    up to 200 (arbitrary cutoff in client.Client()).
 */


class ClientThread implements Runnable {
  private Socket client;
  private JTextArea textArea;
  private ObjectOutputStream output; // output stream to clientThreads
  private ObjectInputStream input; // input stream from clientThreads
  private Boolean clientIsOpen = true;

  ClientThread(Socket client, JTextArea textArea) {
    this.client = client;
    this.textArea = textArea;
  } // end cstr
  
  ClientThread(Socket client, JTextArea textArea, ObjectOutputStream output, ObjectInputStream input) {
    this.client = client;
    this.textArea = textArea;
    this.output = output;
    this.input = input;
  } // end cstr

  @Override
  public void run(){

    do {
      try{
        getStreams();
        processClient();
        closeClient();
       }
      catch (IOException e) {
        // this catch is cheating, but 
        clientIsOpen = false;
        String control = "Connection terminated.";
        textArea.append( "\n" + control );
        closeClient();
       }
    } while(clientIsOpen);
  } // end run()


   // get streams to send and receive data
   private void getStreams() throws IOException
   {
      // set up output stream for objects
      output = new ObjectOutputStream( client.getOutputStream() );
      output.flush(); // flush output buffer to send header information

      // set up input stream for objects
      input = new ObjectInputStream( client.getInputStream() );

   } // end method getStreams

   // process clientThreads with clientThreads
   private void processClient() throws IOException
   {
      String message;
      
      do // process messages sent from clientThreads
      { 
         try // read message and display it
         {
            message = ( String ) input.readObject(); // read new message
            textArea.append("\n" + message);
            
            sendToClients(message);
         } // end try
         catch ( ClassNotFoundException classNotFoundException ) 
         {
           textArea.append( "\nUnknown object type received" );
         } // end catch

      } while (clientIsOpen);
   } // end method processclient

   // close streams and socket
   private void closeClient() 
   {

      try 
      {
         Server.clients.remove(this);
         output.close(); // close output stream
         input.close(); // close input stream
         client.close(); // close socket
      } // end try
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // end catch
   } // end method closeclient

   // send message to clientThreads
   public void sendData( String message )
   {
      try // send object to clientThreads
      {
         output.writeObject( message );
         output.flush(); // flush output to clientThreads
         textArea.append( "\n" + message );
      } // end try
      catch ( IOException ioException ) 
      {
         closeClient();
         textArea.append( "\nError writing object" );
      } // end catch
   } // end method sendData

   private void sendToClients(String message) {
     for(ClientThread clientThreads : Server.clients) {
        clientThreads.sendData(message);
     } // end for
   } // end sendToClients()
      
} // end ClientThread
