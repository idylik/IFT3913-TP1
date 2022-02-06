package main;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

import static main.Type.*;

public class CalculMetriques {

    private static boolean estDansCommentaire;

    /**
     * @param cheminFichierAbsolu chemin absolu du fichier
     * @return nombre de lignes non vides
     */
    public static HashMap<Type, Object> compterLignes(String cheminFichierAbsolu) {
        HashMap<Type, Object> nbLignes = new HashMap<>();

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

    public static void calculerPaquet(String cheminDossier, HashMap<Type, Object> calculsDossier) {
        File dossier = new File(cheminDossier);
        File[] listeEntites = dossier.listFiles();

        for (File entite : listeEntites) {
            if (entite.isFile()) {
                //Classe:
                HashMap compte = compterLignes(entite.getAbsolutePath());
                int loc = (int) compte.get(CLASSE_LOC);
                int cLoc =  (int) compte.get(CLASSE_CLOC);

                int paquetLoc = (int) calculsDossier.get(PAQUET_LOC);
                int paquetCLoc = (int) calculsDossier.get(PAQUET_CLOC);

                calculsDossier.put(PAQUET_LOC, paquetLoc+loc);
                calculsDossier.put(PAQUET_CLOC, paquetCLoc+cLoc);

            } else {
                //Sous-dossier:
                calculerPaquet(entite.getAbsoluteFile().toString(), calculsDossier);
            }
        }

        //return calculsDossier;
    }

    public static HashMap calculerClasse(HashMap calculsClasse) {

        String cheminFichier = calculsClasse.get(CHEMIN).toString()+calculsClasse.get(CLASSE).toString();
        HashMap compte = compterLignes(cheminFichier);
        int loc = (int)compte.get(CLASSE_LOC);
        int cLoc = (int)compte.get(CLASSE_CLOC);


        calculsClasse.put(CLASSE_LOC, loc);
        calculsClasse.put(CLASSE_CLOC, cLoc);

        if (loc > 0 && cLoc >= 0) {
            float dc = (float)cLoc/(float)loc;
            calculsClasse.put(CLASSE_DC, dc);
        }

        return calculsClasse;
    }

}