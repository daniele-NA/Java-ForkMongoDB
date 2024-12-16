package view;

import database.QueryPanel;
import org.bson.Document;
import utils.ForkAlgorithm;

import java.util.*;

import static java.lang.System.err;
import static java.lang.System.out;

/**
 * chiamata al database MongoDB ,con inserimento e svariate query di Json con
 * framework Fork Join nei risultati delle Query
 *
 * @author Crescenzi Daniele
 * @see ForkAlgorithm
 * @see database.DatabaseConnection
 * @see Override
 * @see utils.Overloaded
 * @see QueryPanel
 */
public class Main {
    private static Scanner push;
    private static QueryPanel queryPanel;  //istanza del pannello di controllo
    private static Map<String, Object> map;   //oggetto che mappa chiavi e valori in input
    private static ForkAlgorithm<String> forkAlgorithm = new ForkAlgorithm<>();  //algoritmo ricorsivo per la stampa


    public static void main(String[] args) {

        err.println("Hi, this java program allows you to interact comfortably with your own MongoDB " + System.lineSeparator()
                + "default local URL -> mongodb://localhost:27017");

        while (true) {
            try {
                push = new Scanner(System.in);
                out.print("Enter database uri : ");
                String uri = push.next().trim();
                out.print("Enter database name : ");
                String dbName = push.next().trim();
                queryPanel = new QueryPanel(uri, dbName);
                break;
            } catch (Exception e) {
                err.println(e.getMessage());
            }
        }

        out.println("""
                                \s
                 d) sets a default collection name         \s
                 e) exit
                 s) select all
                 i) insert
                 p) insert with path file
                 m) insert many
                                \s
                \s""");
        while (true) {
            push = new Scanner(System.in);
            out.print(System.lineSeparator()+"Enter choice : ");
            char choice = push.next().toLowerCase().charAt(0);
            switch (choice) {
                case 'e' -> queryPanel.closeAll();  //chiusura connessione database
                case 's' -> out.println(forkAlgorithm.print(queryPanel.selectAll()));
                case 'i' -> queryPanel.insert(insertData());
                case 'p' -> {
                    out.print("enter json's path : ");

                    /*
                    controllo e sistemazione del path
                     */
                    String path = push.next().trim();
                    path=path.replaceAll("\"","");
                    path=path.replaceAll("\\\\","//").trim();
                    out.print(path);
                   try {
                       queryPanel.insert(path);
                   }catch (RuntimeException e) {
                       err.println(e.getMessage());
                   }
                }
                case 'm' -> {
                    List<Document> documents = new ArrayList<>();
                    out.print("\nEnter how many different jsons you want to send : ");
                    final int numberOfDocuments = push.nextInt();
                    for (int i = 0; i < numberOfDocuments; i++) {
                        documents.add(insertData());
                    }
                    queryPanel.insertMany(documents);

                }

                case 'd' -> {
                    try {
                        out.print("Enter collection name : ");
                        queryPanel.setCollectionName(push.next().trim());  //nome default della collezione
                    } catch (NullPointerException e) {
                        out.println(e.getMessage());
                    }
                }
                default -> out.println("invalid choice,try again" + System.lineSeparator());


            }
        }
    }

    private static Document insertData() {    //evita ripetizione per gli input
        map = new HashMap<>();
        out.print("Enter how many parameters you want to send as json input: ");
        int numberOfParameters = push.nextInt();
        for (int i = 0; i < numberOfParameters; i++) {
            out.print((i + 1) + "° KEY : ");
            String key = push.next();
            out.print("VALUE : ");
            String value = push.next();
            map.put(key, value);
        }
        return new Document(map);

    }
}
