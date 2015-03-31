/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Executables;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

/**
 *
 * @author lufon
 */
public class WordAnnoter {

    public static void main(String[] args) {
        
        Hashtable<String, Double> idf = new Hashtable<String, Double>();
        HashSet<String> currentDocWords = new HashSet<String>();
        int N = 0;
        
        String[] documents; // the files pathes 
        for(int i = 0 ; i < documents.length ; ++i)
        {
            String document = documents[i]; // TODO : get the file's path from documents[i], open it and read the document
            String[] words = document.split(" "); // TODO: remove all ponctuation and capital letter, and get a String[] of the words
            for(String word : words)
            {
                N++;
                if(idf.containsKey(word))
                {
                    if(!currentDocWords.contains(word))
                    {
                        idf.put(word, idf.get(word)+1);
                        currentDocWords.add(word);
                    }
                }
                else
                {
                    idf.put(word, 1.0);
                }
            }
        }
        
        for(String word : idf.keySet())
        {
            idf.put(word, Math.log(N/idf.get(word)));
        }
        
    }
}
