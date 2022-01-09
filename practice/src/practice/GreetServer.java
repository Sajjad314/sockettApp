package practice;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GreetServer {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());
        Scanner input = new Scanner(System.in);
        String name = input.nextLine();
        String message;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Scanner scanner = new Scanner(System.in);
                    String msg = scanner.nextLine();
                    
                    msg = name + ": " + msg;
                    String [] arr = msg.split(" ",3);
                    if(arr[1].equals("send"))
                    {
                        
                        String dir = arr[2];
                        File file = new File(dir);
                        try {
                            out.writeUTF(name + ": send " +file.getName());
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
                        out.writeUTF( msg);
                    } catch (IOException ex) {
                        Logger.getLogger(GreetServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    }
                }

            }
        }).start();
        while (true) {
            
            message = in.readUTF();
            String[] arr = message.split(" ", 3);
            if(arr[1].equals("send")){
                System.out.println(message);
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
                    out.writeUTF(name + "Good Bye");
                    break;
                }
            }
            
        }

    }

    public void stop() throws IOException {
        serverSocket.close();
        clientSocket.close();
        out.close();
        in.close();
    }

    public static void main(String[] args) throws IOException {
        GreetServer gs = new GreetServer();

        System.out.println("Enter your name to chat ...");

        gs.start(1234);
    }

}
