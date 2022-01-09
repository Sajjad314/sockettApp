package practice;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GreetClient {

    private static Socket clientSocket;
    private static DataOutputStream out;
    private static DataInputStream in;
    private static String userName;
    static String message;

    public void name(String userName) {
        this.userName = userName;
    }

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());
    }

   

    public void stop() throws IOException {
        clientSocket.close();
        in.close();
        out.close();
    }

    public static void main(String[] args) throws IOException {
        GreetClient gc = new GreetClient();
        gc.startConnection("127.0.0.1", 1234);
        Scanner input = new Scanner(System.in);
        System.out.print("Enter your name to chat : ");
        String namee = input.nextLine();
        gc.name(namee);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Scanner scanner = new Scanner(System.in);
                    String msg = scanner.nextLine();
                    msg = userName + ": " + msg;
                    String [] arr = msg.split(" ",3);
                    if(arr[1].equals("send"))
                    {
                        
                        String dir = arr[2];
                        File file = new File(dir);
                        try {
                            out.writeUTF(userName + ": send " +file.getName());
                            FileInputStream fi = new FileInputStream(file.getPath());
                            byte[] b = new byte[(int)file.length()] ;
                            fi.read(b);
                            out.writeInt(b.length);
                            out.write(b);
                            
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(GreetClient.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(GreetClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else{
                    try {
                        out.writeUTF(msg);
                    } catch (IOException ex) {
                        Logger.getLogger(GreetClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    }
                }

            }
        }).start();
        while (true) {
            message = in.readUTF();
            String[] arr = message.split(" ", 3);
            if(arr[1].equals("send")){
              //  System.out.println(message);
                int fileLength = in.readInt();
                byte[] b = new byte[fileLength];
                in.readFully(b, 0, fileLength);
                FileOutputStream fo=new FileOutputStream("D:\\receive\\"+arr[2]) ;
                
                fo.write(b, 0, b.length);
                fo.close();
                System.out.println("file receved");
                
                
            }
            else{
            System.out.println(message);
            if (message.equals(".")) {
                String name;
                out.writeUTF(userName + "Good Bye");
                break;
            }
            }

        }
        

    }

}
