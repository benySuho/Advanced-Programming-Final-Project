package graph;

public class SqrtAgent implements Agent {
    String name;
    String inputTopic;
    String outputTopic;
    double message;

    /**
     * Constructs a new instance of {@link SqrtAgent} with the given name, input and output topics.
     *
     * @param name The name of the agent.
     * @param subs An array containing the input topic(s) for the agent. The first element is used.
     * @param pubs An array containing the output topic(s) for the agent. The first element is used.
     */
    public SqrtAgent(String name, String[] subs, String[] pubs) {
        if (subs == null || subs.length == 0 || pubs == null || pubs.length == 0) {
            throw new IllegalArgumentException("Both subs and pubs must contain at least one topic.");
        }
        if (subs.length > 1 || pubs.length > 1) {
            throw new IllegalArgumentException("subs and pubs can only contain one topic each.");
        }

        this.name = name;
        this.inputTopic = subs[0];
        this.outputTopic = pubs[0];
        TopicManagerSingleton.get().getTopic(inputTopic).subscribe(this);
        TopicManagerSingleton.get().getTopic(outputTopic).addPublisher(this);
    }

    public SqrtAgent(String name, String sub, String pub) {
        this(name, new String[]{sub}, new String[]{pub});
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        message = 0;
    }


    /**
     * This method is called when a new message is received on the subscribed topic.
     * It calculates the square root of the received message and publishes the result to the output topic.
     *
     * @param topic The topic on which the message was received.
     * @param msg   The received message. It must be a non-negative double value.
     */
    @Override
    public void callback(String topic, Message msg) {
        if (Double.isNaN(msg.asDouble) || msg.asDouble < 0) {
            return;
        }
        message = msg.asDouble;
        TopicManagerSingleton.get().getTopic(outputTopic).publish(new Message(Math.sqrt(message)));
    }

    @Override
    public void close() {
    }
}
