package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws IOException {

        String extensionFichier = ".java";
        String sousDossier = "\\projet-test";
        String nomDossierAbsolu = System.getProperty("user.dir")+sousDossier;
        File absoluteDossier = new File(nomDossierAbsolu);

        ArrayList<String> listeClasses = new ArrayList<>();
        ArrayList<String> listeDossiers = new ArrayList<>();

        ParcoursFichiers.listerFichiers(absoluteDossier, extensionFichier, listeClasses, listeDossiers);

        HashMap[] calculsClasses = initialiserListeCalculs(listeClasses, "classe");
        HashMap[] calculsDossiers = initialiserListeCalculs(listeDossiers, "paquet");

    }

    public static HashMap[] initialiserListeCalculs(ArrayList<String> listeEntites, String nom) {

        HashMap[] calculsEntites = new HashMap[listeEntites.size()];

        for (int i=0; i < listeEntites.size(); i++) {
            calculsEntites[i] = new HashMap();
            int indexBackSlash = listeEntites.get(i).lastIndexOf("\\");
            calculsEntites[i].put("chemin", listeEntites.get(i).substring(0,indexBackSlash+1));
            calculsEntites[i].put(nom, listeEntites.get(i).substring(indexBackSlash+1));
            calculsEntites[i].put(nom+"_LOC", 0);
            calculsEntites[i].put(nom+"_CLOC", 0);
            calculsEntites[i].put(nom+"_DC", 0);

            System.out.println(calculsEntites[i]);
        }

        return calculsEntites;
    }

}
