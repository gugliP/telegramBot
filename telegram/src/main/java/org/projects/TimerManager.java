package org.projects;

import java.util.Timer;
import java.util.TimerTask;

public class TimerManager {
    private LinkMDB linkMDB;
    private ProdottoMDB prodottoMDB;
    private Timer timer;

    //costruttore generale del timer
    public TimerManager() {
        prodottoMDB = new ProdottoMDB("mongodb://localhost:27017", "prova");
        timer = new Timer();
    }

    //metodo che ritorna il chatId dopo il delay
    public Long timerId(long delay){
        LinkMDB linkMDB = new LinkMDB();
        try {
            //estrazione random del chatUd
            Long randomChatId = linkMDB.getRandomChatId();
            timer.schedule(new TimerTask() {
                public void run() {
                }
            }, delay);
        return randomChatId;
        //Gestiamo l'eccezzione nel caso in cui non riusciamo a collegarci con il database
        } catch(Exception e){
            System.err.println("Errore durante l'ottenimento del chatId: " + e.getMessage());
            return null;
        }
    }

    //metodo che pianifica il l'esecuzione della task
    // dopo il delay passato, viene ripetuto ad intervalli regolari
    public void scheduleTimer(TimerTask task, long delay) {
        timer.schedule(task, delay, delay);
    }

}
