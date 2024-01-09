import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class Main {
    private static final String TABLE_DIR_PATH= "tables";
    private static final String METADATA_PATH = "metadata.txt";

    public static void main(String[] args) throws IOException {
        System.out.println("You can use the following commands only:");
        System.out.println("CREATE TABLE <table_name> (col1 <datatype>,col2 <datatype>,...) -- to create a table");
        System.out.println("INSERT INTO <table_name> VALUES (val1,val2,...) -- to insert values in a table");
        System.out.println("VIEW <table_name> -- to Print all the Data in the table");
        System.out.println("SHOW -- to Print all the Data in the database");
        System.out.println("EXIT or QUIT -- to terminate the program");
        boolean flag=true;
        Scanner scn = new Scanner(System.in);
        while(flag){
            String command = scn.nextLine();
            if(command.equalsIgnoreCase("EXIT")||command.equalsIgnoreCase("QUIT")) {
                flag = false;
                System.out.println("The program has been terminated.");
            }
            else
                executeCommand(command);
        }
    }

    public static void executeCommand(String command) {
        String[] tokens = command.split("\\s");
        String keyword = tokens[0];

        switch (keyword.toUpperCase()) {
            case "CREATE" -> createTable(command);
            case "INSERT" -> insertIntoTable(command);
            case "SHOW" -> showTableData(tokens[1]);
            case "SHOWALL" -> showAllTables();
            default -> System.out.println("Invalid command.");
        }
    }

    private static void createTable(String command) {

//        System.out.println(command);
        // Implement the logic to parse and execute the create table command
        String[] tokens = command.split("[(\\s)]+");
        String tableName = tokens[2];
        String columnsDefinition = command.substring(command.indexOf("(") + 1, command.indexOf(")"));

        if (tableExists(tableName)) {
            System.out.println("Table already exists: " + tableName);
            return;
        }

        try (PrintWriter metadataWriter = new PrintWriter(new FileWriter(METADATA_PATH, true));
             PrintWriter dataWriter = new PrintWriter(new FileWriter(TABLE_DIR_PATH + File.separator + tableName + ".txt", true))) {
            metadataWriter.println(tableName + "," + columnsDefinition);
            System.out.println("Table created successfully: " + "table: " + tableName + " ,columns: " + columnsDefinition);
        } catch (IOException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    private static void insertIntoTable(String command) {

        String[] tokens = command.split("[(\\s)]+");
        String tableName = tokens[2];

        if (!tableExists(tableName)) {
            System.out.println("Table not found: " + tableName);
            return;
        }

        Pattern pattern = Pattern.compile("\\((.*?)\\)");

        // Create an ArrayList to store the extracted values
        ArrayList<String> extractedValues = getStrings(command, pattern);

        if(extractedValues.isEmpty()){
            System.out.println("No values to insert");
            return;
        }

        for (String value : extractedValues) {
            String [] valSplit = value.split(",");
           int  valueLength= valSplit.length;
            if (columnsCount(tableName) < valueLength) {
                System.out.println("Invalid number of values: "+ value+ " Length: "+ valueLength + " ,Allowed: " + columnsCount(tableName));

            }
            else if(columnsCount(tableName) > valueLength){
                System.out.println("Invalid number of values: "+ value+ " Length: "+ valueLength + " ,Allowed: " + columnsCount(tableName));
            }
            else{
                try (PrintWriter tableWriter = new PrintWriter(new FileWriter(TABLE_DIR_PATH + File.separator + tableName + ".txt", true))) {
                    tableWriter.println(value);
                    System.out.println("Values inserted successfully: " + "table: " + tableName + " , values: " + value);
                } catch (IOException e) {
                    System.err.println("Error inserting into table: " + e.getMessage());
                }
            }
        }

    }

    private static ArrayList<String> getStrings(String command, Pattern pattern) {
        ArrayList<String> extractedValues = new ArrayList<>();

        // Create a Matcher for the input string
        Matcher matcher = pattern.matcher(command);

        // Find and store values in the ArrayList
        while (matcher.find()) {
            String extractedValue = matcher.group(1);// Group 1 contains the value between parentheses
//            System.out.println(extractedValue);
            extractedValues.add(extractedValue);
        }
        return extractedValues;
    }

    private static int columnsCount(String tableName) {
        try (BufferedReader metadataReader = new BufferedReader(new FileReader(METADATA_PATH))) {
            String line;
            while ((line = metadataReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(tableName)) {
                    return parts.length - 1;
                }
            }
        } catch (IOException e) {
            System.err.println("Error checking if table exists: " + e.getMessage());
        }
        return 0;
    }

    private static void showTableData(String tableName) {

        if(tableName.isEmpty()){
            System.out.println("Table name cannot be empty");
            return;
        }

        try (BufferedReader metadataReader = new BufferedReader(new FileReader(METADATA_PATH));
             BufferedReader dataReader = new BufferedReader(new FileReader(TABLE_DIR_PATH + File.separator + tableName + ".txt"))) {

            String metadataLine;
            while ((metadataLine = metadataReader.readLine()) != null) {
                String[] metadataParts = metadataLine.split(",");
                if (metadataParts[0].equals(tableName)) {

                    printTableData(dataReader, tableName, metadataParts);
                    return;
                }
            }
            System.out.println("Table not found: " + tableName);
        } catch (IOException e) {
            System.err.println("Error showing table metadata and data: " + e.getMessage());
        }
    }


    private static void showAllTables() {

       File[] tableNames = new File(TABLE_DIR_PATH).listFiles();
       if(tableNames == null){
           System.out.println("No tables found");
           return;
       }
        for(File tableName : tableNames) {
//            System.out.println(tableName);
           String tname= tableName.toString().substring(7, tableName.toString().length()-4);
           try (BufferedReader metadataReader = new BufferedReader(new FileReader(METADATA_PATH));
                BufferedReader dataReader = new BufferedReader(new FileReader(tableName.toString()))) {

               String metadataLine;
               while ((metadataLine = metadataReader.readLine()) != null) {
                   String[] metadataParts = metadataLine.split(",");
                   if (metadataParts[0].equals(tname)) {
                       printTableData(dataReader, tname, metadataParts);
                       System.out.println();
                   }
               }

           } catch (IOException e) {
               System.err.println("Error showing all tables: " + e.getMessage());
           }
       }
        return;
    }

    private static void printTableData(BufferedReader dataReader, String tname, String[] metadataParts) throws IOException {

        System.out.println("Table: " + metadataParts[0]);
        StringBuilder columns= new StringBuilder("Columns :");
        for(int i = 1; i < metadataParts.length; i++){
            if(i==metadataParts.length-1){
                columns.append(metadataParts[i]);
            }else{
                columns.append(metadataParts[i]).append(", ");
            }
        }
        System.out.println(columns);

        String dataLine;
        boolean flag = true;
        while ((dataLine = dataReader.readLine()) != null) {
            String[] dataParts = dataLine.split(",");
            if (dataParts[0] != null) {
                flag = false;
                System.out.println("Data: " + Arrays.toString(dataParts));
            }
        }
         if (flag){
            System.out.println("Data: [null]");
         }
    }

    private static boolean tableExists(String tableName) {
        try (BufferedReader metadataReader = new BufferedReader(new FileReader(METADATA_PATH))) {
            String line;
            while ((line = metadataReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(tableName)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error checking if table exists: " + e.getMessage());
        }
        return false;
    }
}

