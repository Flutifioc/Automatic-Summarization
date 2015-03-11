package com.mycompany.automatic_summarization_maven;

import AbstractionSummarizer.AbstractionSummarizer;
import ExtractionSummarizer.ExtractionSummarizer;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.File;
import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class Summarizer 
{

    public static void main(String[] args) throws UnirestException {

	// Etape 1 : lire le fichier texte les documents à résumer
        File fileToRead = new File(args[0]);
        File annotedWords = new File(args[1]);

	// Etape 1 bis : si besoin, le découper en phrases (si étape commune aux deux algorithmes)
        ArrayList<String> sentences = splitFile(fileToRead);
        
        /* Version 1 (deux méthodes séparément) */
        
        // Création des instances des summarizers
        ExtractionSummarizer extractSummarizer = new ExtractionSummarizer(annotedWords);
        AbstractionSummarizer abstractSummarizer = new AbstractionSummarizer();
        
	// Etape 2 : appeler les deux méthodes sur cette entrée, et récupérer leurs sorties
        File extractedSummary = extractSummarizer.summarizeText(sentences);
        File abstractedSummary = abstractSummarizer.summarizeText(sentences);
        
        // Etape 3 : appel à ROUGE pour évaluer les sorties
        // TODO
        
        /* Version 2 (deux méthodes à la suite) */
            // Etape 2 : appeler la méthode par extraction, récupérer la sortie
        // Etape 2 bis : la stocker dans un fichier (optionnel)
        // Etape 3 : appeler la méthode par abstraction, avec pour entrée le résultat de la précédente
        // Etape 4 : mettre le résultat dans un fichier texte
            // Etape 5 : appel à ROUGE pour évaluer la sortie
    }

    public static ArrayList<String> splitFile(File file) {
        return new ArrayList<String>();
    }
}