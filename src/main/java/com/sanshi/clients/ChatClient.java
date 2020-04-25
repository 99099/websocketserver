package com.sanshi.clients;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class ChatClient{

    private final static Logger logger = LoggerFactory.getLogger(ChatClient.class);

//    private static String url = "ws://127.0.0.1:8088";

    private static String url = "ws://127.0.0.1:8088/ws/chat";

    public static void main(String[] args) {
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Draft draft = new Draft_6455();
        WebSocketClient client = new WebSocketClient(uri, draft) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                logger.info("握手成功...");
            }

            @Override
            public void onMessage(String s) {
                logger.info("服务器：{}" , s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                logger.info("断开连接...");
            }

            @Override
            public void onError(Exception e) {
                logger.info("发生错误 {}", e);
            }
        };
        client.connect();

        while (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
            logger.info("正在连接...");
        }

        Scanner in = new Scanner(System.in);
        while (in.hasNext()) {
            String msg = in.next();
            logger.info("客户端：{}", msg);
            client.send(msg);
        }
    }

}
