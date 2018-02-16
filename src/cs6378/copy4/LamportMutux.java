package cs6378.copy4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import cs6378.Message;

public class LamportMutux {
	private NClient client;
	private boolean send_request;
	private List<Message> messageList;
	private HashSet<Integer> pending_replies;

	public LamportMutux(NClient client) {
		this.client = client;
		this.send_request = false;

		messageList = Collections.synchronizedList(new ArrayList<Message>() {
			public synchronized boolean add(Message message) {
				boolean ret = super.add(message);
				Collections.sort(messageList);
				return ret;
			}
		});
	}

	public synchronized void replyMessage(Message reply) {
		if (reply.getType().equals(LamportMsg.REPLY) && send_request) {
			pending_replies.remove(new Integer(reply.getTo()));
		} else {
			System.err.println("Error: this not reply message");
		}
	}

	public synchronized void queueRequest() {
		pending_replies = new HashSet<>(client.getReceivers().keySet());
		pending_replies.remove(new Integer(client.getUID()));
		send_request = true;
	}

	public synchronized Message requestMessage(Message message) {
		if (message.getType().equals(LamportMsg.REQUEST)) {
			this.messageList.add(message);
			Message reply = new Message(client.getClock().getClock(), LamportMsg.REPLY, message.getTo(),
					message.getFrom());
			return reply;
		} else {
			System.err.println("Error: putting wrong type message into queue");
			return null;
		}
	}

	public synchronized boolean criticalSection() {
		if (!send_request) {
			client.broadcast(LamportMsg.REQUEST);
		}

		if (send_request && pending_replies.isEmpty() && messageList.get(0).getFrom() == client.getUID()) {
			send_request = false;
			return true;
		}
		return false;
	}

	public synchronized List<Message> getMessageList() {
		return messageList;
	}

	public synchronized void cleanUp() {
		this.messageList.clear();
	}

	public synchronized void releaseMessage(Message other) {
		int sender = other.getFrom();
		if (messageList.get(0).getFrom() == sender && messageList.get(0).getType().equals(LamportMsg.REQUEST)) {
			messageList.remove(0);
		} else {
			System.err.println("Error: message at head of list is not the message from process entering cs");
		}
	}

	public synchronized void releaseMessage() {
		if (send_request && messageList.get(0).getFrom() == client.getUID()) {
			messageList.remove(0);
			send_request = false;
		} else {
			System.err.println("Error: send release before send request");
		}
	}

	public static void main(String[] args) {

	}
}
