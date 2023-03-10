import java.io.*;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.BorderLayout;
import java.net.*;
class Server extends JFrame
{
    ServerSocket server;
    Socket socket;
    BufferedReader br; //For read data input
    PrintWriter out; //For output
    //Declare Components
    private JLabel heading=new JLabel("Server Side");
    private JTextArea messageArea=new JTextArea();
    private JTextField messageInput=new JTextField();
    private Font font=new Font("Roboto",Font.PLAIN,20);
    //Constructor
    public Server(){
        try {
            server=new ServerSocket(7777);
            System.out.println("Server is ready to accept connection");
            System.out.println("Waiting for client...");
            socket=server.accept();
            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out=new PrintWriter(socket.getOutputStream());
            createGUI();
            handleEvent();
            startReading();
            //startWriting();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private void handleEvent() {
        messageInput.addKeyListener(new KeyListener(){

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub
                //System.out.println("Key released "+e.getKeyCode());
                if(e.getKeyCode()==10){
                    //System.out.println("You have pressed enter button");
                    String contentToSend=messageInput.getText();
                    messageArea.append("ME: "+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }                
            }
        });
    }
    //Create GUI
    private void createGUI(){
        this.setTitle("Server Messenger[END]");
        this.setSize(500,500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Coding for component
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        heading.setIcon(new ImageIcon("chatlogo2.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageArea.setEditable(false);
        //Message ko bich se likhne k liye
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);
        //Set layout of frame
        this.setLayout(new BorderLayout());
        //Adding the components to the frame
        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane=new JScrollPane(messageArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);
        this.setVisible(true);
    }

    public void startReading(){
        //this thread read the data and give us
        Runnable r1=()->{
            System.out.println("Reader started...");
            try{
                while(true){          
                    String msg=br.readLine();
                    if(msg.equals("exit")){
                        System.out.println("Client terminated the chat");
                        JOptionPane.showMessageDialog(this, "Client terminated the chat");
                        //Client k exit operation k baad input ko disable krne k liye
                        messageInput.enable(false);
                        socket.close();
                        break;
                    }
                    //System.out.println("Client: "+msg);                
                    messageArea.append("Client: "+msg+"\n");
                }
            }
            catch(Exception e){
                //e.printStackTrace();
                System.out.println("Connection Closed");
            }
        };
        new Thread(r1).start();
    }
    public void startWriting(){
        //this thread takes data from user and send it to client
        Runnable r2=()->{
            System.out.println("Writer started...");
            try{
                while(!socket.isClosed()){
                    BufferedReader br1=new BufferedReader(new InputStreamReader(System.in));
                    String content=br1.readLine();
                    out.println(content);
                    out.flush();
                    if(content.equals("exit")){
                        socket.close();
                        break;
                    }
                }
                System.out.println("Connection Closed");
            }
            catch(Exception e){
                e.printStackTrace();
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is server");
        new Server();
    }
}