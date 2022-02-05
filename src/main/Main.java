package main;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        String extensionFichier = ".java";
        File absoluteDossier = new File(System.getProperty("user.dir"));
        ParcoursFichiers.listerFichiers(absoluteDossier, extensionFichier);

    }
}
