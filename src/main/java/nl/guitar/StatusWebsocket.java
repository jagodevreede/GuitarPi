package nl.guitar;

import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;

import javax.websocket.Session;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Metered
@Timed
@ServerEndpoint("/status-ws")
public class StatusWebsocket {

    private static List<Session> sessions = new CopyOnWriteArrayList<>();

    @OnOpen
    public void onOpen(final Session session) throws IOException {
        session.getAsyncRemote().sendText("welcome");
        sessions.add(session);
    }

    @OnMessage
    public void onMsg(final Session session, String message) {
        System.out.println("Got ws message: " + message);
        session.getAsyncRemote().sendText(message.toUpperCase());
    }

    @OnClose
    public void onClose(final Session session, CloseReason cr) {
        sessions.remove(session);
    }

    public static void sendToAll(String message) {
        System.out.println("To all: " + message);
        for (Session session : sessions) {
            session.getAsyncRemote().sendText(message);
        }
    }


}
