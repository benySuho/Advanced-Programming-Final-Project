# Advanced Programming Project

## Background
This project is part of an advanced programming course aimed at developing a deep understanding of essential software design patterns and architectures. The primary focus is on implementing a generic HTTP server capable of handling various types of HTTP requests, including GET and POST.

The core objective is to build a versatile system that can support a publisher/subscriber architecture for computational graph processing. In this setup, nodes in the graph represent different computational tasks that operate concurrently. Each node’s output serves as the input for other nodes, enabling the execution of complex calculations.

## Installation

### Prerequisites
- Java Development Kit (JDK) 8 or later
- A modern web browser with JavaScript enabled

### Steps
1. **Clone the Repository:**
    ```sh
    git clone (https://github.com/benySuho/Advanced-Programming-Final-Project)
    cd project_biu
    ```

2. **Compile the Project:**
    ```sh
    javac -d bin -sourcepath src src/*.java
    ```

3. **Run the Main script:**
    ```sh
   java project_biu\Main.java
    ```
4. **Access the Application**
   Open a web browser and navigate to http://localhost:8080/app/index.html to access the application interface.
   
## Project Structure
The project comprises the following main components:

- **Server**: Manages incoming HTTP requests and dispatches them to appropriate handlers.
- **Graph**: Extends `ArrayList<Node>` and includes methods for detecting cycles and constructing the graph from topics and agents.
- **Node**: Represents nodes within the graph, which can be either topics or agents.
- **HtmlGraphWriter**: Generates HTML representations of the computational graph.
- **Servlets**: Handle various requests such as publishing messages to topics, uploading configurations, and serving HTML files.

## Features
- **Dynamic Graph Visualization**: Displays a computational graph where topics are represented as rectangles and agents as circles, with arrows indicating graph direction.
- **Interactive Forms**: Allows users to input and submit data through web forms.
- **Real-Time Table Updates**: Displays current topic values in a dynamically updated table.

## Usage Example
**Basic Server Setup**
To run the HTTP server, you first need to instantiate the MyHTTPServer with a specified port number and the number of threads. After that, you can register different servlets to handle various types of HTTP requests. Here's a simple setup:

  ```sh
// Create an instance of MyHTTPServer with port 8080 and a thread pool of 20 threads
MyHTTPServer server = new MyHTTPServer(8080, 20);

// Register servlets to handle specific requests
server.addServlet("GET", "/calculate", new CalculateServlet()); // Handles GET requests to /calculate
server.addServlet("POST", "/calculator", new CalculatorServlet()); // Handles POST requests to /calculator

// Start the server
server.start();
 ```

