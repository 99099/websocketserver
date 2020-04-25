package com.sanshi.servers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用javax.websocket,部署到tomcat进行使用
 * ws://127.0.0.1:8080/ws/chat
 */
@ServerEndpoint("/chat")
public class ChatServerV2 {

    private final static Logger logger = LoggerFactory.getLogger(ChatServerV2.class);

    private Session session;

    private static AtomicInteger count = new AtomicInteger(0);

    private static Set<ChatServerV2> websocketSet = Collections.synchronizedSet(new HashSet<ChatServerV2>());

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        addOnlineCount();
        websocketSet.add(this);
        for (ChatServerV2 e : websocketSet) {

            e.sendMessage(session.getId() + "进入聊天室，当前在线人数：" + count.get());
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("message from client {} : {}", session.getId(), message);
        for (ChatServerV2 item : websocketSet) {
            item.sendMessage(session.getId() + " : " + message);
        }
    }

    @OnClose
    public void onClose(Session session) {
        websocketSet.remove(this);
        subOnlineCount();
        for (ChatServerV2 e : websocketSet) {
            e.sendMessage(session.getId() + "离开聊天室，当前在线人数：" + count.get());
        }
    }


    @OnError
    public void OnError(Throwable error, Session session) {
        logger.error(session.getId() + " error {} occurs", error);
    }


    private static int addOnlineCount() {
        return count.incrementAndGet();
    }

    private static int subOnlineCount() {
        return count.decrementAndGet();
    }

    private void sendMessage(String text) {
        try {
            this.session.getBasicRemote().sendText(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
