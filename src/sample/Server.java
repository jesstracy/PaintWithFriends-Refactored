package sample;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import jodd.json.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jessicatracy on 9/6/16.
 */
public class Server implements Runnable {
    private GraphicsContext serverGC;
    private boolean myTurn = true;

    public Server(GraphicsContext gc) {
        this.serverGC = gc;
    }

    public void run() {
        try {
            startServer(serverGC);
        } catch (IOException exception) {
            System.out.println("Caught exception creating server socket or accepting client socket...");
            exception.printStackTrace();
        }
    }

    public void startServer(GraphicsContext serverGC) throws IOException {
        ServerSocket serverListener = new ServerSocket(8005);
        System.out.println("Listener ready to accept connections");

        // when it accepts a client socket, open a window.
        Socket clientSocket = serverListener.accept();
        //not getting to main's variable
//        myMain.myTurn = false;
        //**** THIS WILL BE FALSE EVENTUALLY ****
        myTurn = false;

        System.out.println("myMain myTurn should be false: " + myTurn);

        System.out.println("Incoming connection from " + clientSocket.getInetAddress().getHostAddress());

        BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter outputToClient = new PrintWriter(clientSocket.getOutputStream(), true);

        // test that the graphics context works!!
//        serverGC.strokeOval(50, 50, 50, 50);
        String clientInput;
        while ((clientInput = inputFromClient.readLine()) != null) {
            System.out.println("From ben: " + clientInput);
            if (clientInput.equals("switch")) {
                myTurn = !myTurn;
            }
            if (clientInput.substring(0,2).equals("0x")) {
//                Color paintColor = deserializeColor(clientInput);
                Paint myColor = Color.valueOf(clientInput);

                serverGC.setStroke(myColor);
            }
            if (!clientInput.equals("switch") && !clientInput.substring(0,2).equals("0x")) {
                Stroke deserializedStroke = jsonDeserializeStroke(clientInput);
//            serverGC.strokeOval(deserializedStroke.getxCoordinate(), deserializedStroke.getyCoordinate(), deserializedStroke.getStrokeSize(), deserializedStroke.getStrokeSize());
//                serverGC.setStroke(deserializedStroke.color);
                serverGC.strokeOval(deserializedStroke.getxCoordinate(), deserializedStroke.getyCoordinate(), deserializedStroke.getStrokeSize(), deserializedStroke.getStrokeSize());

                // tell client you received their stroke
//                outputToClient.println("Received your stroke!");
            }
        }

    }
    public Stroke jsonDeserializeStroke (String jsonString) {
        JsonParser myParser = new JsonParser();
        Stroke myStrokeObject = myParser.parse(jsonString, Stroke.class);
        return myStrokeObject;
    }

//    public Color deserializeColor(String jsonString) {
//        Color myPaintObject = myParser.parse(jsonString, Color.class);
//        return myPaintObject;
//    }



    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }
}
