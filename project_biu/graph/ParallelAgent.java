package graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * A class that wraps an existing agent and provides parallelism by sending messages to the agent from a separate thread.
 * It uses a queue to hold messages until they can be sent, and a single thread to send messages from the queue.
 */
public class ParallelAgent implements Agent {
    Agent agent;
    // A queue to hold messages until they can be sent
    BlockingQueue<MessageQueued> queue;
    // A flag to indicate whether the agent is running
    boolean running;
    // The thread that sends messages from the queue, Exercise requires one thread
    Thread senderThread;

    /**
     * A class to hold a topic and a message together in queue.
     */
    public static class MessageQueued {
        String topic;
        Message msg;

        /**
         * Constructor for MessageQueued.
         *
         * @param topic The topic of the message.
         * @param msg   The message.
         */
        MessageQueued(String topic, Message msg) {
            this.msg = msg;
            this.topic = topic;
        }
    }

    /**
     * Constructor for ParallelAgent.
     *
     * @param agent     The agent to be wrapped.
     * @param queueSize The size of the queue to hold messages.
     */
    public ParallelAgent(Agent agent, int queueSize) {
        this.agent = agent;
        // Initialize the queue with the given size
        queue = new ArrayBlockingQueue<>(queueSize);
        running = true;
        senderThread = new Thread(() -> {
            while (running) {
                MessageQueued msg = take(); // Waits if queue Empty
                if (msg != null) {
                    // Send the message to the agent
                    agent.callback(msg.topic, msg.msg);
                }
            }
        });
        senderThread.start();
    }

    /**
     * Take a message from the queue.
     *
     * @return The message taken from the queue, or null if the thread is interrupted.
     */
    public MessageQueued take() {
        try {
            return queue.take(); // Waits if queue Empty
        } catch (InterruptedException e) {
            return null; // in case of thread stopped
        }
    }

    /**
     * Put a message into the queue.
     *
     * @param msg The message to be put into the queue.
     */
    public void put(MessageQueued msg) {
        try {
            queue.put(msg);
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Get the name of the agent.
     *
     * @return The name of the agent.
     */
    @Override
    public String getName() {
        return agent.getName();
    }

    /**
     * Reset the agent.
     */
    @Override
    public void reset() {
        agent.reset();
    }

    /**
     * Send a message to the agent.
     *
     * @param topic The topic of the message.
     * @param msg   The message.
     */
    @Override
    public void callback(String topic, Message msg) {
        put(new MessageQueued(topic, msg));
    }

    /**
     * Stop the agent and the sender thread.
     */
    @Override
    public void close() {
        running = false;
        senderThread.interrupt(); // if take() waits to new message
    }
}