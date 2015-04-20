package ExtractionSummarizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class ExtractionSummarizer {
    
    private double TRESHOLD = 0.1;
    private static double pourcentage = 20.0/100.0;
    
    public ExtractionSummarizer(List<String> sentences, Hashtable<String, Double> idf) {
        //Double[] lexrankScores = calculateLexRank(sentences, idf);
        //summarizeText(sentences, lexrankScores);
    }    
    
    /************************************** Methode LexRank **********************************************/
    
    public String calculateLexRank(List<String> sentences, Hashtable<String, Double> idf)
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
            
        Double error = 0.0001;
        Double[] lexrankScores = powerMethod(lexrank, nbSentences, error);
        return getBestSentencesLexRank(sentences, lexrankScores);
    }
    
    private String getBestSentencesLexRank(List<String> sentences, Double[] lexrankScores)
    {
        Double minScore = getNthBiggestValue(lexrankScores, (int)(lexrankScores.length * pourcentage));
        
        // search for best sentences
        String bestSentences = "";
        for(int i = 0; i < sentences.size() ; i++)
        {
            if(lexrankScores[i] >= minScore) 
            {
                bestSentences += sentences.get(i);
                bestSentences += ". \n";
            }
        }
        return bestSentences;
    }
    
    /************************************** Methode des Centralite **********************************************/
    
    public String calculateDegreeCentrality(List<String> sentences, Hashtable<String, Double> idf, int[][] idsmodcos, int[] degree)
    {        
        for(int i = 0 ; i < sentences.size(); i++)
        {
            for(int j = i ; j < sentences.size(); j++)
            {
                Double idfmodcos = calculateModifCosine(sentences.get(i),sentences.get(j), idf);
                
                if(idfmodcos > TRESHOLD)
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
        return getBestSentencesCentrality(sentences, degree);       
    }
    
    private String getBestSentencesCentrality(List<String> sentences, int[] degree)
    {
        int minScore = getNthBiggestValue(degree, (int)(degree.length * pourcentage));
        
        // search for best sentences
        String bestSentences = "";
        for(int i = 0; i < sentences.size() ; i++)
        {
            if(degree[i] >= minScore) 
            {
                bestSentences += sentences.get(i);
                bestSentences += ". \n";
            }
        }
        return bestSentences;
    }
    
    private Double calculateModifCosine(String s1, String s2, Hashtable<String, Double> idf)
    {
        Hashtable<String, Double> tfs1 = calculateFrequenceOfWord(s1, idf);
        Hashtable<String, Double> tfs2 = calculateFrequenceOfWord(s2, idf);
                 
        // calcul du denominateur
        Double den = calculateModifCosineDen(s1,tfs1) * calculateModifCosineDen(s2,tfs2);        
        
        Double idfmodcos = 0.0;
        String concatSentences = s1 + " " + s2;
        String[] words = concatSentences.split("[^a-zA-Z]");
        
        Set<String> treatedWords = new HashSet<String>();
        for(String word : words)
        {
            if (word.matches(".*\\w.*"))
            {
                word = word.toLowerCase();
                if(!treatedWords.contains(word))
                {     
                    Double tf1 = 0.0;
                    Double tf2 = 0.0;
                    Double scoreIdf = 0.0;
                                                         
                    if(tfs1.containsKey(word))
                    { 
                        tf1 = tfs1.get(word);
                    }
                    
                    if(tfs2.containsKey(word))
                    {
                        tf2 = tfs2.get(word);     
                    }
                    
                    if(idf.containsKey(word))
                    {
                        scoreIdf = getWordScore(word, idf);
                    }

                    Double wordScore = tf1*tf2*Math.pow(scoreIdf,2);
                    idfmodcos += wordScore;
                    treatedWords.add(word);
                }
            }
        }
        
        idfmodcos /= den;
                
        return idfmodcos;      
    }
     
    private Hashtable<String, Double> calculateFrequenceOfWord(String sentence, Hashtable<String, Double> idf)
    {
        Hashtable<String, Double> tf = new Hashtable<String, Double>();
        
        String[] words = sentence.split("[^a-zA-Z]");
        for(String word : words)
        {
            if (word.matches(".*\\w.*"))
            {
                word = word.toLowerCase();
                Double wordScore = getWordScore(word, idf);

                if(tf.containsKey(word))
                    tf.put(word, tf.get(word) + wordScore);
                else
                    tf.put(word, wordScore);
            }
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
       
        return Math.sqrt(den);
    }
    
    private Double getWordScore(String word, Hashtable<String, Double> idf)
    {
        Double score = 12.233653765290939;
         if(idf.containsKey(word))                   
             score = idf.get(word);
         
         return score;
    }
    
    /************************************** Methode des Centroides **********************************************/
    
    public String calculateCentroid(List<String> sentences, Hashtable<String, Double> idf)
    {
        // Calculer tf*idf pour chaque mot
        Hashtable<String, Double> tfidf = new Hashtable<String, Double>();
        for(String sentence : sentences)
        {
            String[] words = sentence.split("[^a-zA-Z]");
            for(String word : words)
            {
                if (word.matches(".*\\w.*"))
                {
                    word = word.toLowerCase();
                    Double wordScore = getWordScore(word, idf);

                    if(tfidf.containsKey(word))
                        tfidf.put(word, tfidf.get(word) + wordScore);
                    else
                        tfidf.put(word, wordScore);
                }
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
            String[] words = sentence.split("[^a-zA-Z]");
            for(String word : words)
            {
                if(centroid.containsKey(word))
                    sentencesScore[i] = sentencesScore[i] + centroid.get(word);
            }
        }
        
        return getBestSentencesCendroid(sentences, sentencesScore);
    }
   
    private String getBestSentencesCendroid(List<String> sentences, Double[] sentencesScore)
    {
        Double minScore = getNthBiggestValue(sentencesScore, (int)(sentencesScore.length * pourcentage));
        String bestSentences = "";
        
        for(int i = 0; i < sentences.size() ; i++)
        {
            if(sentencesScore[i] >= minScore)
            {
                bestSentences += sentences.get(i);
                bestSentences += ". \n";
            }
        }
        
        return bestSentences;
    }
    
    /************************************** Matrices Operations **********************************************/
    
    private Double getNthBiggestValue(Double[] sentencesScore, int N)
    {
        Double[] sentencesScoreCpy = Arrays.copyOf(sentencesScore, sentencesScore.length);
        Arrays.sort(sentencesScoreCpy);
        return sentencesScoreCpy[sentencesScore.length - N - 1];
    }
    
    private int getNthBiggestValue(int[] sentencesScore, int N)
    {
        int[] sentencesScoreCpy = Arrays.copyOf(sentencesScore, sentencesScore.length);
        Arrays.sort(sentencesScoreCpy);
        return sentencesScoreCpy[sentencesScore.length - N - 1];
    }
    
    private Double norm(Double[] vect) {
        Double norm = 0.0;
        for(int i =0; i < vect.length ; i++){
            norm += vect[i]*vect[i];
        }
        norm = Math.sqrt(norm);
        return norm;
    }
    
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
    
    private Double[] multiply(Double[][] matrice1, Double[] matrice2) {
       int rowsIn1 = matrice1.length;
       int columnsIn1 = matrice1[0].length; // same as rows in B
       Double[] matriceResult = new Double[rowsIn1];
       for (int i = 0; i < rowsIn1; i++) {
           matriceResult[i] = 0.0;
           for (int k = 0; k < columnsIn1; k++) {
               matriceResult[i] = matriceResult[i] + matrice1[i][k] * matrice2[k];
           }
       }
       return matriceResult;
   }

    private Double[] powerMethod(Double[][] lexrank, int nbSentences, Double error)
    {
        ArrayList<Double[]> p = new ArrayList<Double[]>();
        p.add(new Double[nbSentences]);
        
        Arrays.fill(p.get(0), 1.0/nbSentences);
        int t = 0;
        Double delta = 0.0;
        
        do
        {
            t = t+1;
            p.add(new Double[nbSentences]);
        
            p.set(t,multiply(transpose(lexrank, nbSentences), p.get(t-1)));
            delta = norm(minus(p.get(t), p.get(t-1)));
        } while(delta > error);
        
        return p.get(t);
    }
        
}
