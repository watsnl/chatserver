package server;

/**
 * @author Nick Leeman
 * Created: 4/8/14
 * Modified: 4/15/14
 * Purpose: Server() provides the ServerSocket for the chat server, as well as a
 *          GUI (if desired) showing messages sent between clients. Code modified
 *          from Deitel & Deitel.
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Server extends JFrame
{
   private JTextField enterField; // inputs message from user
   private JTextArea displayArea; // display information to user
   private ServerSocket server; // server socket
   public static List<ClientThread> clients = Collections.synchronizedList(new ArrayList<ClientThread>());

   // set up GUI
   public Server()
   {
      super( "Server" );

      enterField = new JTextField(); // create enterField
      enterField.setEditable( false );
      enterField.addActionListener(
         new ActionListener() 
         {
            // send message to client
            public void actionPerformed( ActionEvent event )
            {
               enterField.setText( "" );
            } // end method actionPerformed
         } // end anonymous inner class
      ); // end call to addActionListener

      add( enterField, BorderLayout.NORTH );

      displayArea = new JTextArea(); // create displayArea
      add( new JScrollPane( displayArea ), BorderLayout.CENTER );
      displayArea.setEditable(false);
      
      displayArea.setLineWrap(true);
      displayArea.setWrapStyleWord(true);

      setSize( 300, 150 ); // set size of window
      setVisible( true ); // show window
   } // end Server constructor

   // set up and runApplication server 
   public void runApplication()
   {
      try // set up server to receive connections; process connections
      {
         server = new ServerSocket( 12345, 100 ); // create ServerSocket

         while(true){
            ClientThread newClientThread;
            try{
              //server.accept returns a client connection
              newClientThread = new ClientThread(server.accept(), displayArea);
              clients.add(newClientThread);
              new Thread(newClientThread).start();
            } 
            catch (IOException e) {
              System.out.println("Accept failed: 12345");
            }
          }
      } // end try
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // end catch
   } // end method runServer
} // end class Server

/**************************************************************************
 * (C) Copyright 1992-2012 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/