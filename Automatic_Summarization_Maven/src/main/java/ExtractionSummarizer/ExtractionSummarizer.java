/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ExtractionSummarizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

public class ExtractionSummarizer {
    
    private int TRESHOLD = 1; // CHANGE ME 
    
    public ExtractionSummarizer(ArrayList<String> sentences) {
        
        Hashtable<String, Double> idf = new Hashtable<String, Double>(); // get from word annoter
        
        // Calculer tf*idf pour chaque mot
        Hashtable<String, Double> tfidf = new Hashtable<String, Double>();
        for(String sentence : sentences)
        {
            String[] words = sentence.split(" "); // TODO: remove all ponctuation and capital letter, and get a String[] of the words
            for(String word : words)
            {
                if(tfidf.containsKey(word))
                    tfidf.put(word, tfidf.get(word) + idf.get(word));
                else
                    tfidf.put(word, idf.get(word));
            }
        }
        
        // Construire le centroid du documents en choisisant les mots ayant les plus hauts scores
        Hashtable<String, Double> centroid = new Hashtable<String, Double>();
        for(String word :  tfidf.keySet())
        {
            if(tfidf.get(word) > TRESHOLD)
                centroid.put(word, tfidf.get(word));
        }
        
        
        // Calculer les scores des phrases
        ArrayList<Double> sentencesScore = new ArrayList<Double>();
        for(int i = 0; i < sentences.size() ; i++)
        {
            sentencesScore.add(i, 0.0);
            String sentence = sentences.get(i);
            String[] words = sentence.split(" "); // TODO: remove all ponctuation and capital letter, and get a String[] of the words
            for(String word : words)
            {
                if(centroid.containsKey(word))
                    sentencesScore.add(i, sentencesScore.get(i) + centroid.get(word));
            }
        }
        
        // return sentences with bigger score
        
    }
    
    public File summarizeText(ArrayList<String> sentences) {
        return new File ("");
    }
}
