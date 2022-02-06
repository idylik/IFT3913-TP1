package main;

import java.io.File;
import java.io.FileWriter;
import com.opencsv.CSVWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    private final static String EXTENSION_FICHIER = ".java";

    public static void main(String[] args) throws IOException {

        File dossier;
        String typeClasse = "classe";
        String typePaquet = "paquet";

        if (args.length == 1) {
                if (Files.exists(Paths.get(args[0]))) {
                    dossier = new File(args[0]);
                } else {
                    System.out.println("Le chemin d'accès spécifié n'existe pas");
                    System.exit(2);
                    return;
                }
        } else {
            System.out.println("Le chemin d'accès du dossier n'est pas spécifié en argument");
            System.exit(1);
            return;
        }

        ArrayList<String> listeClasses = new ArrayList<>();
        ArrayList<String> listeDossiers = new ArrayList<>();
        listeDossiers.add(dossier.getPath());

        ParcoursFichiers.listerFichiers(dossier, EXTENSION_FICHIER, listeClasses, listeDossiers);

        //Tableaux de HashMap qui serviront aux calculs
        HashMap[] calculsClasses = initialiserListeCalculs(listeClasses, "classe");
        HashMap[] calculsDossiers = initialiserListeCalculs(listeDossiers, "paquet");

        for (HashMap classe : calculsClasses) {
            classe = CalculMetriques.calculerClasse(classe);
            System.out.println(classe);
        }

        for (HashMap paquet : calculsDossiers) {
            String cheminDossier = paquet.get("chemin").toString()+paquet.get("paquet").toString();
            CalculMetriques.calculerPaquet(cheminDossier, paquet);
            int loc = (int) paquet.get("paquet_LOC");
            int cLoc =  (int) paquet.get("paquet_CLOC");
            if (loc > 0 && cLoc >= 0) {
                float dc = (float)cLoc/(float)loc;
                paquet.put("paquet_DC", dc);
            }
            System.out.println(paquet);
        }

        creerFichierCsv(args[0], typeClasse, calculsClasses);
        creerFichierCsv(args[0], typePaquet, calculsDossiers);
    }

    public static HashMap[] initialiserListeCalculs(ArrayList<String> listeEntites, String nom) {

        HashMap[] calculsEntites = new HashMap[listeEntites.size()];

        for (int i=0; i < listeEntites.size(); i++) {
            calculsEntites[i] = new HashMap();
            int indexSlash = listeEntites.get(i).lastIndexOf(File.separator);

            calculsEntites[i].put("chemin", listeEntites.get(i).substring(0,indexSlash+1));
            calculsEntites[i].put(nom, listeEntites.get(i).substring(indexSlash+1));
            calculsEntites[i].put(nom+"_LOC", 0);
            calculsEntites[i].put(nom+"_CLOC", 0);
            calculsEntites[i].put(nom+"_DC", 0);
        }

        return calculsEntites;
    }

    public static void creerFichierCsv(String chemin, String type, HashMap[] calculsClasses) throws IOException {
        FileWriter ecritureFichier = new FileWriter(chemin + File.separator + type + "s.csv");
        CSVWriter ecritureCsv = new CSVWriter(ecritureFichier);

        String[] enTete = {"chemin", type, type + "_LOC", type + "_CLOC", type + "_DC"};
        ecritureCsv.writeNext(enTete,false);
        for (HashMap classe : calculsClasses) {
            String[] line = {classe.get("chemin").toString(), classe.get(type).toString(),
                    classe.get(type + "_LOC").toString(), classe.get(type + "_CLOC").toString(),
                    classe.get(type + "_DC").toString()};
            ecritureCsv.writeNext(line, false);
        }
        ecritureCsv.close();
    }
}