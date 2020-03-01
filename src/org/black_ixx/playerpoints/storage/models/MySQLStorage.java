package org.black_ixx.playerpoints.storage.models;

import lib.PatPeter.SQLibrary.MySQL;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.RootConfig;
import org.black_ixx.playerpoints.storage.DatabaseStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;

/**
 * Storage handler for MySQL source.
 * 
 * @author Mitsugaru
 */
public class MySQLStorage extends DatabaseStorage {

    /**
     * MYSQL reference.
     */
    private MySQL mysql;
    /**
     * Number of attempts to reconnect before completely failing an operation.
     */
    private final int retryLimit;
    /**
     * The table name to use.
     */
    private final String tableName;
    /**
     * Current retry count.
     */
    private int retryCount;
    /**
     * Skip operation flag.
     */
    private boolean skip;

    /**
     * Constructor.
     *
     * @param plugin - Plugin instance.
     */
    public MySQLStorage(final PlayerPoints plugin) {
        super(plugin);
        final RootConfig config = plugin.getModuleForClass(RootConfig.class);
        if (config.debugDatabase) {
            plugin.getLogger().info("Constructor");
        }
        retryLimit = config.retryLimit;
        //setup table name and strings
        tableName = config.table;
        SetupQueries(tableName);
        //Connect
        connect();
        if (!mysql.isTable(tableName)) {
            build();
        }
    }

