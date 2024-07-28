package graph;

import java.util.Set;
import java.util.HashSet;

public class Topic {
    public final String name;
    Set<Agent> subs;
    Set<Agent> pubs;
    Message lastMsg;

    /**
     * Constructs a new Topic with the given name.
     *
     * @param name The name of the Topic. It should be unique within the system.
     * @throws IllegalArgumentException If the provided name is null or empty.
     */
    Topic(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Topic name cannot be null or empty");
        }
        this.name = name;
        subs = new HashSet<Agent>();
        pubs = new HashSet<Agent>();
    }

    public void subscribe(Agent a) {
        subs.add(a);
    }

    public void unsubscribe(Agent a) {
        subs.remove(a);
    }

    public void publish(Message m) {
        lastMsg = m; // Update last message received by the topic
        for (Agent sub : subs) {
            sub.callback(this.name, m); // Send message
        }
    }

    public void addPublisher(Agent a) {
        pubs.add(a);
    }

    public void removePublisher(Agent a) {
        pubs.remove(a);
    }

    public Message getLastMsg() {
        if (lastMsg == null) {
            return null;
        }
        return lastMsg;
    }
}
