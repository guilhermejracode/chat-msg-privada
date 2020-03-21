/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guilhermejra.socket;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author alunod
 */
public class Cliente implements Runnable{
    
    private ClientSocket clientSocket;
    private Scanner scanner;
    private PrintWriter out;
    
    public Cliente() throws IOException{
        clientSocket = new ClientSocket(new Socket("127.0.0.1", Servidor.PORT));
        scanner = new Scanner(System.in);
        System.out.println("Cliente conectado\r\n('sair' para finalizar)");
    }
    
    public void start(){
        String msg;
        
        System.out.println("Digite seu login: ");
        String login = scanner.nextLine();
        clientSocket.sendMsg(login);
        
        new Thread(this).start();
        
        do {            
            System.out.print("Digite uma msg: \r\n");
            msg = scanner.nextLine();
            clientSocket.sendMsg(msg);
        } while (!msg.equalsIgnoreCase("sair"));
    }
    
    public static void main(String[] args) {
        try {
            
            new Cliente().start();
            
        } catch (IOException ex) {
            System.err.println("Não foi possivel conectar no servidor: " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        String msg;
        do{
            msg = clientSocket.receiveMsg();
            System.out.println(msg);
        }while(msg != null && !msg.isEmpty());
    }
}
