package database;

import com.mongodb.client.*;
import utils.ForkAlgorithm;

/**
 * astrazione delle connessione ad un database SENZA CREDENZIALI mongoDB
 *
 * @author Crescenzi Daniele
 * @see ForkAlgorithm
 * @see Override
 * @see utils.Overloaded
 * @see QueryPanel
 */

public sealed abstract class DatabaseConnection permits QueryPanel {

    private final String uri;   // Indica l'URL del tuo MongoDB "mongodb://localhost:27017"
    private final String databaseName;
    private final MongoClient mongoClient;  //client Mongo
    private final MongoDatabase database;

    protected DatabaseConnection(String uri, String databaseName) {
        /**
         * controlli e instaurazione connessione
         */
        checkStr(databaseName);
        if (uri.trim().startsWith("mongodb://")) {
            this.uri = uri.trim();
        } else throw new IllegalArgumentException("invalid URI");
        this.databaseName = databaseName.trim();
        mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase(databaseName);
    }

    private void checkStr(String s) {
        if (s == null) throw new IllegalArgumentException("NULL string");
        if (s.trim().isEmpty()) throw new IllegalArgumentException("Empty string");
    }

    protected void closeConnection() {
        mongoClient.close();
    }

    public String getUri() {
        return uri;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
