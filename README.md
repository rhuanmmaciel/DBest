<img src="./assets/images/dbest-logo2.png" alt="Logo do DBest" width="256">

# Database Engine for Seamless Transformations (DBest)

**DBest** is an interactive database engine designed for creating, visualizing, and executing query plans. This engine enables users to design query execution plans through an intuitive drag-and-drop interface, making it easier to understand query execution, optimize performance, and integrate diverse data sources. DBest is ideal for teaching, data integration, and enhancing query efficiency.


Key features of the DBest engine include:  
- **Creating Query Plans**: Build complex query execution plans using a drag-and-drop interface.  
- **Visualizing Execution Trees**: Gain clear insights into query structures and operator relationships.  
- **Optimizing Performance**: Improve query efficiency by adjusting execution paths.  
- **Teaching Database Concepts**: Provide students with practical tools to learn query optimization and database internals.  
- **Integrating Data Sources**: Seamlessly combine data from heterogeneous data sources
  
<br>

---

<br>

## DBest as a Query Execution Tool

It is important to note that **DBest is not a query optimizer**. While traditional database management systems (DBMS) often include sophisticated query optimization techniques that automatically transform SQL queries into efficient execution plans, DBest operates differently. DBest's primary function is to **execute predefined query plans** that have been manually created by the user.


In DBest:

- **User-Defined Query Plans**: Users manually create query plans by selecting operators and arranging them into an execution tree. The query plan is explicitly designed by the user, specifying exactly how the query should be executed.
  
- **No Automatic Query Optimization**: DBest does not automatically optimize query plans. Unlike a DBMS, which analyzes queries to find the most efficient execution path, DBest assumes that the user has designed the query plan based on their specific needs and understanding of the data. 

- **Execution of Query Plans**: DBest's role is to **execute the provided query plan** exactly as it is defined. This includes reading the input data, applying the selected operators, and producing a result set based on the sequence of operations specified by the user.

While DBest does not optimize queries, it provides **query cost indicators** to help users analyze the performance of their query execution:

<!-- - **Execution Time**: DBest tracks the time it takes to execute each operator and the overall query plan. -->
- **Memory Usage**: DBest provides information on memory usage during query execution, helping users identify areas where memory may become a bottleneck.
- **Operator Costs**: Each operator in the query plan has an associated cost, and DBest can collect and report on the cost of executing individual operators, allowing users to identify expensive operations and adjust their query plans accordingly.

These indicators help users understand the efficiency of their query plans, enabling them to make informed decisions about how to optimize their plans manually if needed.

<br>

---

<br>


## Getting Started

To get started with DBest, follow these steps:

1. **Install DBest:**  
   Download the DBest JAR [file](DBest.jar) and run it to launch the tool. Make sure you have Java 17 installed, as it is required to execute the application.

2. **Read the [Wiki](https://github.com/mergen-sergio/DBest/wiki):**
   Explore the wiki for a comprehensive, step-by-step guide to using DBest. Learn how to create indexes, build query trees, and run queries effectively.

3. **Access Sample Data:**  
   Practice using the tool with sample data provided in the wiki. You can download the dataset [here](https://github.com/mergen-sergio/DBest/wiki/01a%20-%20tutorial-data).

<br>

---

<br>

## About DBest

DBest began as a project under the guidance of **Professor Sergio Mergen**, initially designed to teach database internals. Over time, it evolved to cover additional topics, including relational algebra, broadening its educational scope. Today, DBest also serves as a practical tool for seamlessly integrating data sources.

Special acknowledgment goes to **Rhuan Moreira Maciel** and **Luiz Henrique Broch Lago**, whose early contributions were important in the development and growth of the tool.






