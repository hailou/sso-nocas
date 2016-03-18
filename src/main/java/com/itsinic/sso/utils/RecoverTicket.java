package com.itsinic.sso.utils;



import com.itsinic.sso.model.Ticket;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RecoverTicket implements Runnable {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RecoverTicket.class);

	private Map<String, Ticket> tickets;
	
	public RecoverTicket(Map<String, Ticket> tickets) {
		super();
		this.tickets = tickets;
	}

	@Override
	public void run() {

		List<String> ticketKeys = new ArrayList<String>();
		for(Entry<String, Ticket> entry : tickets.entrySet()) {
			if(entry.getValue().getRecoverTime().getTime() < System.currentTimeMillis())
				ticketKeys.add(entry.getKey());
		}
		for(String ticketKey : ticketKeys) {
			tickets.remove(ticketKey);
			logger.info("====ticket[" + ticketKey + "]过期已删除！=====");
		}
	}

}
