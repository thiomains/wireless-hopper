package de.thiomains.infinisync.wirelesshopper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Hopper;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseConnection {

    private Connection connection;

    public DatabaseConnection() {
        connect();
        createTable();
    }

    private void connect() {
        String url = "jdbc:sqlite:wireless-hopper-data.db";

        try {
            Connection connection = DriverManager.getConnection(url);
            System.out.println("SQLite connection established");
            this.connection = connection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void close() {
        try {
            connection.close();
            System.out.println("SQLite connection closed successfully");
        } catch (SQLException e) {
            System.out.println("Could not close SQLite connection: " + e.getMessage());
        }
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS connections (" +
                "   source text PRIMARY KEY," +
                "   destination text NOT NULL" +
                ");";

        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertHopperConnection(Location source, Location destination) {
        String sourceLocationString = source.getWorld().getName() + "," + source.getBlockX() + "," + source.getBlockY() + "," + source.getBlockZ();
        String destinationLocationString = destination.getWorld().getName() + "," + destination.getBlockX() + "," + destination.getBlockY() + "," + destination.getBlockZ();

        String sql = "INSERT INTO connections(source, destination) VALUES (?,?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, sourceLocationString);

            preparedStatement.setString(2, destinationLocationString);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<HopperConnection> getAllHopperConnections() {
        String sql = "SELECT * FROM connections";

        ArrayList<HopperConnection> hopperConnections = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {

                String sourceString = resultSet.getString("source");
                String destinationString = resultSet.getString("destination");

                String[] sourceStringArray = sourceString.split(",");
                String[] destinationStringArray = destinationString.split(",");

                Location sourceHopperLocation = new Location(Bukkit.getWorld(sourceStringArray[0]), Double.parseDouble(sourceStringArray[1]), Double.parseDouble(sourceStringArray[2]), Double.parseDouble(sourceStringArray[3]));
                Location destinationHopperLocation = new Location(Bukkit.getWorld(destinationStringArray[0]), Double.parseDouble(destinationStringArray[1]), Double.parseDouble(destinationStringArray[2]), Double.parseDouble(destinationStringArray[3]));

                hopperConnections.add(new HopperConnection(sourceHopperLocation, destinationHopperLocation));
            }

            return hopperConnections;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void deleteHopperConnection(HopperConnection hopperConnection) {
        String sql = "DELETE FROM connections WHERE source = ?";

        Location source = hopperConnection.getSource();
        String sourceLocationString = source.getWorld().getName() + "," + source.getBlockX() + "," + source.getBlockY() + "," + source.getBlockZ();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, sourceLocationString);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
