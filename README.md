# Advanced Programming Project

## Overview
This project is part of an advanced programming course and involves developing a generic HTTP server capable of handling various HTTP requests such as GET and POST. The server performs different operations based on client requests and provides dynamic, interactive views.

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

## Usage
1. **Open the Main HTML File:**
   Open `index.html` in a web browser.
2. **Submit Data via Forms:**
   Use the forms to input and submit data.
3. **View Updates:**
   Observe real-time updates in the computational graph and the table of current topic values.

## File Descriptions
- **index.html**: Main HTML file featuring iframes for forms, graph display, and table.
- **form.html**: Contains forms for user inputs.
- **graph.html**: Displays the graphical representation of the computational graph.
- **table.html**: Displays the current values of topics in a table format.
- **Server.java**: Core server class managing HTTP requests.
- **Graph.java**: Implements the logic for creating and managing the graph.
- **Node.java**: Represents individual nodes within the graph.
- **HtmlGraphWriter.java**: Generates HTML code for visualizing the graph.



