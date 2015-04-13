package Executables;

import AbstractionSummarizer.AbstractionSummarizer;
import ExtractionSummarizer.ExtractionSummarizer;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class Summarizer {

    public static void main(String[] args) throws UnirestException {

        String toto = detectAcronyms("The U.S.S.R. are Russians. The U.S. are Americans.");
        
        ArrayList<File> documents = new ArrayList<File>();
        File abstractDestFile = new File(args[0] + "results.txt");

        // Etape 1 : parsing du fichier de config
        try {
            File configFile = new File(args[0]);
            BufferedReader configReader = new BufferedReader(new FileReader(configFile));
            String line;

            while ((line = configReader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                String[] elements = line.split("=");
                if (elements.length < 2) {
                    System.err.println("Received invalid line while parsing the config file :");
                    System.err.println(line);
                    continue;
                }
                if (elements[0].equalsIgnoreCase("file")) {
                    File newFile = new File(elements[1]);
                    if (!newFile.exists()) {
                        System.err.println("Received non-existant file in config : " + elements[1]);
                    } else {
                        documents.add(newFile);
                    }
                } else if (elements[0].equalsIgnoreCase("abstractdestfile")) {
                    abstractDestFile = new File(elements[1]);
                    if (abstractDestFile.exists()) {
                        abstractDestFile.delete();
                    }
                    abstractDestFile.createNewFile();
                }
                
            }
            // Etape 1 : lire le fichier texte les documents à résumer

            // Etape 1 bis : si besoin, le découper en phrases (si étape commune aux deux algorithmes)
            ArrayList<String> sentences = new ArrayList<String>();
            for (File document : documents) {
                if (!document.exists()) {
                    System.err.println("File " + document.getCanonicalPath() + " does not exist.");
                    continue;
                }
                splitFile(document, sentences);
            }

        /* Version 1 (deux méthodes séparément) */
        // Création des instances des summarizers
            // ExtractionSummarizer extractSummarizer = new ExtractionSummarizer(annotedWords);
            AbstractionSummarizer abstractSummarizer = new AbstractionSummarizer(sentences, abstractDestFile);

        // Etape 2 : appeler les deux méthodes sur cette entrée, et récupérer leurs sorties
            // File extractedSummary = extractSummarizer.summarizeText(sentences);
            File abstractedSummary = abstractSummarizer.summarizeText(sentences);

        } catch (IOException ex) {
            Logger.getLogger(Summarizer.class.getName()).log(Level.SEVERE, null, ex);
        }

            // Etape 3 : appel à ROUGE pour évaluer les sorties
        // TODO
        /* Version 2 (deux méthodes à la suite) */
        // Etape 2 : appeler la méthode par extraction, récupérer la sortie
        // Etape 2 bis : la stocker dans un fichier (optionnel)
        // Etape 3 : appeler la méthode par abstraction, avec pour entrée le résultat de la précédente
        // Etape 4 : mettre le résultat dans un fichier texte
        // Etape 5 : appel à ROUGE pour évaluer la sortie
    }

    public static void splitFile(File document, List<String> sentencesList) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(document));
        String line = reader.readLine();
        String content = line;
        while (line != null) {
            content = content + line;
            line = reader.readLine();
        }
        content = detectAcronyms(content);
        content = content.replaceAll("[\\r\\n]+", " ");
        String[] sentences = content.split("[\\n\\r]|[^\\.].\\. |\\?|!");
        for (String sentence : sentences ){
            if (!sentence.equals("")) {
                sentence = sentence.replaceAll("\\.?##+", ".");
                sentencesList.add(sentence);
            }
        }
        reader.close();
    }
    
    public static String detectAcronyms(String input) {
        String[] elements = input.split("\\.");
        String output = "";
        for (String element : elements) {
            if (element.length() == 1) {
                output = output + "##" + element + "##";
            } else {
                output = output + element + ".";
            }
        }
        return output;
    }
}
