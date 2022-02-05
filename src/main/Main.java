package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {

        String extensionFichier = ".java";
        String sousDossier = "\\projet-test";
        String nomDossierAbsolu = System.getProperty("user.dir")+sousDossier;
        File absoluteDossier = new File(nomDossierAbsolu);

        ArrayList<String> listeFichiers = ParcoursFichiers.listerFichiers(absoluteDossier, extensionFichier);

    }
}
