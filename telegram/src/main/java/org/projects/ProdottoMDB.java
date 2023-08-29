package org.projects;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProdottoMDB {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    //costruttore del prodotto estratto dal database MongoDB
    public ProdottoMDB(String connectionString, String databaseName) {
        try {
            mongoClient = MongoClients.create(connectionString);
            database = mongoClient.getDatabase(databaseName);
            collection = database.getCollection("Prodotti");
        //Gestiamo il caso in cui non riusciamo a stabilire la connessione con il database
        } catch(Exception e){
            System.err.println("Si è verificato un errore durante la creazione della connessione al database: " + e.getMessage());
        }
    }

    //metodo per estrarre la lista dei prodotti dal database
    public List<Prodotto> getProdotti() {
        List<Prodotto> prodotti = new ArrayList<>();
        try {
            Document query = new Document();
            //query per la connessione ad una collezione specifica
            List<Document> results = collection.find(query).into(new ArrayList<>());
            for (Document result : results) {
                //vari metodi per estrarre le informazioni con la query
                String nome = result.getString("Nome");
                double prezzo = result.getDouble("Prezzo");
                String tipoOggetto = result.getString("Categoria");
                String descrizione = result.getString("Descrizione");
                String linkImmagine = result.getString("linkImmagine");
                //creazione dell'oggetto "prodotto"
                Prodotto prodotto = new Prodotto(nome, prezzo, tipoOggetto, descrizione, linkImmagine);
                //aggiunta del prodotto all'arraylist
                prodotti.add(prodotto);
            }
        } catch(Exception e){
            System.err.println("Si è verificato un errore durante l'ottenimento dei prodotti: " + e.getMessage());
        }
        //ritorno della lista dei prodotti
        return prodotti;
    }

    //metodo per l'estrazione casuale di un prodotto dalla lista
    public Prodotto getProdottoCasuale() {
        //creiamo la lista prdotti che vengono estratti dal metodo getProdotti
        List<Prodotto> prodotti = getProdotti();
        try {
            //controllo lista vuota
            if (!prodotti.isEmpty()) {
                //estrazione di un prodotto random dalla lista
                Random random = new Random();
                int randomIndex = random.nextInt(prodotti.size());
                //ritorno del prodotto
                return prodotti.get(randomIndex);
            }
        } catch(Exception e){
            System.err.println("Si è verificato un errore durante l'ottenimento del prodotto: " + e.getMessage());
        }
        return null;
    }

    //metodo per chiudere la connessione (non usato)
    public void closeConnection() {
        mongoClient.close();
    }
}