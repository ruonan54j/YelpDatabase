package ca.ece.ubc.cpen221.mp5.Server;

import ca.ece.ubc.cpen221.mp5.Database.YelpDataBase;

import javax.json.stream.JsonParsingException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YelpServer {

    public static final int YELP_PORT = 4949;

    private ServerSocket serversocket;
    YelpDataBase ydb;

    public YelpServer() throws IOException {
        ydb = new YelpDataBase();
        serversocket = new ServerSocket(YELP_PORT);
    }

    public void serve() throws IOException {
        while(true){
            final Socket socket = serversocket.accept();

            Thread handler = new Thread(new Runnable() {
                public void run() {
                    try {
                        try {
                            handle(socket);
                        } finally {
                            socket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            handler.start();
        }
    }

    private void handle(Socket socket) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);


        //TODO: star rating cannot be over 5
        try {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.matches("^GETRESTAURANT .*")) {
                    Matcher m = Pattern.compile("^(?:GETRESTAURANT )(.*)(?:\\s*)$").matcher(line);
                    if (m.find()) {
                        try {
                            out.println(ydb.getRestaurant(m.group(1)));
                        } catch (IllegalArgumentException iae) {
                            out.println("invalid restaurant id");
                        }
                    } else {
                        out.println("invalid input format");
                    }
                } else if (line.matches("^ADDUSER .*")) {
                    Matcher m = Pattern.compile("^(?:ADDUSER )(.*)$").matcher(line);

                    if (m.find()) {
                        try {
                            out.println(ydb.addUser(m.group(1)));
                        } catch (IllegalArgumentException iae) {
                            out.println("invalid user string");
                        }
                    }
                }
                else if (line.matches("^ADDREVIEW .*")) {
                    Matcher m = Pattern.compile("^(?:ADDREVIEW )(.*)$").matcher(line);
                    if (m.find()) {
                        try {
                            out.println(ydb.addReview(m.group(1)));
                        } catch (IllegalArgumentException iae) {
                            out.println("invalid string");
                        }
                    }
                }
                else if (line.matches("^ADDRESTAURANT .*")) {
                    Matcher m = Pattern.compile("^(?:ADDRESTAURANT )(.*)$").matcher(line);
                    if (m.find()) {
                        try {
                            out.println(ydb.addRestaurant(m.group(1)));
                        } catch (IllegalArgumentException iae) {
                            out.println("invalid string");
                        }
                    } else {
                        out.println("invalid input format");
                    }
                }
                else if (line.matches("^ADDRESTAURANT .*")) {
                    Matcher m = Pattern.compile("^(?:ADDRESTAURANT )(.*)$").matcher(line);
                    if (m.find()) {
                        try {
                            out.println(ydb.addRestaurant(m.group(1)));
                        } catch (JsonParsingException jpe) {
                            out.println("Json string error");
                        }
                    } else {
                        out.println("invalid input format");
                    }
                }
                else out.println("unrecognized input");
            }
            out.flush();
        }
        finally {
            br.close();
            out.close();
        }
    }


    public static void main(String[] args){
        try{
            YelpServer server = new YelpServer();
            server.serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
/*
            out.println("What would you like to do?\n\nGETARESTAURANT <business id> : Returns the specified " +
                    "restaurant in json format\nADDUSER <user information> : <user information> is in the form" +
                    "{\"name\": <Your name>. A new user with a unique ID will be created for you and displayed " +
                    "on the screen in json format.\nADDRESTAURANT <restaurantinformation> : <restaurantinformation> " +
                    "is in the form: {\"latitide\": <double>, \"longitude\": <double>, \"name\": \"<name>\", " +
                    "\"neighborhood\": [\"<neighborhoods>\", \"<neighborhood>\"], \"full_address\": \"<full_address\"" +
                    ", \"city\": \"<city>\", \"state\": \"<state>\"}, where all the things in \"<>\" are filled out" +
                    "by you.\nADDREVIEW <review information> : add a review, with the following format: {\"starRating\"" +
                            ": <double>, \"text\": \"<reviewtext>\", \"user_id\": \"<userID>\", \"business_id\":" +
                            "\"<business_id>\"}" );
                            */