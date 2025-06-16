package de.thiomains.infinisync.wirelesshopper;

import org.bukkit.Location;

public class HopperConnection {

    private Location source;
    private Location destination;

    public HopperConnection(Location source, Location destination) {
        this.source = source;
        this.destination = destination;
    }

    public Location getSource() {
        return source;
    }

    public Location getDestination() {
        return destination;
    }
}
