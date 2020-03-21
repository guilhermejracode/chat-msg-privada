/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guilhermejra.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author aluno
 */
public class Servidor implements Runnable{
    
    public static final int PORT = 5000;
    private ServerSocket serverSocket;
    private List<ClientSocket> clienteSocketList;
    
    public Servidor() throws IOException{
        serverSocket = new ServerSocket(PORT);
        clienteSocketList = new LinkedList<>();
        System.out.println("Servidor iniciado.");
    }
    
    private void start() throws IOException{
        while(true){
            ClientSocket clienteSocket = new ClientSocket(serverSocket.accept());
            clienteSocketList.add(clienteSocket);
            new Thread(this).start();
        }
    }
    
    public static void main(String[] args) {
        try {
            new Servidor().start();
            
        } catch (IOException ex) {
            System.err.println("Não foi possivel iniciar o servidor: " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        ClientSocket clienteSocket = clienteSocketList.get(clienteSocketList.size()-1);
        
        String login = "";
        login = clienteSocket.receiveMsg();
        clienteSocket.setLogin(login);
        System.out.println("Cliente conectado: " + login);
        
        String msg;
        do{
            msg = clienteSocket.receiveMsg();
            System.out.println("Msg recebida de "+login+": "+msg);
            
            boolean destino = msg.matches("^@.*\\s.*");
            System.out.println("Msg privata: " + destino);
            if(destino){
                String[] msg_nova = msg.split(" ");
                String login_destino = msg_nova[0].substring(1);
                msg = "";
                for(int i=1;i<msg_nova.length;i++){
                    msg += msg_nova[i]+" ";
                }
                
                enviarMsgPrivada(login_destino, clienteSocket, login + " msg_privada: " + msg);
            }
            else{
                enviarMsgTodos(clienteSocket, login + ": " + msg);
            }
            
        }while(msg != null && !msg.isEmpty());
    }
    
    private void enviarMsgTodos(ClientSocket emissor, String msg){
        for (ClientSocket clientSocket1 : clienteSocketList) {
            if(clientSocket1 != emissor)
                clientSocket1.sendMsg(msg);
        }
    }
    
    private void enviarMsgPrivada(String destino, ClientSocket emissor, String msg){
        for (ClientSocket clientSocket1 : clienteSocketList) {
            if(clientSocket1 != emissor && clientSocket1.getLogin().equalsIgnoreCase(destino))
                clientSocket1.sendMsg(msg);
        }
    }
}
