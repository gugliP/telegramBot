package org.projects;

//classe astratta per i prodotti presenti nel database
public abstract class DBObject {
    String nome;
    double prezzo;
    String tipoOggetto;

    //costruttore generale
    public DBObject(String nome, double prezzo, String tipoOggetto) {
        this.nome = nome;
        this.prezzo = prezzo;
        this.tipoOggetto = tipoOggetto;
    }

    //restituisce il nome del prodotto
    public String getNome(){
        return nome;
    }

    //restituisce il prezzo del prodotto
    public double getPrezzo() {
        return prezzo;
    }

    //restituisce la categoria del prodotto
    public String getTipoOggetto() {
        return tipoOggetto;
    }

}
