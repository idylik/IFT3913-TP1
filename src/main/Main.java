package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws IOException {

        String extensionFichier = ".java";
        File dossier = new File("./projet-test");

        ArrayList<String> listeClasses = new ArrayList<>();
        ArrayList<String> listeDossiers = new ArrayList<>();

        ParcoursFichiers.listerFichiers(dossier, extensionFichier, listeClasses, listeDossiers);

        //Tableaux de HashMap qui serviront aux calculs
        HashMap[] calculsClasses = initialiserListeCalculs(listeClasses, "classe");
        HashMap[] calculsDossiers = initialiserListeCalculs(listeDossiers, "paquet");

        for (HashMap classe : calculsClasses) {
            classe = CalculMetriques.calculerClasse(classe);
            System.out.println(classe);
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

}
