package org.projects;

public class Prodotto extends DBObject {
    private String descrizione;
    private String linkImmagine;

    //costruttore del Prodotto
    public Prodotto(String nome, double prezzo, String tipoOggetto, String descrizione, String linkImmagine) {
        //richiamiamo il metodo della classe che estendiamo con il "super"
        super(nome, prezzo, tipoOggetto);
        this.descrizione = descrizione;
        this.linkImmagine = linkImmagine;
    }

    //resituisce la descrizione del prodotto
    public String getDescrizione() {
        return descrizione;
    }

    //restituisce l'immagine associata al prodotto
    String getLinkImmagine(){
        return linkImmagine;
    }
}