package com.tempoMessenger.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tempoMessenger.model.Device;

@RestController
@RequestMapping("/devices")
@CrossOrigin(origins = "*")
public class DeviceController {
	
	private Map<String, Device> devices = new ConcurrentHashMap<>();
	
	@Autowired
	private SimpMessagingTemplate messageTemplate;
	
	@PostMapping("/register")
	public void register(@RequestBody Device device) {
		device.setLastseen(System.currentTimeMillis());
		devices.put(device.getName(), device);
		System.out.println("Registered: " + device.getName());
		sendUpdatedDevices();
	}
	
	@GetMapping
	public List<Device> getDevice() {
		return getActiveDevices();
	}
	
	private List<Device> getActiveDevices() {
		
		long now = System.currentTimeMillis();
		
		return devices.values().stream()
		.filter(d -> now - d.getLastseen() < 2000)
		.collect(Collectors.toList());
	}
	
	private void sendUpdatedDevices() {
		messageTemplate.convertAndSend("/topic/devices", getActiveDevices());
	}
	
}
