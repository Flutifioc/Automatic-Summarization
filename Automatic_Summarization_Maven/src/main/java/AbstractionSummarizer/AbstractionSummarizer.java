/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package AbstractionSummarizer;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author lufon
 */
public class AbstractionSummarizer {
    public AbstractionSummarizer() {
        
    }
    
    public File summarizeText(ArrayList<String> sentences) throws UnirestException {
        HttpResponse<String> response = Unirest.post("https://rxnlp-opinosis.p.mashape.com/generateOpinosisSummaries")
                .header("X-Mashape-Key", "3GOWrhKJBYmshlk9vy3Fkz1lHzIYp1AfV6Fjsn37wRHotx852h")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .field("maxGap", "3")
                .field("maxSentences", "5")
                .field("minRedundancy", "2")
                .field("doCollapse", "true")
                .field("scoringFunction", "3")
                .field("text", "[{'sentence':'the/DT bathroom/NN was/VBD clean/JJ and/CC the/DT bed/NN was/VBD comfy/JJ ./.'},"
                        + "{'sentence':'the/DT bathroom/NN was/VBD clean/JJ and/CC the/DT bed/NN was/VBD comfy/JJ ./.'},"
                        + "{'sentence':'the/DT bed/NN was/VBD comfy/JJ and/CC bathroom/NN was/VBD clean/JJ ./.'},"
                        + "{'sentence':'the/DT bathroom/NN was/VBD dirty/JJ ./.'},"
                        + "{'sentence':'the/DT bathroom/NN was/VBD dirty/JJ ./.'},"
                        + "{'sentence':'the/DT bathroom/NN was/VBD dirty/JJ ./.'},"
                        + "{'sentence':'the/DT bathroom/NN was/VBD dirty/JJ ./.'},"
                        + "{'sentence':'the/DT bathroom/NN was/VBD too/RB dirty/JJ ./.'}]").asString();

        System.out.println(response.getStatusText());

        return new File ("bouh");
    }
}
