import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vikaasa on 11/13/2016.
 */
public class client {
    String server;
    String userName;
    int port;
    Socket requestSocket;           //socket connect to the server
    ObjectOutputStream objectOutputStream;         //stream write to the socket
    ObjectInputStream objectInputStream;          //stream read from the socket

    public client(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.userName = username;
    }

    void run() {
        try {
            requestSocket = new Socket(server, port);
            ClientReceiveThread clientReceiveThread = new ClientReceiveThread(requestSocket);
            clientReceiveThread.start();
            objectOutputStream = new ObjectOutputStream(requestSocket.getOutputStream());
            //objectInputStream = new ObjectInputStream(requestSocket.getInputStream());
            objectOutputStream.writeObject(userName);
            Thread t1 = new Thread(new KeyboardInput(this));
            t1.start();
        } catch (ConnectException e) {
        } catch (UnknownHostException unknownHost) {
        } catch (IOException ioException) {
        }
    }

    void sendMessage(Message message) {
        try {
            if (message.getMessageType() == Message.MessageType.FILE) {
                message.fileContent = Files.readAllBytes(message.getFile().toPath());
            }
            objectOutputStream.writeObject(message);
            objectOutputStream.reset();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public class KeyboardInput extends Thread {

        client client;
        public KeyboardInput(client client) {
            this.client = client;
        }

        Scanner sc = new Scanner(System.in);
        Message.MessageType messageType;
        Message.SendType sendType;
        String destinationUserName;
        String messageBody;

        @Override
        public void run() {
            System.out.println("==================================================");
            System.out.println("Enter the command in the following format: \n<message_type> <\"message/file_address\"> <send_type> <username>\nNote: If a broadcast message, do not enter a destination username.");
            System.out.println("\nType 'stop' to terminate the client process.");
            System.out.println("==================================================");

            while (true) {
                try {
                    String str = sc.nextLine();
                    Message message = new Message(Message.SendType.BROADCAST, Message.MessageType.TEXT);
                    List<String> command = new ArrayList<String>();
                    Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(str);
                    while (m.find())
                        command.add(m.group(1).replace("\"","")); // Add .replace("\"", "") to remove surrounding quotes.
                    if (command.get(0).equals("stop")) {
                        message.setMessageType(Message.MessageType.ADMIN);
                        client.sendMessage(message);
                        System.out.println("Terminating client!");
                        System.exit(0);
                    }
                    if (command.get(0).toLowerCase().equals("text"))
                        messageType = Message.MessageType.TEXT;
                    else if (command.get(0).toLowerCase().equals("file"))
                        messageType = Message.MessageType.FILE;
                    messageBody = command.get(1);
                    if (command.get(2).toLowerCase().equals("broadcast"))
                        sendType = Message.SendType.BROADCAST;
                    else if (command.get(2).toLowerCase().equals("unicast"))
                        sendType = Message.SendType.UNICAST;
                    else if (command.get(2).toLowerCase().equals(("blockcast")))
                        sendType = Message.SendType.BLOCKCAST;
                    if (sendType == Message.SendType.BROADCAST)
                        destinationUserName = "";
                    else
                        destinationUserName = command.get(3);
                    message.setMessageType(messageType);
                    message.setSendType(sendType);
                    message.setUserName(destinationUserName);
                    if (messageType == Message.MessageType.TEXT)
                        message.setText(messageBody);
                    else
                        message.setFile(new File(messageBody));
                    client.sendMessage(message);
                } catch (Exception e) {
                    System.out.println("Wrong input format!");
                }
            }
        }
    }


    public static void main(String args[]) {
        client client = new client("localhost", 8000, args[0]);
        client.run();
    }

    public class ClientReceiveThread extends Thread {
        Message message;
        Socket socket;
        ObjectInputStream objectInputStream;

        ClientReceiveThread(Socket socket) {
            this.socket = socket;
            try {
                objectInputStream = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            while (true) {
                try {
                    message = (Message) objectInputStream.readObject();
                    processMessage(message);
                } catch (IOException e) {
                } catch (ClassNotFoundException e) {
                }
            }
        }

        void processMessage(Message message) {
            if (message.getMessageType() == Message.MessageType.TEXT) {
                System.out.println("Received text message from: " + message.getSenderUserName() + ": " + message.getText());
            } else {
                saveFile(message);
                System.out.println("Received file from: " + message.getSenderUserName() + ": " + message.getFile().getName());
            }
        }

        private void saveFile(Message message) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(message.getFile().getName());
                fileOutputStream.write(message.fileContent);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
