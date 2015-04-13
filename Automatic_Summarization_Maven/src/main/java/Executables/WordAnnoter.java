package Executables;

import java.util.HashSet;
import edu.jhu.nlp.wikipedia.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

public class WordAnnoter {

    public static void main(String[] args) {
        
        Hashtable<String, Double> idf = new Hashtable<String, Double>();
        int N = 0;
        
        WikiPageIterator it = initializeWikipediaParser("/usagers/lufon/Documents/IFT6010/IFT6010/Automatic_Summarization_Maven/simplewiki-latest-pages-articles.xml.bz2");
        
        String document = "";
        while (it.hasMorePages()) {
            document = getWikipediaArticle(it);
            HashSet<String> currentDocWords = new HashSet<String>();
            N++;
            
            String[] words = document.split(" "); // TODO: remove capital letter
            for(String word : words)
            {
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
                    currentDocWords.add(word);
                }
            }
        }
        
        for(String word : idf.keySet())
        {
            idf.put(word, Math.log(N/idf.get(word)));
        }
        
        writeIDFToTextFile(idf);
    }
    
    private static WikiPageIterator initializeWikipediaParser(String path)
    {
        WikiXMLParser wxp = WikiXMLParserFactory.getDOMParser(path);
        WikiPageIterator it = null;
        
        try {
            wxp.parse();
            it = wxp.getIterator();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return it;
    }
    
    private static String getWikipediaArticle(WikiPageIterator it)
    {
        String text = "";
        
        if(it.hasMorePages()) {
            WikiPage page = it.nextPage();
            text = page.getText();
        }
        
        return text;
    }
    
    private static void writeIDFToTextFile(Hashtable<String, Double> idf)
    {
     try {
        BufferedWriter out = new BufferedWriter(new FileWriter("idf.txt"));
            for(String word : idf.keySet()) {
                out.write(word + " " + idf.get(word) + "\n");
                out.newLine();
            }
            out.close();
        } catch (IOException e) {}   
    }
}
