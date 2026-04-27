package com.tempoMessenger.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@MessageMapping("/connect.request")
	public void requestConnection(Map<String, String> payload) {
	    System.out.println("REQUEST: " + payload);

	    messagingTemplate.convertAndSend("/topic/connect/" + payload.get("to"), payload);
	}

	@MessageMapping("/connect.accept")
	public void acceptConnection(Map<String, String> payload) {
	    System.out.println("ACCEPT: " + payload);

	    String from = payload.get("from");
	    String to = payload.get("to");

	    String roomId = generateRoomId(from, to);

	    Map<String, String> response = new HashMap<>();
	    response.put("roomId", roomId);
	    response.put("from", from);
	    response.put("to", to);

	    messagingTemplate.convertAndSend("/topic/chat/" + from, response);
	    messagingTemplate.convertAndSend("/topic/chat/" + to, response);
	}
	
	@MessageMapping("/chat.send")
	public void sendMessage(Map<String, String> payload) {

	    String roomId = payload.get("roomId");
	    String sender = payload.get("sender");
	    String message = payload.get("message");

	    System.out.println("MESSAGE PAYLOAD: " + payload); // 🔍 debug

	    Map<String, String> response = new HashMap<>();
	    response.put("roomId", roomId);
	    response.put("sender", sender);   // ✅ force sender
	    response.put("message", message);

	    messagingTemplate.convertAndSend(
	        "/topic/messages/" + roomId,
	        response
	    );
	}
	
	public String generateRoomId(String a, String b) {
		return a.compareTo(b) < 0 ? a + "_" + b : b + "_" + a;
	}
}
