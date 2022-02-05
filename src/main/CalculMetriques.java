package main;

import java.io.*;
import java.util.Scanner;

public class CalculMetriques {

    /**
     * @param cheminFichierAbsolu chemin absolu du fichier
     * @return nombre de lignes non vides
     */
    public static int compterLignes(String cheminFichierAbsolu) {

        int nbLignesNonVides = 0;
        Scanner fichier;
        try {
            fichier = new Scanner(new File(cheminFichierAbsolu));

            while (fichier.hasNext()) {
                String ligne = fichier.nextLine();
                if (!ligne.isEmpty()) {
                    nbLignesNonVides++;
                }
            }

            fichier.close();
        } catch (FileNotFoundException ex) { }

        return nbLignesNonVides;
    }
}