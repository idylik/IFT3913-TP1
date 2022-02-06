package main;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import static main.TypeLigne.*;

public class CalculMetriques {

    private static boolean estDansCommentaire;

    /**
     * @param cheminFichierAbsolu chemin absolu du fichier
     * @return nombre de lignes non vides
     */
    public static HashMap<TypeLigne, Integer> compterLignes(String cheminFichierAbsolu) {
        HashMap<TypeLigne, Integer> nbLignes = new HashMap<>();

        int classeCLOC = 0;
        int classeLOC = 0;
        int nbLignesNonVides = 0;
        estDansCommentaire = false;

        Scanner fichier;
        try {
            fichier = new Scanner(new File(cheminFichierAbsolu));

            while (fichier.hasNext()) {
                String ligne = fichier.nextLine();
                if (!ligne.isEmpty()) {
                    ligne = ligne.trim();
                    nbLignesNonVides++;
                    if (estCode(ligne)) classeLOC++;
                    if (estCommentaire(ligne)) classeCLOC++;

                    }
                }

            fichier.close();
        } catch (FileNotFoundException ex) { }

        nbLignes.put(LIGNES_NON_VIDES, nbLignesNonVides);
        nbLignes.put(CLASSE_LOC, classeLOC);
        nbLignes.put(CLASSE_CLOC, classeCLOC);

        return nbLignes;
    }

    private static boolean estCode(String ligne) {
        return !(estDansCommentaire || ligne.startsWith("//") || ligne.startsWith("/*"));
    }

    public static boolean estCommentaire(String ligne) {
        if ((ligne.contains("//") && !estDansCommentaire)
        || (ligne.contains("/*") && ligne.contains("*/"))) {
            return true;
        }
        if (ligne.contains("/*") && !ligne.contains("*/")) {
            estDansCommentaire = true;
            return true;
        }
        if (estDansCommentaire && ligne.contains("*/")) {
            estDansCommentaire = false;
            return true;
        }
        return estDansCommentaire && !(ligne.contains("/*") && ligne.contains("*/"));
    }

    public static HashMap calculerPaquet(HashMap calculsDossier) {
        String cheminDossier = calculsDossier.get("chemin").toString()+calculsDossier.get("classe").toString();
        File dossier = new File(cheminDossier);
        File[] listeFichiers = dossier.listFiles();

        for (File fichier : listeFichiers) {
            if (fichier.isFile()) {
                //Classe:

            } else {

            }
        }


        return new HashMap();
    }

    public static HashMap calculerClasse(HashMap calculsClasse) {

        String cheminFichier = calculsClasse.get("chemin").toString()+calculsClasse.get("classe").toString();
        HashMap compte = compterLignes(cheminFichier);
        int loc = (int) compte.get(CLASSE_LOC);
        int cLoc =  (int) compte.get(CLASSE_CLOC);
        float dc = (float)cLoc/(float)loc;

        calculsClasse.put("classe_LOC", loc);
        calculsClasse.put("classe_CLOC", cLoc);
        calculsClasse.put("classe_DC",  dc);

        return calculsClasse;
    }

}