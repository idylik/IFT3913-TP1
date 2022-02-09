package main;

import java.io.File;
import java.io.FileWriter;
import com.opencsv.CSVWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static main.Type.*;

public class Main {

    private final static String EXTENSION_FICHIER = ".java";

    public static void main(String[] args) throws IOException {

        File dossier;

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
        HashMap[] calculsClasses = initialiserListeCalculs(listeClasses, CLASSE);
        HashMap[] calculsPaquets = initialiserListeCalculs(listeDossiers, PAQUET);

        for (HashMap classe : calculsClasses) {
            classe = CalculMetriques.calculerClasse(classe);
            System.out.println(classe);
        }

        for (HashMap<Type, Object> paquet : calculsPaquets) {
            String cheminDossier = paquet.get(CHEMIN).toString()+paquet.get(PAQUET).toString();
            CalculMetriques.calculerPaquet(cheminDossier, paquet, 0);
            int loc = (int) paquet.get(PAQUET_LOC);
            int cLoc = (int) paquet.get(PAQUET_CLOC);
            float dc = 0;
            long wcp = ((Number) paquet.get(WCP)).longValue();
            float bc = 0;

            if (loc > 0 && cLoc > 0) {
                dc = (float)cLoc/(float)loc;
                paquet.put(PAQUET_DC, dc);
            }

            if (dc > 0 && wcp > 0) {
                bc = (float)dc/(float)wcp;
                paquet.put(PAQUET_BC, bc);
            }

            System.out.println(paquet);
        }

        creerFichierCsv(args[0], CLASSE, calculsClasses);
        creerFichierCsv(args[0], PAQUET, calculsPaquets);
    }

    public static HashMap[] initialiserListeCalculs(ArrayList<String> listeEntites, Type type) {

        HashMap[] calculsEntites = new HashMap[listeEntites.size()];

        for (int i=0; i < listeEntites.size(); i++) {
            calculsEntites[i] = new HashMap<Type,Object>();
            int indexSlash = listeEntites.get(i).lastIndexOf(File.separator);

            calculsEntites[i].put(CHEMIN, listeEntites.get(i).substring(0,indexSlash+1));
            calculsEntites[i].put(type, listeEntites.get(i).substring(indexSlash+1));
            calculsEntites[i].put(LOC.typeFrom(type), 0);
            calculsEntites[i].put(CLOC.typeFrom(type), 0);
            calculsEntites[i].put(DC.typeFrom(type), 0);
            calculsEntites[i].put(CCM.typeFrom(type), 0);
            calculsEntites[i].put(BC.typeFrom(type), 0);
        }

        return calculsEntites;
    }

    public static void creerFichierCsv(String chemin, Type type, HashMap[] calculEntites) throws IOException {
        FileWriter ecritureFichier = new FileWriter(chemin + File.separator + type.getNom() + "s.csv");
        CSVWriter ecritureCsv = new CSVWriter(ecritureFichier);

        String[] enTete = {CHEMIN.getNom(), type.getNom(), LOC.stringFrom(type), CLOC.stringFrom(type), DC.stringFrom(type), type == CLASSE ? WMC.getNom() : WCP.getNom(), BC.stringFrom(type)};
        ecritureCsv.writeNext(enTete,false);
        for (HashMap<Type,Object> classe : calculEntites) {
            String[] line = {classe.get(CHEMIN).toString(), classe.get(type).toString(),
                    classe.get(LOC.typeFrom(type)).toString(), classe.get(CLOC.typeFrom(type)).toString(),
                    classe.get(DC.typeFrom(type)).toString(), type == CLASSE ? classe.get(WMC).toString() : classe.get(WCP).toString(), classe.get(BC.typeFrom(type)).toString()};
            ecritureCsv.writeNext(line, false);
        }
        ecritureCsv.close();
    }
}