    @Override
    public int getPoints(final String id) {
        int points = 0;
        final RootConfig config = plugin.getModuleForClass(RootConfig.class);
        if (id == null || id.isEmpty()) {
            if (config.debugDatabase) {
                plugin.getLogger().info("getPoints() - bad ID");
            }
            return points;
        }
        if (config.debugDatabase) {
            plugin.getLogger().info("getPoints(" + id + ")");
        }
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = mysql.prepare(GET_POINTS);
            statement.setString(1, id);
            result = mysql.query(statement);
            if (result != null && result.next()) {
                points = result.getInt("points");
            }
        }
        catch (final SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                                   "Could not create getter statement.", e);
            retryCount++;
            connect();
            if (!skip) {
                points = getPoints(id);
            }
        }
        finally {
            cleanup(result, statement);
        }
        retryCount = 0;
        if(config.debugDatabase) {
        	plugin.getLogger().info("getPlayers() result - " + points);
        }
        return points;
    }

    @Override
    public boolean setPoints(final String id, final int points) {
        boolean value = false;
        final RootConfig config = plugin.getModuleForClass(RootConfig.class);
        if (id == null || id.isEmpty()) {
            if (config.debugDatabase) {
                plugin.getLogger().info("setPoints() - bad ID");
            }
            return false;
        }
        if (config.debugDatabase) {
            plugin.getLogger().info("setPoints(" + id + "," + points + ")");
        }
        final boolean exists = playerEntryExists(id);
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            if (exists) {
                statement = mysql.prepare(UPDATE_PLAYER);
            }
            else {
                statement = mysql.prepare(INSERT_PLAYER);
            }
            statement.setInt(1, points);
            statement.setString(2, id);
            result = mysql.query(statement);
            value  = true;
        }
        catch (final SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                                   "Could not create setter statement.", e);
            retryCount++;
            connect();
            if (!skip) {
                value = setPoints(id, points);
            }
        }
        finally {
            cleanup(result, statement);
        }
        retryCount = 0;
        if(config.debugDatabase) {
        	plugin.getLogger().info("setPoints() result - " + value);
        }
        return value;
    }

    @Override
    public boolean playerEntryExists(final String id) {
        boolean has = false;
        final RootConfig config = plugin.getModuleForClass(RootConfig.class);
        if (id == null || id.isEmpty()) {
            if (config.debugDatabase) {
                plugin.getLogger().info("playerEntryExists() - bad ID");
            }
            return false;
        }
        if (config.debugDatabase) {
            plugin.getLogger().info("playerEntryExists(" + id + ")");
        }
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = mysql.prepare(GET_POINTS);
            statement.setString(1, id);
            result = mysql.query(statement);
            if (result.next()) {
                has = true;
            }
        }
        catch (final SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                                   "Could not create player check statement.", e);
            retryCount++;
            connect();
            if (!skip) {
                has = playerEntryExists(id);
            }
        }
        finally {
            cleanup(result, statement);
        }
        retryCount = 0;
        if(config.debugDatabase) {
        	plugin.getLogger().info("playerEntryExists() result - " + has);
        }
        return has;
    }

    @Override
    public boolean removePlayer(final String id) {
        boolean deleted = false;
        if (id == null || id.isEmpty()) {
            return false;
        }
        PreparedStatement statement = null;
        ResultSet result = null;
        final RootConfig config = plugin.getModuleForClass(RootConfig.class);
        if (config.debugDatabase) {
            plugin.getLogger().info("removePlayers(" + id + ")");
        }
        try {
            statement = mysql.prepare(REMOVE_PLAYER);
            statement.setString(1, id);
            result  = mysql.query(statement);
            deleted = true;
        }
        catch (final SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                                   "Could not create player remove statement.", e);
            retryCount++;
            connect();
            if (!skip) {
                deleted = playerEntryExists(id);
            }
        }
        finally {
            cleanup(result, statement);
        }
        retryCount = 0;
        if(config.debugDatabase) {
        	plugin.getLogger().info("renovePlayers() result - " + deleted);
        }
        return deleted;
    }

    @Override
    public Collection<String> getPlayers() {
        final Collection<String> players = new HashSet<>();

        final RootConfig config = plugin.getModuleForClass(RootConfig.class);
        if (config.debugDatabase) {
            plugin.getLogger().info("Attempting getPlayers()");
        }
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = mysql.prepare(GET_PLAYERS);
            result    = mysql.query(statement);

            while (result.next()) {
                final String name = result.getString("playername");
                if (name != null) {
                    players.add(name);
                }
            }
        }
        catch (final SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                                   "Could not create get players statement.", e);
            retryCount++;
            connect();
            if (!skip) {
                players.clear();
                players.addAll(getPlayers());
            }
        }
        finally {
            cleanup(result, statement);
        }
        retryCount = 0;
        if(config.debugDatabase) {
        	plugin.getLogger().info("getPlayers() result - " + players.size());
        }
        return players;
    }

    /**
     * Connect to MySQL database. Close existing connection if one exists.
     */
    private void connect() {
        final RootConfig config = plugin.getModuleForClass(RootConfig.class);
        if (mysql != null) {
            if (config.debugDatabase) {
                plugin.getLogger().info("Closing existing MySQL connection");
            }
            mysql.close();
        }
        mysql = new MySQL(plugin.getLogger(), " ", config.host,
                          Integer.parseInt(config.port), config.database, config.user,
                          config.password);
        if (config.debugDatabase) {
            plugin.getLogger().info("Attempting MySQL connection to " + config.user + "@" + config.host + ":" + config.port + "/" + config.database);
        }
        if (retryCount < retryLimit) {
            mysql.open();
        }
        else {
            plugin.getLogger().severe(
                    "Tried connecting to MySQL " + retryLimit
                            + " times and could not connect.");
            plugin.getLogger()
                    .severe("It may be in your best interest to restart the plugin / server.");
            retryCount = 0;
            skip = true;
        }
    }

    @Override
    public boolean destroy() {
        boolean success = false;
        final RootConfig config = plugin.getModuleForClass(RootConfig.class);
        if (config.debugDatabase) {
            plugin.getLogger().info("Dropping playerpoints table");
        }
        try {
            mysql.query(String.format("DROP TABLE %s;", tableName));
            success = true;
        }
        catch (final SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                                   "Could not drop MySQL table.", e);
        }
        return success;
    }

    @Override
    public boolean build() {
        boolean success = false;
        final RootConfig config = plugin.getModuleForClass(RootConfig.class);
        if (config.debugDatabase) {
            plugin.getLogger().info(String.format("Creating %s table", tableName));
        }
        try {
            mysql.query(String.format("CREATE TABLE %s (id INT UNSIGNED NOT NULL AUTO_INCREMENT, playername varchar(36) NOT NULL, points INT NOT NULL, PRIMARY KEY(id), UNIQUE(playername));", tableName));
            success = true;
        }
        catch (final SQLException e) {
            plugin.getLogger().log(Level.SEVERE,
                                   "Could not create MySQL table.", e);
        }
        return success;
    }

}
