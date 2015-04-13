/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package AbstractionSummarizer;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author lufon
 */
public class AbstractionSummarizer {
    
    private ArrayList<String> nonAnnotedSentences;
    private ArrayList<String> annotedSentences;
    private String body;
    private int maxGap;
    private int maxSentences;
    private int minRedundancy;
    private String doCollapse;
    private int scoringFunction;
    private File destFile;
    
    
    public AbstractionSummarizer(ArrayList<String> inputSentences, File destFile) {
        nonAnnotedSentences = new ArrayList<String>(inputSentences);
        annotedSentences = new ArrayList<String>();
        body = "";
        maxGap = 3;
        maxSentences = 5;
        minRedundancy = 2;
        doCollapse = "true";
        scoringFunction = 3;
        this.destFile = destFile;
    }
    
    public void setMaxGap(int value) {
        maxGap = value;
    }
    
    public void setMaxSentences(int value) {
        maxSentences = value;
    }
    
    public void setMinRedundancy(int value) {
        minRedundancy = value;
    }
    
    public void setDoCollapse(String value) {
        doCollapse = value;
    }
    
    public void setScoringFunction(int value) {
        scoringFunction = value;
    }
    
    private void annotateSentences() {
        MaxentTagger tagger = new MaxentTagger("Documents/Taggers/english-bidirectional-distsim.tagger");
        
        for (String sentence : nonAnnotedSentences) {
            String annotedString = tagger.tagString(sentence);
            annotedString = annotedString.replace("_", "/") + " ./.";
            annotedSentences.add(annotedString);
        }
    }
    
    private void createBodyForRequest() {
        body = "{\"maxGap\":\"" + maxGap + "\",\"maxSentences\":\"" + maxSentences
                + "\", \"minRedundancy\":\"" + minRedundancy + "\",\"doCollapse\":\""
                + doCollapse + "\",\"scoringFunction\":\"" + scoringFunction 
                + "\",\"text\":[{\"sentence\":\"" + annotedSentences.get(0) + "\"}";
        for (String sentence : annotedSentences.subList(1, annotedSentences.size())) {
            body = body + ",{\"sentence\":\"" + sentence + "\"}";
        }
        body = body + "]}";
        
    }
    
    public void summarizeText() throws UnirestException, FileNotFoundException, IOException {
        annotateSentences();
        createBodyForRequest();
        HttpResponse<JsonNode> response = Unirest.post("https://rxnlp-opinosis.p.mashape.com/generateOpinosisSummaries")
                .header("X-Mashape-Key", "3GOWrhKJBYmshlk9vy3Fkz1lHzIYp1AfV6Fjsn37wRHotx852h")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(body)
                .asJson();

        System.out.println(response.getBody());
        System.out.println(response.getStatus());
        FileOutputStream destStream = new FileOutputStream(destFile);
        destStream.write(response.getBody().toString().getBytes());
        destStream.close();
    }
}
