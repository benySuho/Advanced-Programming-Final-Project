package server;

import servlets.Servlet;
import server.RequestParser.RequestInfo;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.*;


/**
 * This class represents a simple HTTP server that can handle multiple client connections concurrently.
 * It uses a fixed thread pool to process incoming requests and supports adding and removing servlets.
 */
public class MyHTTPServer extends Thread implements HTTPServer {
    private final int port;
    private final int nThreads;
    private ServerSocket serverSocket;
    private boolean running;
    private final ExecutorService executor;
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Servlet>> servletsMap;

    /**
     * Constructs a new MyHTTPServer instance.
     *
     * @param port     the port number on which the server will listen for incoming connections.
     * @param nThreads the number of threads in the thread pool.
     */
    public MyHTTPServer(int port, int nThreads) {
        this.port = port;
        this.nThreads = nThreads;
        this.executor = Executors.newFixedThreadPool(nThreads);
        this.servletsMap = new ConcurrentHashMap<>();
        this.servletsMap.put("GET", new ConcurrentHashMap<>());
        this.servletsMap.put("POST", new ConcurrentHashMap<>());
        this.servletsMap.put("DELETE", new ConcurrentHashMap<>());
    }

    /**
     * Adds a servlet to the server.
     *
     * @param httpCommand the HTTP command (GET, POST, DELETE) that the servlet will handle.
     * @param uri         the URI pattern that the servlet will match.
     * @param servlet           the servlet instance.
     */
    public void addServlet(String httpCommand, String uri, Servlet servlet) {
        this.servletsMap.get(httpCommand.toUpperCase()).put(uri, servlet);
    }

    /**
     * Removes a servlet from the server.
     *
     * @param httpCommand the HTTP command (GET, POST, DELETE) that the servlet was handling.
     * @param uri         the URI pattern that the servlet was matching.
     */
    public void removeServlet(String httpCommand, String uri) {
        this.servletsMap.get(httpCommand.toUpperCase()).remove(uri);
    }

    /**
     * Starts the server and begins listening for incoming connections.
     */
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(1000);
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executor.submit(() -> handleClient(clientSocket));
                } catch (SocketTimeoutException | SocketException ignored) {
                } catch (IOException e) {
                    e.printStackTrace();
                    if (serverSocket != null && !serverSocket.isClosed())
                        serverSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the server.
     */
    @Override
    public void start() {
        this.running = true;
        new Thread(this).start();
    }

    /**
     * Stops the server by shutting down the executor service,
     * and closing the server socket.
     */
    public void close() {
        this.running = false;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * Handles an incoming client connection by parsing the request, finding the appropriate servlet,
     * and invoking the servlet's handle method.
     *
     * @param clientSocket the socket connected to the client.
     */
    private void handleClient(Socket clientSocket) {
        try {
            RequestInfo requestInfo = RequestParser.parseRequest(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
            if (requestInfo == null) {
                sendNotFound(clientSocket.getOutputStream());
                return;
            }
            Servlet servlet = findServlet(requestInfo);
            if (servlet != null) {
                servlet.handle(requestInfo, clientSocket.getOutputStream());
            } else {
                sendNotFound(clientSocket.getOutputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Finds the appropriate servlet for a given request by matching the request's HTTP command and URI.
     *
     * @param requestInfo the parsed request information.
     * @return the servlet that matches the request, or null if no matching servlet is found.
     */
    private Servlet findServlet(RequestInfo requestInfo) {
        ConcurrentHashMap<String, Servlet> map = servletsMap.get(requestInfo.getHttpCommand().toUpperCase());
        String uri = requestInfo.getUri().split("\\?")[0];

        while (!uri.isEmpty()) {
            Servlet servlet = map.get(uri);
            if (servlet != null) {
                return servlet;
            }
            int lastSlash = uri.lastIndexOf('/');
            if (lastSlash == -1) {
                break;
            } else if (lastSlash == uri.length() - 1) {
                uri = uri.substring(0, lastSlash);
            } else
                uri = uri.substring(0, lastSlash + 1);
        }
        return null;
    }

    /**
     * Sends a 404 Not Found response to the client.
     *
     * @param out the output stream to send the response.
     * @throws IOException if an error occurs while writing to the output stream.
     */
    private void sendNotFound(OutputStream out) throws IOException {
        String response = "HTTP/1.1 404 Not Found\r\nContent-Length: 0\r\n\r\n";
        out.write(response.getBytes());
        out.flush();
    }
}
