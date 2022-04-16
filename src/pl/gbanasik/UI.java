package pl.gbanasik;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Action listeners for every Btn and Field
 */
public class UI implements ActionListener {
    //Logic

    ChatClient net;
    Timer timer;

    //UI

    //Main panel
    static public JFrame mainFrame;
    static private JPanel topPanel;
    static private JPanel middlePanel;
    static private JPanel bottomPanel;

    //Fields to edit for user
    static private JScrollPane scroll;
    static private JTextField serverPortTxt;
    static private JTextField clientPortTxt;
    static private JTextField usernameTxt;
    static private JTextField messageTxt;
    static private JTextArea chatTxt;
    static private JButton connectBtn;
    static private JButton sendBtn;

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == timer) {
            String message = net.Read();
            if(!message.isEmpty()) {
                chatTxt.append(message + "\n");
            }
        }
    }

    /**
    Chat UI for user
     */
    public UI(ChatClient netHandler) {

        //Setting variables: net handler
        net = netHandler;
        timer = new Timer(100, this);
        timer.start();

        //Generating chat window
        mainFrame = new JFrame("Chat P2P");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(640,600);
        mainFrame.setResizable(false);

        //Top panel - CONNECTING
        topPanel = new JPanel();
        topPanel.add(new JLabel("Port"));

        serverPortTxt = new JTextField(10);
        clientPortTxt = new JTextField(10);
        topPanel.add(serverPortTxt);
        topPanel.add(new JLabel("Friend port"));
        topPanel.add(clientPortTxt);
        topPanel.add(new JLabel("Username"));
        usernameTxt = new JTextField(10);
        topPanel.add(usernameTxt);



        connectBtn = new JButton("Connect");
        topPanel.add(connectBtn);

        //Text Area - chat
        middlePanel = new JPanel();

        chatTxt = new JTextArea();
        chatTxt.setSize(mainFrame.getWidth()-40, 400);
        chatTxt.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        chatTxt.setLineWrap(true);
        chatTxt.setEditable(false);
        chatTxt.setCaretPosition(chatTxt.getDocument().getLength());
        chatTxt.setRows(30);
        scroll = new JScrollPane(chatTxt, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        middlePanel.add(scroll);

        //Bottom panel (messages)
        bottomPanel = new JPanel();
        messageTxt = new JTextField(30);
        sendBtn = new JButton("Send");
        bottomPanel.add(messageTxt);
        bottomPanel.add(sendBtn);

        //Main frame settings
        mainFrame.getContentPane().add(BorderLayout.NORTH, topPanel);
        mainFrame.getContentPane().add(BorderLayout.CENTER,middlePanel);
        mainFrame.getContentPane().add(BorderLayout.SOUTH, bottomPanel);
        mainFrame.setVisible(true);

        //Connecting logic
        sendBtn.addActionListener(e ->{
            if(!messageTxt.getText().isEmpty()) {
                net.Send(messageTxt.getText());
                chatTxt.append("["+usernameTxt.getText()+"]  " + messageTxt.getText()+"\n");
                messageTxt.setText("");
                System.out.printf("SENDING MESSAGE: %s", messageTxt.getText());
            }
        });

        //Actions for "connect" Btn
        connectBtn.addActionListener(e -> {
            if(!serverPortTxt.getText().isEmpty() && !usernameTxt.getText().isEmpty()) {
                if(connectBtn.getText() == "Connect" && net.Connect(Integer.parseInt(serverPortTxt.getText()), Integer.parseInt(clientPortTxt.getText()), usernameTxt.getText())) {
                    connectBtn.setText("Disconnect");
                    System.out.printf("%s %s %s\n", "127.0.0.1", serverPortTxt.getText(), usernameTxt.getText());
                    usernameTxt.setEnabled(false);
                    clientPortTxt.setEnabled(false);
                    serverPortTxt.setEnabled(false);
                } else if (connectBtn.getText() == "Disconnect"){
                    connectBtn.setText("Connect");
                    net.Disconnect();
                    usernameTxt.setEnabled(true);
                    clientPortTxt.setEnabled(true);
                    serverPortTxt.setEnabled(true);
                }
            }
        });
    }
}
