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
import com.sun.org.apache.bcel.internal.generic.AALOAD;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

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
        maxSentences = 20;
        minRedundancy = 2;
        doCollapse = "true";
        scoringFunction = 1;
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
    
    private void annotateSentences(String execPath) {
        MaxentTagger tagger = new MaxentTagger(execPath + "/Taggers/english-bidirectional-distsim.tagger");
        annotedSentences = new ArrayList<String>();
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
    
    public void setSentences(ArrayList<String> newSentences) {
        this.annotedSentences = new ArrayList<String>();
        this.annotedSentences.addAll(newSentences);
    }
    
    public void setDestinationFile(File newDest) {
        this.destFile = newDest;
    }
    
    public void summarizeText(String execPath) throws UnirestException, FileNotFoundException, IOException {
        annotateSentences(execPath);
        createBodyForRequest();
        HttpResponse<JsonNode> response = Unirest.post("https://rxnlp-opinosis.p.mashape.com/generateOpinosisSummaries")
                .header("X-Mashape-Key", "3GOWrhKJBYmshlk9vy3Fkz1lHzIYp1AfV6Fjsn37wRHotx852h")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(body)
                .asJson();

        if (response.getStatus() == 504) {
            System.err.println("Erreur : timeout. Relancer le programme.");
            return;
        }
        FileOutputStream destStream = new FileOutputStream(destFile);
        JSONArray responseArray = response.getBody().getArray().getJSONObject(0).getJSONArray("results");
        String resultSentences = "";
        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject jsonObject = responseArray.getJSONObject(i);
            resultSentences = resultSentences + (jsonObject.getString("summary").replaceAll("/\\S{1,4}", "")) + "\r\n";
        }
        destStream.write(resultSentences.getBytes());
        destStream.close();
    }
    
}
