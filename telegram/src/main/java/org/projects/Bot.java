package org.projects;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.w3c.dom.Element;


import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;


import static org.projects.LinkMDB.getFormattedLinksString;


public class Bot extends TelegramBot implements  Runnable{
    private TimerManager timer;
    private ProdottoMDB prodottoMDB;
    private LinkMDB linkMDB;
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

    //comando di start per inviare il messaggio di benvenuto
    private static final String START_COMMAND = "/start";
    private static final String FUNCTIONS_MESSAGE = getFormattedLinksString();


    //Inizializzazione del bot
    public Bot() {
        // Token del bot Telegram
        super("INSERIRE TOKEN");
        // Connessione al database da parte del bot
        this.prodottoMDB = new ProdottoMDB(uri, dbName);
        TimerManager timer = new TimerManager();
        // Ritardo del timer in millisecondi
        Long timerDelay = 15000L;
        //estrazione del chatId random a cui inviare l'offerta
        Long randomChatId = timer.timerId(timerDelay);
        //invia il prodotto all'id estratto
        sendProdottoMessage(randomChatId);
        // Ricezione dei messaggi da parte del bot e risposta al messaggio di inizializzazione
        this.setUpdatesListener(updates -> {
            for (Update update : updates) {
                Message message = update.message();
                if (message != null && message.text() != null && message.text().equals(START_COMMAND)) {
                    // Invio del messaggio di benvenuto
                    sendFunctionsMessage(message.chat().id());
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
        // Pianifica l'invio periodico dei prodotti utilizzando il timer
        timer.scheduleTimer(new TimerTask() {
            public void run() {
                Long randomChatId = timer.timerId(timerDelay);
                sendProdottoMessage(randomChatId);
            }
        }, timerDelay);
    }


    //metodo per inviare il messaggio di inizio del bot
    private void sendFunctionsMessage(long chatId) {
        //riceve l'id della chat a cui inviare la risposta
        SendMessage request = new SendMessage(chatId, FUNCTIONS_MESSAGE);
        SendResponse response = execute(request);
        //messaggio su board che ci dice se è andato tutto bene o meno
        System.out.println("Messaggio inviato? " + response.isOk());
    }


    //metodo che invia il prodotto in un canale
    private void sendProdottoMessage(long chatId) {
        //estrae un prodotto casuale dal database
        Prodotto prodottoCasuale = prodottoMDB.getProdottoCasuale();
        //controlla che abbia estratto un prodotto in caso contrario invia un messaggio di errore
        if (prodottoCasuale != null) {
            //imposta le 2 cifre dopo la virgola per il prezzo scontato
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            //creazione del prodotto da inviare sul canale
            //aggiunto un piccolo carattere che nasconde il link dell'immagine
            String messageText = "<a href='"+prodottoCasuale.getLinkImmagine()+"'>\u200E</a>\u2705 Nome: " + prodottoCasuale.getNome() + "\n"
                    + "\uD83D\uDCB0 Prezzo: " + prodottoCasuale.getPrezzo() + "€\n"
                    + "\uD83D\uDCA5 Prezzo Scontato:"+ decimalFormat.format(prodottoCasuale.getPrezzo()-(prodottoCasuale.getPrezzo()*0.80))+"€\n"
                    + "\uD83C\uDF1F Categoria: " + prodottoCasuale.getTipoOggetto() + "\n"
                    + "\uD83D\uDCDD Descrizione: " + prodottoCasuale.getDescrizione() +"\n"
                    + "\n";
            //invio del prodotto sul canale passato al metodo
            SendMessage request = new SendMessage(chatId, messageText);
            request.parseMode(ParseMode.HTML);
            SendResponse response = execute(request);
            //solito messaggio che ci dice se è andato tutto bene
            System.out.println("Messaggio inviato? " + response.isOk());
            //in caso di database vuoto resituisce il messaggio
        } else {
            SendMessage request = new SendMessage(chatId, "Nessun prodotto presente in questo momento.");
            SendResponse response = execute(request);
            System.out.println("Messaggio inviato? " + response.isOk());
        }
    }


    //PARTE RELATIVA AL THREAD
    //Modificare l'orario presente all'interno della variabile targetTime per scegliere
    //quando inviare il messaggio temporizzato


    @Override
    public void run() {
        this.linkMDB = new LinkMDB();
        // Recuperiamo un id qualunque
        Long randomChatId1 = linkMDB.getRandomChatId();
        // Ottieni l'orario attuale
        LocalTime currentTime = LocalTime.now();
        // Imposta l'orario a cui inviare il messaggio
        LocalTime targetTime = LocalTime.of(18, 31);
        // Calcola l'intervallo di tempo fino all'orario prestabilito
        long initialDelayMillis = currentTime.until(targetTime, ChronoUnit.MILLIS);
        // Si assicura che il ritardo sia positivo
        if (initialDelayMillis < 0) {
            // Se l'intervallo è negativo, aggiungi 24 ore in millisecondi
            initialDelayMillis += TimeUnit.HOURS.toMillis(24);
        }
        // Pianifica l'invio del messaggio
        scheduleMessageAt12(randomChatId1, initialDelayMillis);
    }

    //metodo che fa inviare al bot il messaggio programmato
    private void scheduleMessageAt12(long chatId, long initialDelayMillis) {
        // Crea un TimerTask per l'invio del messaggio
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // Utilizza il metodo sendProdottoMessage per inviare il messaggio
                sendProdottoMessage(chatId);
            }
        };
        // Pianifica l'esecuzione del TimerTask
        new Timer().schedule(task, initialDelayMillis);
    }
}