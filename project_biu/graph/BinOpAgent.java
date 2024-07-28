package graph;

import java.util.function.BinaryOperator;

public class BinOpAgent implements Agent {
    String name;
    String firstTopic;
    String secondTopic;
    String outputTopic;
    double firstMessage;
    double secondMessage;
    boolean firstReceived;
    boolean secondReceived;
    BinaryOperator<Double> binOp;

    /**
     * Constructs a new instance of BinOpAgent. This agent subscribes to two input topics, performs a binary operation on the received
     * messages, and publishes the result to an output topic.
     *
     * @param name The name of the agent.
     * @param first The name of the first input topic.
     * @param second The name of the second input topic.
     * @param output The name of the output topic.
     * @param binOp Function: The binary operation to be performed on the received messages.
     */
    public BinOpAgent(String name, String first, String second, String output, BinaryOperator<Double> binOp) {
        this.name = name;
        this.firstTopic = first;
        this.secondTopic = second;
        this.outputTopic = output;
        this.binOp = binOp;

        TopicManagerSingleton.get().getTopic(first).subscribe(this);
        TopicManagerSingleton.get().getTopic(second).subscribe(this);
        TopicManagerSingleton.get().getTopic(output).addPublisher(this);
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
        }
        else return;
        // Publish only if two messages received
        if (!(firstReceived && secondReceived))
            return;
        double out = binOp.apply(firstMessage, secondMessage);
        TopicManagerSingleton.get().getTopic(outputTopic).publish(new Message(out));
    }

    @Override
    public void close() {
    }
}
