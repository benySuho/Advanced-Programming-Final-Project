package configs;

import graph.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;


public class GenericConfig implements Config {
    String filename;
    Set<Agent> agents;

    /**
     * This method creates and initializes the agents based on the configuration file.
     * <p>
     * The method reads the configuration file specified by the {@link #filename} attribute.
     * It expects the file to contain lines in the format:
     * <ul>
     *     <li>Agent class name</li>
     *     <li>Comma-separated list of subscription topics</li>
     *     <li>Comma-separated list of publication topics</li>
     * </ul>
     * Each set of three lines represents a single agent configuration.
     * <p>
     * If the file does not exist or contains invalid data, the method does nothing.
     * <p>
     * After reading the configuration, the method dynamically creates instances of the specified agent classes,
     * wraps them in {@link ParallelAgent} instances, and subscribes the parallel agents to the specified topics.
     */
    @Override
    public void create() {
        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        List<String> linesList = new ArrayList<>();
        try {
            // Read all lines from the file
            linesList = Files.readAllLines(Paths.get(filename));
            if (linesList.size() % 3 != 0) {
                linesList = null;
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        agents = new HashSet<>();
        for (int i = 0; i < linesList.size() / 3; i++) {
            String agentClass = "graph."+linesList.get(i * 3).split("\\.")[1];
            String[] subs = linesList.get(i * 3 + 1).split(",");
            String[] pubs = linesList.get(i * 3 + 2).split(",");

            Agent agent = (Agent) dynamicallyCreateAgentClass(agentClass, subs, pubs);
            agents.add(agent);
        }
    }

    @Override
    public String getName() {
        return "Generic Config";
    }

    @Override
    public int getVersion() {
        return 2;
    }

    /**
     * Closes all parallel agents associated with this configuration.
     * <p>
     * This method iterates through the set of agents and calls the {@link Agent#close()} method on each one.
     * After closing all the agents, it clears the set of agents.
     */
    @Override
    public void close() {
        for (Agent a : agents) {
            a.close();
        }
        agents.clear();
    }

    /**
     * Sets the configuration file path for the {@link GenericConfig} instance.
     *
     * <p>This method checks if the specified file exists before setting the file path.
     * If the file does not exist, the method does nothing.
     *
     * @param filename The path of the configuration file.
     */
    public void setConfFile(String filename) {
        if (Files.exists(Paths.get(filename)))
            this.filename = filename;
    }


    /**
     * Dynamically creates an instance of a specified agent class using reflection.
     *
     * @param className The fully qualified name of the agent class to be instantiated.
     * @param subs      An array of subscription topic names.
     * @param pubs      An array of publication topic names.
     * @return An instance of the specified agent class, or {@code null} if the class could not be found or instantiated.
     */
    private static Object dynamicallyCreateAgentClass(String className, String[] subs, String[] pubs) {
        try {
            // Load the class dynamically

            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor(new Class[]{String.class, String[].class, String[].class});
            Object o = constructor.newInstance(className, subs, pubs);
            return o;
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found: " + e.getMessage());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            System.out.println("Class not created: " + className);
        }
        return null;
    }
}
