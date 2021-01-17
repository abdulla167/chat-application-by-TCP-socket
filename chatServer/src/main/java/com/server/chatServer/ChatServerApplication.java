package com.server.chatServer;

import com.server.chatServer.Sockets.ServerSocket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class ChatServerApplication {

	public static void main(String[] args) throws IOException {
		ApplicationContext context = SpringApplication.run(ChatServerApplication.class, args);
		ServerSocket serverSocket = (ServerSocket) context.getBean("serverSocket", context);
		serverSocket.start();
	}

}
