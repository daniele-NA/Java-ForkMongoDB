package database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import utils.ForkAlgorithm;
import utils.Overloaded;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * chiamata al database MongoDB ,con inserimento e svariate query di Json con
 * framework Fork Join nei risultati delle Query
 *
 * @author Crescenzi Daniele
 * @see ForkAlgorithm
 * @see database.DatabaseConnection
 * @see Override
 * @see utils.Overloaded
 */

public non-sealed class QueryPanel extends DatabaseConnection {

    private MongoCollection<Document> collection;   //collezione variabile di dati
    private String collectionName;

    private Map<String, Object> mapJson;

    public QueryPanel(String uri, String databaseName) {
        super(uri, databaseName);
    }

    public List<String> selectAll() {

        List<String> ret=new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(1);

        // Creazione Task
        Callable<Void> databaseTask = () -> {
            try {
                // Connessione al database MongoDB

                collection = super.getDatabase().getCollection(collectionName); // Nome della tua collection

                // Esegui una query per ottenere i documenti
                for (Document doc : collection.find()) {
                    ret.add(doc.toJson()+System.lineSeparator());
                }

            } catch (Exception e) {
                return null;
            }
            return null;
        };
        // Invia il task al pool
        Future<Void> future = executor.submit(databaseTask);

        // Attendere che il task finisca (opzionale)
        try {
            future.get(); // Blocco fino al completamento del task
        } catch (Exception e) {
            return null;
        }

        // Arrestare l'executor
        executor.shutdown();
        return ret;
    }

    @Overloaded
    public void insert(Document document) { //insert di un solo elemento

        collection = super.getDatabase().getCollection(collectionName); // Nome della tua collection
        collection.insertOne(document);
    }

    @Overloaded
    public void insert(String pathToJsonFile) {

        collection = super.getDatabase().getCollection(collectionName); // Nome della tua collection

        final ObjectMapper objectMapper = new ObjectMapper(); //trasforma da json a HashMap<>

        try {
            // Leggi il file JSON e convertilo in una HashMap
            File file = new File(pathToJsonFile); // Sostituisci con il percorso del tuo file JSON
            mapJson = objectMapper.readValue(file, HashMap.class);
            Document document = new Document(mapJson);
            collection.insertOne(document);


        } catch (IOException e) {
            throw new RuntimeException("Error reading json file");
        }
    }

    public void insertMany(List<Document> documents) { //inserimento di una lista di documenti
        collection = super.getDatabase().getCollection(collectionName);
        collection.insertMany(documents);
    }

    public void setCollectionName(String collectionName) throws NullPointerException {
        Objects.requireNonNull(collectionName);
        this.collectionName = collectionName.trim();
    }
    public void closeAll(){
        super.closeConnection();
    }

}




