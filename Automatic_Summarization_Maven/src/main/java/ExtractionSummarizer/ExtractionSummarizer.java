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
        
    }
    
    private void calculateCentroid()
    {
        
    }
    
    private void calculateModifCosine(String s1, String s2)
    {
        // calcul du denominateur
        
        // den1 = 0
        // for each word in s1
            // if word not in tfs1
            // tfs1[word] = the number of occurence in s1  
            // den1 += (tfs1[word] * idf[word])^2

        // den2 = 0
        // for each word in s2
            // if word not in tfs2
            // tfs2[word] = the number of occurence in s2  
            // den2 += (tfs2[word] * idf[word])^2
        
        // den = den1 * den2
        
        // idfmodcos = 0        
        // for each word in s1
            // if the word is not yet treated
                // idfmodcos = (tfs1[word] * tfs2[word] * idf[word]^2)
        
        // idfmodcos /= den
        
    }
    
    private void calculateDegreeCentrality()
    {
        // for each sentence
            // for each sentence after if
                // idfmodcos = calculateModifCosine(s1,s2)
                // if idfmodcos > THRESHOLD
                    // idsmodcos[s1][s2] = 1
                    // idsmodcos[s2][s1] = 1
                    // degree[s1]++
                    // degree[s2]++
                // else
                    // idsmodcos[s1][s2] = 0
                    // idsmodcos[s2][s1] = 0
    }
    
    private void calculateLexRank()
    {
        //for each sentence s1
            // for each sentence s2
                // lexrank[s1][s2] = idsmodcos[s1][s2] / degree[s1]
        
        // lexrankScores = powerMethod(lexrank, nbSentences, error) 
    }
    
    private void powerMethod()
    {
        // p[0] = 1/nbSentnces
        // t = 0

        // do
            // t = t+1
            // p[t] = transposed(lexrankMatrix)*p[t-1]
            // d = norm(p[t] - p[t-1])
        // while d<error
        
        // return p[t]
        
    }
    
    public File summarizeText(ArrayList<String> sentences) {
        return new File ("");
    }
}
