package org.projects;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LinkMDB {
    //parser per leggere la configurazione del database
    ParserXML parser = new ParserXML("config_db.xml");
    //il parser prende la root
    Element root = parser.getDOMParsedDocumentRoot();
    //figlio db
    Element db = (Element) root.getElementsByTagName("db").item(0);
    //uri del database
    String uri = db.getElementsByTagName("uri").item(0).getTextContent();
    //nome del database
    String dbName = db.getElementsByTagName("dbname").item(0).getTextContent();
    private MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;


    //costruttore del Link
    public LinkMDB() {
        try {
            // Crea una connessione al database MongoDB e nello specifico alla collezione prova
            mongoClient = MongoClients.create(uri);
            database = mongoClient.getDatabase(dbName);
            collection = database.getCollection("prova");
        //Gestiamo l'eccezzione nel caso in cui non riusciamo a connetterci con il database
        } catch(Exception e){
            System.err.println("Si è verificato un errore durante la connessione al database: " + e.getMessage());
        }
    }

    //metodo che restituisce la lista dei link estratti dal database
    public List<String> getFormattedLinks() {
        List<String> formattedLinks = new ArrayList<>();
        try {
            //Esegui una query per ottenere i link
            Document query = new Document();
            List<Document> results = collection.find(query).into(new ArrayList<>());
            // Itera sui risultati e formatta i link
            for (Document result : results) {
                String channelLink = result.getString("Link");
                String channelName = result.getString("Nome");

                String formattedLink = "\uD83D\uDD17 [" + channelName + "] \u27A1\uFE0F " + channelLink + "";
                formattedLinks.add(formattedLink);
            }
        } catch(Exception e){
            System.err.println("Si è verificato un errore durante l'ottenimento degli ID: " + e.getMessage());
        }
        return formattedLinks;
    }

    public void closeConnection() {
        // Chiudi la connessione al database
        mongoClient.close();
    }

    //metodo che viene usato nel messaggio di risposta allo start
    public static String getFormattedLinksString() {
        LinkMDB linkMDB = new LinkMDB();
        List<String> formattedLinks = linkMDB.getFormattedLinks();
        linkMDB.closeConnection();
        StringBuilder sb = new StringBuilder();
        sb.append("👋 WELCOME 👋\n\n"+"Scegli il canale per le tue esigenze:\n");
        for (String link : formattedLinks) {
            sb.append(link).append("\n");
        }
        return sb.toString();
    }

    //estrae gli Id dei gruppo telegram dal database
    public static List<Long> getFormattedID() {
        List<Long> formattedIds = new ArrayList<>();
        try {
            // Esegui una query per ottenere gli Id
            Document query = new Document();
            collection = database.getCollection("chatid");
            List<Document> results = collection.find(query).into(new ArrayList<>());
            // Itera sui risultati e formatta gli Id
            for (Document result : results) {
                Long channelId = result.getLong("chatId");  // Modifica qui gli Id estratti
                formattedIds.add(channelId);
            }
        } catch(Exception e){
            System.err.println("Si è verificato un errore durante l'ottenimento degli ID: " + e.getMessage());
        }
        //restiuisce la lista degli Id formattati
        return formattedIds;
    }


    //Estrae dalla lista degli Id un id random e lo restituisce
    public Long getRandomChatId() {
        // Crea la lista degli Id
        List<Long> formattedIds = getFormattedID();
        // Controlla che la lista non sia vuota
        if (!formattedIds.isEmpty()) {
            try {
                // Estrae un Id casuale
                Random random = new Random();
                int randomIndex = random.nextInt(formattedIds.size());
            // Restituisce l'ID estratto
            return formattedIds.get(randomIndex);
            } catch(Exception e){
                System.err.println("Si è verificato un errore durante l'ottenimento dell'ID casuale: " + e.getMessage());
            }
        }
        return null;
    }
}