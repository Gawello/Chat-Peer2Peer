package pl.gbanasik;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ChatClient {
    private DatagramChannel socket = null;
    private int myPort;
    private int clientPort;
    private String usernameBackup;

    public ChatClient(){}

    /**
     * DISCONNECTING USERS
    */
    public void Disconnect() {
        if (socket.isOpen()) {
            try {
                socket.disconnect();
                socket.close();
            } catch (IOException e) {
                System.out.println("Błąd rozłączania: " + e.getMessage());
                e.printStackTrace();
            }
            socket = null;
        }
    }

    /**
     *
     * @param my_port Main user port
     * @param client_port Friend port (second user)
     * @param username Nickname
     * @return Checking - users connected y/n
     */
    public boolean Connect(int my_port, int client_port, String username) {
        myPort = my_port;
        clientPort = client_port;
        usernameBackup = username;

        //Connect to the server
        try {
            socket = DatagramChannel.open();
            InetAddress address = InetAddress.getByName("127.0.0.1");
            InetSocketAddress addr = new InetSocketAddress(address, myPort);
            socket.bind(addr);
            socket.configureBlocking(false);
        } catch (IOException ex) {
            System.out.println("IOError: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * CONNECTING USERS
     * @return
     */
    public String Read() {
        if (socket == null || socket.isOpen() == false) {
            return "";
        }

        byte[] buffer = new byte[512];
        ByteBuffer bb = ByteBuffer.wrap(buffer);

        try {
            if (socket.receive(bb) == null) {
                return "";
            }
        } catch (IOException e) {
            System.out.println("Błąd odczytu: " + e.getMessage());
            e.printStackTrace();
        }
        return bb_to_str(bb, StandardCharsets.UTF_8);
    }


        //WYSYŁANIE WIADOMOŚCI
        public void Send(String message) {
            InetAddress address = null;
            try {
                address = InetAddress.getByName("127.0.0.1");
            } catch(UnknownHostException e) {
                System.out.println("Niewłaściwy hostname: " + e.getMessage());
                e.printStackTrace();
            }

            try {
                message = "[" + usernameBackup + "]: " + message;
                InetSocketAddress addr = new InetSocketAddress(address, clientPort);
                socket.send(str_to_bb(message, StandardCharsets.UTF_8), addr);
                System.out.println("Wysłana wiadomość: " + message);
            } catch(IOException e){
                System.out.println("Błąd wysyłania: " + e.getMessage());
                e.printStackTrace();
            }
        }

        public boolean IsConnected() {
            if(socket != null) {
                return socket.isConnected();
            } else {
                return false;
            }
        }

        public static ByteBuffer str_to_bb(String msg, Charset charset) {
            return ByteBuffer.wrap(msg.getBytes(charset));
        }

        public static String bb_to_str(ByteBuffer buffer, Charset charset){
            byte[] bytes;
            if(buffer.hasArray()) {
                bytes = buffer.array();
            } else {
                bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
            }
            return new String(bytes, charset);
        }
    }

