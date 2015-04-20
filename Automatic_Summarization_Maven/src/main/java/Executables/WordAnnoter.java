package Executables;

import java.util.HashSet;
import edu.jhu.nlp.wikipedia.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WordAnnoter {
    
    private BlockingQueue<String> queueArticles = new ArrayBlockingQueue<String>(5);
    private parseThread p;
    
    public void calculateIdf() throws InterruptedException {
        
        Hashtable<String, Double> idf = new Hashtable<String, Double>();
        int N = 0;
        
        initializeWikipediaParser("C:\\Users\\Lara\\Desktop\\Poly\\H2015\\ift6010\\Automatic_Summarization_Maven\\simplewiki-latest-pages-articles.xml.bz2");
        
        while (true) 
        {
            String document = queueArticles.poll(1, TimeUnit.MINUTES);
            
            if(document == null) 
                break;
            
            HashSet<String> currentDocWords = new HashSet<String>();
            N++;
            
            String[] words = document.split("[^a-zA-Z]");
            for(String word : words)
            {
                word = word.toLowerCase();
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
        
        System.out.println("Write to texte File");
        writeIDFToTextFile(idf, N);
        System.out.println("Finished Writing");
    }
    
    private void initializeWikipediaParser(String path)
    {
        WikiXMLParser wxp = WikiXMLParserFactory.getSAXParser(path);
        
        try {
            wxp.setPageCallback(new PageCallbackHandler() {
                public void process(WikiPage page) {
                    //System.out.println(page.getTitle());
                    try {
                        queueArticles.put(page.getText());
                    } catch (InterruptedException ex) {
                        Logger.getLogger(WordAnnoter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            
            p = new parseThread(wxp);
        }catch(Exception e) {
            e.printStackTrace();
        }
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
    
    private static void writeIDFToTextFile(Hashtable<String, Double> idf, int N)
    {
     try {
        BufferedWriter out = new BufferedWriter(new FileWriter("idf.txt"));
            for(String word : idf.keySet()) {
                out.write(word + " " + idf.get(word));
                out.newLine();
            }
            out.write(N + "articles");
            out.close();
        } catch (IOException e) {}   
    }
    
    public class parseThread extends Thread
    {
        private WikiXMLParser wxp;
        
        public void run()
        {
            try {
                wxp.parse();
            } catch (Exception ex) {
                Logger.getLogger(WordAnnoter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public parseThread(WikiXMLParser wxp_)
        {
            wxp = wxp_; 
            start();
        }
        
    }
}
