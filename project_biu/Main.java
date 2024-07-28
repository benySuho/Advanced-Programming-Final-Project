import server.*;
import servlets.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {
    private static boolean stop;

    public static void startServer() {
        HTTPServer myServer = new MyHTTPServer(8080, 5);

        myServer.addServlet("GET", "/publish", new TopicDisplayer());
        myServer.addServlet("GET", "/app/", new HtmlLoader("html_files"));
        myServer.addServlet("POST", "/upload", new ConfLoader());

        myServer.start();
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.out.println("Can't start server");
            return;
        }
        String ipAddress = inetAddress.getHostAddress();
        System.out.println("Server started on " + ipAddress + ":8080");
        System.out.println("Enter q to stop the server.");
        while (!stop) {
            try {
                stop = System.in.read() == 'q';
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        myServer.close();
    }

    public static void main(String[] args) {
        startServer();
    }

}

