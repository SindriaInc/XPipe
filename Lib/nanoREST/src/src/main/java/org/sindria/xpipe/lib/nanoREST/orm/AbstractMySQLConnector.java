package org.sindria.xpipe.lib.nanoREST.orm;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class for securely connecting to a MySQL database without JDBC or other dependencies.
 */
public abstract class AbstractMySQLConnector {

    private static final Logger LOGGER = Logger.getLogger(AbstractMySQLConnector.class.getName());
    private final String hostname;
    private final int port;
    private final String username;
    private final String password;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private int sequenceId = 0;

    /**
     * Constructor to set up the connection details.
     * @param hostname MySQL server hostname.
     * @param port MySQL server port.
     * @param username Username for authentication.
     * @param password Password for authentication.
     */
    public AbstractMySQLConnector(String hostname, int port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * Abstract method to process data received from the MySQL server.
     * Implementation should define how to handle server response.
     * @param data The raw byte data received.
     */
    protected abstract void processServerResponse(byte[] data);

    /**
     * Connect to the MySQL server.
     * @throws IOException If there is an error in creating the connection.
     */
    public void connect() throws IOException {
        LOGGER.info("Attempting to connect to MySQL server...");
        this.socket = new Socket(hostname, port);
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        handshake();
        LOGGER.info("Connected successfully.");
    }

    /**
     * Perform the MySQL handshake for authentication.
     * @throws IOException If there is an error in communication.
     */
    private void handshake() throws IOException {
        byte[] initialResponse = new byte[1024];
        int bytesRead = inputStream.read(initialResponse);

        if (bytesRead > 0) {
            processServerResponse(initialResponse);
            sendAuthenticationPacket();
        } else {
            throw new IOException("Failed to receive handshake from server.");
        }
    }

    /**
     * Send a properly formatted MySQL authentication packet.
     * @throws IOException If there is an error in sending data.
     */
    private void sendAuthenticationPacket() throws IOException {
        byte[] authPayload = (username + "\0" + password + "\0").getBytes(StandardCharsets.UTF_8);
        sendPacket(authPayload);
    }

    /**
     * Send a properly formatted MySQL packet.
     * @param payload The payload data.
     * @throws IOException If there is an error in communication.
     */
    private void sendPacket(byte[] payload) throws IOException {
        int packetLength = payload.length;
        ByteBuffer packet = ByteBuffer.allocate(4 + packetLength);
        packet.put((byte) (packetLength & 0xFF));
        packet.put((byte) ((packetLength >> 8) & 0xFF));
        packet.put((byte) ((packetLength >> 16) & 0xFF));
        packet.put((byte) sequenceId++);
        packet.put(payload);
        outputStream.write(packet.array());
        outputStream.flush();
    }

    /**
     * Close the connection to the MySQL server.
     */
    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                LOGGER.info("Disconnected from MySQL server.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error closing connection", e);
        }
    }

    /**
     * Execute a generic query and return the result as a String or Object.
     * @param query The SQL query to execute.
     * @return Query result as String or Object.
     * @throws IOException If there is an error in communication.
     */
    public Object executeQuery(String query) throws IOException {
        if (socket == null || socket.isClosed()) {
            throw new IOException("Not connected to the server.");
        }

        byte[] queryBytes = query.getBytes(StandardCharsets.UTF_8);
        sendPacket(queryBytes);

        byte[] response = new byte[4096];
        int bytesRead = inputStream.read(response);
        if (bytesRead > 0) {
            return new String(response, 0, bytesRead, StandardCharsets.UTF_8);
        } else {
            throw new IOException("No response received for the query.");
        }
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static void main(String[] args) {
        AbstractMySQLConnector connector = new AbstractMySQLConnector("localhost", 3306, "root", "password") {
            @Override
            protected void processServerResponse(byte[] data) {
                LOGGER.info("Server Response: " + new String(data, StandardCharsets.UTF_8));
            }
        };

        try {
            connector.connect();
            Object result = connector.executeQuery("SELECT * FROM users;");
            LOGGER.info("Query Result: " + result);
            connector.disconnect();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error during MySQL operation", e);
        }
    }
}
