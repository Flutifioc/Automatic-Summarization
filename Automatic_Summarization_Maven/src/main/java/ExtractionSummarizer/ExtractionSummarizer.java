package ExtractionSummarizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class ExtractionSummarizer {
    
    private int TRESHOLD = 1; // CHANGE ME 
    
    public ExtractionSummarizer(ArrayList<String> sentences, Hashtable<String, Double> idf) {
        Double[] lexrankScores = calculateLexRank(sentences, idf);
        summarizeText(sentences, lexrankScores);
    }
    
    private Double[] calculateLexRank(ArrayList<String> sentences, Hashtable<String, Double> idf)
    {        
        final int nbSentences = sentences.size();
        int[][] idsmodcos = new int[nbSentences][nbSentences];
        int[] degree = new int[nbSentences];
        
        calculateDegreeCentrality(sentences, idf, idsmodcos, degree);
        
        Double[][] lexrank = new Double[nbSentences][nbSentences];
        for(int i = 0 ; i < sentences.size(); i++)
        {
            for(int j = 0 ; j < sentences.size(); j++)
            {
                lexrank[i][j] = idsmodcos[i][j] / (degree[i]*1.0);
            }
        }
            
        Double error = 0.0;
        Double[] lexrankScores = powerMethod(lexrank, nbSentences, error);
        return lexrankScores;
    }
    
    private void calculateDegreeCentrality(ArrayList<String> sentences, Hashtable<String, Double> idf, int[][] idsmodcos, int[] degree)
    {
        int THRESHOLD = 0;
        
        for(int i = 0 ; i < sentences.size(); i++)
        {
            for(int j = i ; j < sentences.size(); j++)
            {
                Double idfmodcos = calculateModifCosine(sentences.get(i),sentences.get(j), idf);
                
                if(idfmodcos > THRESHOLD)
                {
                    idsmodcos[i][j] = 1;
                    idsmodcos[j][i] = 1;
                    degree[i]++;
                    degree[j]++;
                }
                else
                {
                    idsmodcos[i][j] = 0;
                    idsmodcos[j][i] = 0;
                }
            }
        }
    }
    
     private Double calculateModifCosine(String s1, String s2, Hashtable<String, Double> idf)
    {
        Hashtable<String, Double> tfs1 = calculateFrequenceOfWord(s1, idf);
        Hashtable<String, Double> tfs2 = calculateFrequenceOfWord(s2, idf);
        
        // calcul du denominateur
        Double den = calculateModifCosineDen(s1,tfs1) * calculateModifCosineDen(s2,tfs2);        
        
        Double idfmodcos = 0.0;
        String concatWord = s1 + " " + s2;
        String[] words = concatWord.split(" ");
        
        Set<String> treatedWords = new HashSet<String>();
        for(String word : words)
        {
            if(!treatedWords.contains(word)){ // TODO : REMOVE CAPITAL LETTTERS
                idfmodcos += tfs1.get(word) * tfs2.get(word) * Math.pow(idf.get(word),2);
                treatedWords.add(word);
            }
        }
        
        idfmodcos /= den;
                
        return idfmodcos;      
    }
     
    private Hashtable<String, Double> calculateFrequenceOfWord(String sentence, Hashtable<String, Double> idf)
    {
        Hashtable<String, Double> tf = new Hashtable<String, Double>();
        
        String[] words = sentence.split(" ");
        for(String word : words)
        {
            if(tf.containsKey(word)) // TODO : REMOVE CAPITAL LETTTERS
                    tf.put(word, tf.get(word) + idf.get(word));
                else
                    tf.put(word, idf.get(word));
        }
        
        return tf;
    }
    
    private Double calculateModifCosineDen(String s, Hashtable<String, Double> tf)
    {
        Double den = 0.0;
        for(String word : tf.keySet())
        {
            den += Math.pow(tf.get(word), 2);
        }
        return den;
    }
    
    private Double[] powerMethod(Double[][] lexrank, int nbSentences, Double error)
    {
        Double[][] p = new Double[nbSentences][nbSentences];
        
        Arrays.fill(p[0], 1.0/nbSentences);
        int t = 0;
        Double delta = 0.0;
        
        do
        {
            t = t+1;
            p[t] = multiply(transpose(lexrank, nbSentences), p[t-1]);
            delta = determinant(minus(p[t], p[t-1]));
        } while(delta < error);
        
        return p[t];
    }
    
    
    private File summarizeText(ArrayList<String> sentences, Double[] scores) {
        return new File ("");
    }
    
    
    private void calculateCentroid(ArrayList<String> sentences, Hashtable<String, Double> idf)
    {
        // Calculer tf*idf pour chaque mot
        Hashtable<String, Double> tfidf = new Hashtable<String, Double>();
        for(String sentence : sentences)
        {
            String[] words = sentence.split(" "); 
            for(String word : words)
            {
                if(tfidf.containsKey(word)) // TODO : REMOVE CAPITAL LETTTERS
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
        final int nbSentences = sentences.size();
        Double[] sentencesScore = new Double[nbSentences];
        for(int i = 0; i < sentences.size() ; i++)
        {
            sentencesScore[i] = 0.0;
            String sentence = sentences.get(i);
            String[] words = sentence.split(" "); // TODO: remove all ponctuation and capital letter, and get a String[] of the words
            for(String word : words)
            {
                if(centroid.containsKey(word))
                    sentencesScore[i] = sentencesScore[i] + centroid.get(word);
            }
        }
    }
     
    private static Double determinant(Double[] m){
        return 0.0;
    }
    
    /*
    public static double determinant(double[][] m) {
        int n = m.length;
        if (n == 1) {
            return m[0][0];
        } else {
            double det = 0;
            for (int j = 0; j < n; j++) {
                det += Math.pow(-1, j) * m[0][j] * determinant(minor(m, 0, j));
            }
            return det;
        }
    }
    
    private static double[][] minor(final double[][] m, final int i, final int j) {
        int n = m.length;
        double[][] minor = new double[n-1][n-1];
        // index for minor matrix position:
        int r = 0, s = 0;
        for (int k = 0; k < n; k++) {
            double[] row = m[k];
            if (k != i) {
                for (int l = 0; l < row.length; l++) {
                    if (l != j) {
                        minor[r][s++] = row[l];
                    }
                }
                r++;
                s = 0;
            }
        }
        return minor;
    }
    */
    
    private Double[] minus(Double[] matrice1, Double[] matrice2)
    {
        Double[] matriceResult = new Double[matrice1.length];
        
        for(int i = 0 ; i < matrice1.length ; i++)
        {
            matriceResult[i] = matrice1[i] - matrice2[i]; 
        }
        
        return matriceResult;
    }
    
    private Double[][] transpose(Double[][] matrice, int n) {
        Double tmp;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tmp = matrice[i][j];
                matrice[i][j] = matrice[j][i];
                matrice[j][i] = tmp;
            }
        }
        return matrice;
    }
    
    private static Double[] multiply(Double[][] matrice1, Double[] matrice2) {
       int rowsIn1 = matrice1.length;
       int columnsIn1 = matrice1[0].length; // same as rows in B
       Double[] matriceResult = new Double[rowsIn1];
       for (int i = 0; i < rowsIn1; i++) {
            for (int k = 0; k < columnsIn1; k++) {
                matriceResult[i] = matriceResult[i] + matrice1[i][k] * matrice2[k];
           }
       }
       return matriceResult;
   }

}
