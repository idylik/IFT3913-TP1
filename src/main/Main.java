package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static main.TypeLigne.CLASSE_CLOC;
import static main.TypeLigne.CLASSE_LOC;

public class Main {

    public static void main(String[] args) throws IOException {

        String extensionFichier = ".java";
        File dossier = new File("./projet-test");

        ArrayList<String> listeClasses = new ArrayList<>();
        ArrayList<String> listeDossiers = new ArrayList<>();
        listeDossiers.add(dossier.getPath());

        ParcoursFichiers.listerFichiers(dossier, extensionFichier, listeClasses, listeDossiers);

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
            if (loc > 0 && cLoc > 0) {
                float dc = (float)cLoc/(float)loc;
                paquet.put("paquet_DC", dc);
            }
            System.out.println(paquet);
        }

    }

    public static HashMap[] initialiserListeCalculs(ArrayList<String> listeEntites, String nom) {

        HashMap[] calculsEntites = new HashMap[listeEntites.size()];

        for (int i=0; i < listeEntites.size(); i++) {
            calculsEntites[i] = new HashMap();
            int indexSlash = listeEntites.get(i).lastIndexOf("/");
            indexSlash = indexSlash == -1 ? listeEntites.get(i).lastIndexOf("\\") : indexSlash;

            calculsEntites[i].put("chemin", listeEntites.get(i).substring(0,indexSlash+1));
            calculsEntites[i].put(nom, listeEntites.get(i).substring(indexSlash+1));
            calculsEntites[i].put(nom+"_LOC", 0);
            calculsEntites[i].put(nom+"_CLOC", 0);
            calculsEntites[i].put(nom+"_DC", 0);
        }

        return calculsEntites;
    }


    public static void ecrireCsv(HashMap donneesCalcul, String nomFichier, String nomEntite) {
        //File fichier = new File(nomFichier);

    }

}
