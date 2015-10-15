package com.bezman.socket;

import com.bezman.annotation.OnSocketConnect;
import com.bezman.annotation.OnSocketEvent;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import org.springframework.stereotype.Component;

@Component
public class ConnectionListener
{
    @OnSocketConnect
    public void onSocketConnect(SocketIOClient socketIOClient)
    {
        System.out.println("Connectoin");
    }

    @OnSocketEvent("some")
    public void onSomeEvent(SocketIOClient socketIOClient, Integer some, AckRequest ackRequest)
    {
        System.out.println(some);
        ackRequest.sendAckData("Hello");
    }
}
