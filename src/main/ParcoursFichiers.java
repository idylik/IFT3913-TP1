package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ParcoursFichiers {
    public static ArrayList<String> listerFichiers(File chemin, String extensionFichier) throws IOException {
        var listeFichiersJava = new ArrayList<String>();
        File listeEntites[] = chemin.listFiles();

        for (File entite : listeEntites) {
            if (entite.isFile()) {
                if (entite.getName().endsWith(extensionFichier)) {
                    System.out.println(entite.getAbsoluteFile());
                    listeFichiersJava.add(entite.getAbsoluteFile().toString());
                }
            } else {
                listerFichiers(entite, extensionFichier);
            }
        }

        return listeFichiersJava;
    }
}