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

    public static void calculerPaquet(String cheminDossier, HashMap calculsDossier) {
        File dossier = new File(cheminDossier);
        File[] listeEntites = dossier.listFiles();

        for (File entite : listeEntites) {
            if (entite.isFile()) {
                //Classe:
                HashMap compte = compterLignes(entite.getAbsolutePath());
                int loc = (int) compte.get(CLASSE_LOC);
                int cLoc =  (int) compte.get(CLASSE_CLOC);

                int paquetLoc = (int) calculsDossier.get("paquet_LOC");
                int paquetCLoc = (int) calculsDossier.get("paquet_CLOC");

                calculsDossier.put("paquet_LOC", paquetLoc+loc);
                calculsDossier.put("paquet_CLOC", paquetCLoc+cLoc);

            } else {
                //Sous-dossier:
                calculerPaquet(entite.getAbsoluteFile().toString(), calculsDossier);
            }
        }

        //return calculsDossier;
    }

    public static HashMap calculerClasse(HashMap calculsClasse) {

        String cheminFichier = calculsClasse.get("chemin").toString()+calculsClasse.get("classe").toString();
        HashMap compte = compterLignes(cheminFichier);
        int loc = (int) compte.get(CLASSE_LOC);
        int cLoc =  (int) compte.get(CLASSE_CLOC);


        calculsClasse.put("classe_LOC", loc);
        calculsClasse.put("classe_CLOC", cLoc);

        if (loc > 0 && cLoc > 0) {
            float dc = (float)cLoc/(float)loc;
            calculsClasse.put("classe_DC",  dc);
        }

        return calculsClasse;
    }

}