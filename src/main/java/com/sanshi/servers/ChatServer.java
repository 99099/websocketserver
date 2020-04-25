package com.sanshi.servers;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 使用java-websocket框架，直接运行main函数
 */
public class ChatServer extends WebSocketServer {

    private final static Logger logger = LoggerFactory.getLogger(ChatServer.class);

    private static Set<WebSocket> wsSet = Collections.synchronizedSet(new HashSet<WebSocket>());

    private ChatServer(int port) {
        super(new InetSocketAddress(port));
    }

    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        String client = webSocket.getRemoteSocketAddress().getAddress().getHostAddress()
                + ":" + webSocket.getRemoteSocketAddress().getPort();
        wsSet.add(webSocket);
        logger.info("新客户端：{} 接入...", client);
        this.broadcast("新客户端: " + client + " 接入...");
    }

    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        wsSet.remove(webSocket);
        logger.info("连接关闭 ...");
    }

    public void onMessage(WebSocket webSocket, String s) {
        String client = webSocket.getRemoteSocketAddress().getAddress().getHostAddress()
                + ":" + webSocket.getRemoteSocketAddress().getPort();
        logger.info("{} : {}", client, s);
        this.broadcast(client + " : " + s);

    }

    public void onError(WebSocket webSocket, Exception e) {
        logger.info("{} 连接错误， {}", webSocket, e);
    }

    public void onStart() {
        logger.info("连接开始...");
    }


    /**
     * ws://127.0.0.1:8088
     */
    public static void main(String[] args) {
        ChatServer server = new ChatServer(8088);
        server.start();
    }
}
