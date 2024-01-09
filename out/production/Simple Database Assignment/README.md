# Simple Database
This is a simple Java program that implements a basic database system. It allows you to create tables and insert values into them. 
The program uses text files to store metadata and records.

## Usage
You can use the following commands:
- CREATE TABLE <table_name> (col1 <datatype>,col2 <datatype>,...) - Create a table with the specified columns and data types.
- INSERT INTO <table_name> VALUES (val1,val2,...) - Insert values into a table.
- SHOW <table_name> - Print all the Data in the table.
- SHOWALL - Print all the Data in the database.
- EXIT or QUIT - Terminate the program.
  
## Technical Stack Used
  - JAVA 8
  - IntelliJ IDEA
  
## Example Commands
Here are some example commands you can try:

- #### Create a table:
    ```
    CREATE TABLE my_table (col1 INTEGER,col2 STRING)
    ```
  
- #### Insert values into a table:
    ```
    INSERT INTO my_table VALUES (123,'Hello World')
    ```
- #### Show Table Data:
    ```
    SHOW my_table
    ```
- #### Show all Data in the database:
    ```
    SHOWALL
    ```
- ### Terminate the program:
    ```
    EXIT or QUIT
    ```
  
## File Structure
The program uses two files:
metadata.txt: Stores the metadata of the tables created.<br>
database.txt: Stores the records inserted into each table.<br>
Make sure both files are present in the same directory as the Java source file.
