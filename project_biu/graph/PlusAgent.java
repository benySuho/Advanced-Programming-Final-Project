package graph;

public class PlusAgent implements Agent {
    String name;
    String firstTopic;
    String secondTopic;
    String outputTopic;
    double firstMessage;
    double secondMessage;
    boolean firstReceived;
    boolean secondReceived;

    /**
     * Constructs a PlusAgent instance with the given name, subscriptions, and publications.
     *
     * @param name The name of the PlusAgent.
     * @param subs An array of strings representing the topics to subscribe to.
     *             Must have at least 2 elements.
     * @param pubs An array of strings representing the topics to publish to.
     *             Must have at least one element.
     * @throws IllegalArgumentException If the conditions for subs and pubs are not met.
     */
    public PlusAgent(String name, String[] subs, String[] pubs) {
        if (subs == null || pubs == null || subs.length < 1 || pubs.length < 1)
            throw new IllegalArgumentException("PlusAgent: subs must have at least 2 elements and pubs at least one");
        this.name = name;
        this.firstTopic = subs[0];
        this.secondTopic = subs[1];
        this.outputTopic = pubs[0];

        TopicManagerSingleton.get().getTopic(firstTopic).subscribe(this);
        TopicManagerSingleton.get().getTopic(secondTopic).subscribe(this);
        TopicManagerSingleton.get().getTopic(outputTopic).addPublisher(this);
    }

    public PlusAgent(String name, String[] subs, String pubs) {
        this(name, subs, new String[]{pubs});
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Resets the firstMessage and secondMessage values to 0.
     */
    @Override
    public void reset() {
        boolean firstReceived = false;
        boolean secondReceived = false;
        firstMessage = 0;
        secondMessage = 0;
    }

    /**
     * This method is called when a new message is received on a subscribed topic.
     *
     * @param topic The topic on which the message was received.
     * @param msg   The received message.
     *              If both messages have been received, it calculates the sum of messages
     *              and publishes the result on the outputTopic.
     */
    @Override
    public void callback(String topic, Message msg) {
        if (Double.isNaN(msg.asDouble))
            return;
        if (topic.equals(firstTopic)) {
            firstReceived = true;
            firstMessage = msg.asDouble;
        } else if (topic.equals(secondTopic)) {
            secondReceived = true;
            secondMessage = msg.asDouble;
        } else return;
        // Publish only if two messages received
        if (!(firstReceived && secondReceived))
            return;
        double out = firstMessage + secondMessage;
        TopicManagerSingleton.get().getTopic(outputTopic).publish(new Message(out));
    }

    @Override
    public void close() {
    }
}
