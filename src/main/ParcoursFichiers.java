package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ParcoursFichiers {
    public static void listerFichiers(File chemin, String extensionFichier, ArrayList<String> listeClasses, ArrayList<String> listeDossiers) throws IOException {
        //var listeFichiersJava = new ArrayList<String>();
        File listeEntites[] = chemin.listFiles();

        for (File entite : listeEntites) {
            if (entite.isFile()) {
                if (entite.getName().endsWith(extensionFichier)) {
                    listeClasses.add(entite.getAbsoluteFile().toString());
                }
            } else {
                listeDossiers.add(entite.getAbsoluteFile().toString());
                listerFichiers(entite, extensionFichier, listeClasses, listeDossiers);
            }
        }

    }
}
