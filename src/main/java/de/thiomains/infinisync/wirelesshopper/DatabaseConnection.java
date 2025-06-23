package de.thiomains.infinisync.wirelesshopper;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseConnection {

    private WirelessHopper main;
    private Connection connection;

    public DatabaseConnection(WirelessHopper main) {
        this.main = main;
        connect();
        createTable();
    }

    private void connect() {
        String url = "jdbc:sqlite:wireless-hopper-data.db";

        try {
            Connection connection = DriverManager.getConnection(url);
            main.getLogger().info("SQLite connection established successfully");
            this.connection = connection;
        } catch (SQLException e) {
            main.getLogger().severe("SQLite connection failed: " + e.getMessage());
            main.getLogger().severe("Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(main);
        }
    }

    public void close() {
        try {
            connection.close();
            main.getLogger().info("SQLite connection closed successfully");
        } catch (SQLException e) {
            main.getLogger().severe("Could not close SQLite connection: " + e.getMessage());
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
            main.getLogger().severe("Could not create table: " + e.getMessage());
            main.getLogger().severe("Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(main);
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
            main.getLogger().severe("could not insert hopper connection: " + e.getMessage());
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
            main.getLogger().severe("could not get hopper connections: " + e.getMessage());
            main.getLogger().severe("Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(main);
            return null;
        }
    }

    public void deleteHopperConnection(HopperConnection hopperConnection) {
        String sql = "DELETE FROM connections WHERE source LIKE ?";

        Location source = hopperConnection.getSource();
        String sourceLocationString = source.getWorld().getName() + "," + source.getBlockX() + "," + source.getBlockY() + "," + source.getBlockZ();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, sourceLocationString);
            preparedStatement.execute();
        } catch (SQLException e) {
            main.getLogger().severe("could not delete hopper connection: " + e.getMessage());
            main.getLogger().severe("Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(main);
        }
    }

}
