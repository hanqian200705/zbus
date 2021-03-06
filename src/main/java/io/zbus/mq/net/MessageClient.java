package io.zbus.mq.net;

import java.util.List;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.zbus.mq.Message;
import io.zbus.net.CodecInitializer;
import io.zbus.net.Identifier;
import io.zbus.net.EventDriver;
import io.zbus.net.tcp.TcpClient;

public class MessageClient extends TcpClient<Message, Message>{
	
	public MessageClient(String address, final EventDriver driver){
		super(address, driver); 
		
		codec(new CodecInitializer() {
			@Override
			public void initPipeline(List<ChannelHandler> p) {
				p.add(new HttpRequestEncoder()); 
				p.add(new HttpResponseDecoder());  
				p.add(new HttpObjectAggregator(driver.getPackageSizeLimit()));
				p.add(new MessageToHttpWsCodec());
			}
		}); 
		
		//make message match work 
		setIdentifier(new Identifier<Message, Message>() { 
			@Override
			public void setRequestId(Message message, String id) { 
				message.setId(id);
			} 
			@Override
			public String getResponseId(Message message) { 
				return message.getId();
			}
		}); 
	}   
}
 
