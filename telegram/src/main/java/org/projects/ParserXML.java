package org.projects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


public class ParserXML {
    private DocumentBuilderFactory dbf;
    private File file;

    public ParserXML(String fileName) {
        this.dbf = DocumentBuilderFactory.newInstance();
        this.file = new File(fileName);
    }

    public Element getDOMParsedDocumentRoot() {
        DocumentBuilder db;
        Document document;
        Element root=null;
        try{
            db = this.dbf.newDocumentBuilder();
            document = db.parse(file);
            root = document.getDocumentElement();
        //gestiamo le varie eccezioni che potrebbero risultare per il parser
        } catch (ParserConfigurationException | SAXException | IOException ex){
            ex.printStackTrace();
            Logger.getLogger(ParserXML.class.getName()).log(Level.SEVERE, null, ex);

        }
        return root;
    }
}
