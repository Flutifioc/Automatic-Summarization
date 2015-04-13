package Executables;

import AbstractionSummarizer.AbstractionSummarizer;
import ExtractionSummarizer.ExtractionSummarizer;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class Summarizer {
    private static String text ="";

    public static void main(String[] args) throws UnirestException {

        String toto = detectAcronyms("The U.S.S.R. are Russians. The U.S. are Americans.");
        
        ArrayList<File> documents = new ArrayList<File>();
        File abstractDestFile = new File(args[0] + "results.txt"); // texte.txt

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

			Extraction(sentences);
			
        /* Version 1 (deux méthodes séparément) */
        // Création des instances des summarizers
            //ExtractionSummarizer extractSummarizer = new ExtractionSummarizer(annotedWords);
            AbstractionSummarizer abstractSummarizer = new AbstractionSummarizer(sentences, abstractDestFile);

        // Etape 2 : appeler les deux méthodes sur cette entrée, et récupérer leurs sorties
            //File extractedSummary = extractSummarizer.summarizeText(sentences);
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
	
	private static Hashtable<String, Double> getIDFScores() throws FileNotFoundException, IOException
    {
        Hashtable<String, Double> idf = new Hashtable<String, Double>();
        
        File file = new File("C:\\Users\\Lara\\Desktop\\INF8225_Projet\\INF8225_Projet\\idf.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null)
        {
            String[] content = line.split(" ");
            if(content.length == 2)
                idf.put(content[0], Double.parseDouble(content[1]));
        }
        
        return idf;
    }
        
    private static void Extraction(List<String> sentences) throws IOException
    {
        //WordAnnoter wordAnnoter = new WordAnnoter();
        //wordAnnoter.calculateIdf();

        Hashtable<String, Double> idf = getIDFScores();

        ExtractionSummarizer extractSummarizer = new ExtractionSummarizer(sentences, idf);

        String bestSentences = extractSummarizer.calculateCentroid(sentences, idf);
        String bestSentences1 = extractSummarizer.calculateDegreeCentrality(sentences, idf, new int[sentences.size()][sentences.size()], new int[sentences.size()]);
        String bestSentences2 = extractSummarizer.calculateLexRank(sentences, idf);
        String summaries = "Texte\n" + text + "\nCentroid\n" + bestSentences + "\nCentrality\n" + bestSentences1 + "\nLexRank\n" + bestSentences2;    

        WriteSummaryInFile(summaries);
        }

    private static void WriteSummaryInFile(String summary)
    {
       try {
            BufferedWriter out = new BufferedWriter(new FileWriter("bestSentences.txt"));
            out.write(summary);
            out.close();
        } catch (IOException e) {} 
    }
	
    public static void splitFile(File document, List<String> sentencesList) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(document));
        String line = reader.readLine();
        String content = "";
        while (line != null) {
            text = text + line + "\n";
            content = content + line;
            line = reader.readLine();
        }
        content = detectAcronyms(content);
        content = content.replaceAll("[\\r\\n]+", " ");
        String[] sentences = content.split("\\n|[.](?<!\\\\d)(?!\\\\d)");//"[\\n\\r]|[^\\.].\\. |\\?|!|\"");
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
