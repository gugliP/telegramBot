package org.projects;


public class Main {

    public static void main(String[] args) {
        //Creazione ed avvio del bot
        Bot b = new Bot();
        //Creazione del thread per il messaggio programmato
        Thread botThread= new Thread(b);
        //Attivazione del thread
        botThread.start();
    }

}