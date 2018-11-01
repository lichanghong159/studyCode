package cn.lch.learn.code.chatroom.bio.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{
     Socket socket ;

     public ClientHandler(Socket socket) {
         this.socket = socket;
     }
     @Override
        public void run() {
         BufferedReader reader =null;
         PrintWriter pw =null;
         try {
             reader = new BufferedReader(new InputStreamReader(System.in));
              pw = new PrintWriter(socket.getOutputStream(), true);
              while (true){
                 String msg = reader.readLine();

                  pw.println(msg);
                  pw.flush();
                  if("bye".equalsIgnoreCase(msg)){
                    break;
                  }
              }
         } catch (IOException e) {
             e.printStackTrace();
         }finally {
             if(reader!=null){
                 try {
                     reader.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
             if(pw!=null){
                 pw.close();
             }
         }
         if(socket!=null){
             try {
                 socket.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
     }
    }