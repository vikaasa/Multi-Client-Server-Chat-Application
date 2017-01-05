import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by ramitsuri on 11/13/2016.
 */
public class server {
    int port;
    ServerSocket serverSocket;
    ArrayList<ClientThread> clients;

    public server(int port) {
        this.port = port;
        clients = new ArrayList<>();
    }

    void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(socket);
                clients.add(clientThread);
                clientThread.start();
                //System.out.println("Connection received from " + socket.getInetAddress().getHostName());
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void main(String args[]) {
        server server = new server(8000);
        server.run();
    }

    public class ClientThread extends Thread {
        int id;
        private boolean isRunning = true;
        String userName;
        Message message;
        Socket socket;
        ObjectInputStream objectInputStream;
        ObjectOutputStream objectOutputStream;

        ClientThread(Socket socket) {
            this.socket = socket;
            try {
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                userName = (String) objectInputStream.readObject();
                System.out.println(userName + " connected");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        public void run() {
            while (isRunning) {
                try {
                    message = (Message) objectInputStream.readObject();
                    message.setSenderUserName(userName);
                    System.out.println(message.getMessageType().toString() + " message from client " + message.getSenderUserName());
                    processMessage(message);
                } catch (IOException e) {
                } catch (ClassNotFoundException e) {
                }
            }
        }

        private void processMessage(Message message) {
            if(message.getMessageType() == Message.MessageType.ADMIN){
                clients.remove(this);
                isRunning = false;
            }
            else if (message.getSendType() == Message.SendType.BROADCAST) {
                for (ClientThread clientThread : clients) {
                    if (!clientThread.userName.equals(message.getSenderUserName()))
                        clientThread.send(message);
                }
            } else if (message.getSendType() == Message.SendType.UNICAST) {
                for (ClientThread clientThread : clients) {
                    if (clientThread.userName.equals(message.getUserName())) {
                        clientThread.send(message);
                        break;
                    }
                }
            } else if (message.getSendType() == Message.SendType.BLOCKCAST) {
                for (ClientThread clientThread : clients) {
                    if (!clientThread.userName.equals(message.getUserName()) && !clientThread.userName.equals(message.getSenderUserName()))
                        clientThread.send(message);
                }
            }
        }

        private void send(Message message) {
            try {
                objectOutputStream.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


