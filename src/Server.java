import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame {
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ServerSocket server;
    private Socket connection;

    public Server() {
        super("Ryans Instant Messenger");
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                sendMessage(event.getActionCommand());
                userText.setText("");
            }
        });
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(300,150);
        setVisible(true);
    }

    public void startRunning(){
        try{
            server = new ServerSocket(6789, 100);
            while (true) {
                try{
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                }catch (EOFException ex){
                    showMessage("\n Server ended the connection.");
                }finally {
                    closeCrap();
                }
            }
        }catch (IOException io){
            io.printStackTrace();
        }
    }

    private void waitForConnection() throws IOException{
        showMessage("Waiting for someone to connect...\n");
        connection = server.accept();
        showMessage(" Now connected to " + connection.getInetAddress().getHostName());
    }

    private void setupStreams() throws IOException{
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Streams are now setup \n");
    }

    private void whileChatting() throws IOException{
        String message = " You are now connected ";
        sendMessage(message);
        ableToType(true);
        do{
            try{
                message = (String)inputStream.readObject();
                showMessage("\n" + message);
            }catch(ClassNotFoundException ex) {
                showMessage("\n idk what happened");
            }
        }while(!message.equals("CLIENT - END"));
    }

    private void closeCrap() throws IOException{
        showMessage("\n Closing connections...\n");
        ableToType(false);
        try{
            outputStream.close();
            inputStream.close();
            connection.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private void sendMessage(String message){
        try{
            outputStream.writeObject("SERVER - " + message);
            outputStream.flush();
            showMessage("\nSERVER - " + message);
        }catch(IOException ex){
            chatWindow.append("\n ERROR: I CANT SEND THAT MESSAGE!");
        }
    }

    private void showMessage(final String text){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                chatWindow.append(text);
            }
        });
    }

    private void ableToType(final boolean canType){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                userText.setEditable(canType);
            }
        });
    }
}
