
import java.io.File;
import AbstractionSummarizer.*;
import ExtractionSummarizer.*;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequestWithBody;
import java.util.ArrayList;

public class main {

    public static void main(String[] args) {

	// Etape 1 : lire le fichier texte les documents à résumer
        //File documentList = new File(args[0]);

	// Etape 1 bis : si besoin, le découper en phrases (si étape commune aux deux algorithmes)
       // ArrayList<String> sentences = splitFile(fileToRead);
        
        /* Version 1 (deux méthodes séparément) */
        ExtractionSummarizer extractSummarizer;
        AbstractionSummarizer abstractSummarizer;
	// Etape 2 : appeler les deux méthodes sur cette entrée, et récupérer leurs sorties
        // Etape 3 : mettre ces sorties dans deux fichiers texte
        // Etape 4 : appel à ROUGE pour évaluer les sorties
        HttpRequestWithBody bob = Unirest.post("https://rxnlp-opinosis.p.mashape.com/generateOpinosisSummaries")
                .header("X-Mashape-Key", "3GOWrhKJBYmshlk9vy3Fkz1lHzIYp1AfV6Fjsn37wRHotx852h")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");

        bob.body("");


        /* Version 2 (deux méthodes à la suite) */
            // Etape 2 : appeler la méthode par extraction, récupérer la sortie
        // Etape 2 bis : la stocker dans un fichier (optionnel)
        // Etape 3 : appeler la méthode par abstraction, avec pour entrée le résultat de la précédente
        // Etape 4 : mettre le résultat dans un fichier texte
            // Etape 5 : appel à ROUGE pour évaluer la sortie
    }

    public static ArrayList<String> splitFile(File file) {
        return new ArrayList<String>();
    }
}
