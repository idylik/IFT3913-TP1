package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Classe qui sert à obtenir l'arborescence du dossier avec ses classes et sous-dossiers
 */
public class ParcoursFichiers {



    /**
     * @param chemin chemin du dossier/paquet à analyser
     * @param extensionFichier type de code à analyser
     * @param listeClasses listes des classes du dossier
     * @param listeDossiers listes des sous-dossiers du dossier
     * @return compteur (complexité de McCabe)
     */
    public static void listerFichiers(File chemin, String extensionFichier, ArrayList<String> listeClasses, ArrayList<String> listeDossiers) throws IOException {

        File listeEntites[] = chemin.listFiles();

        for (File entite : listeEntites) {
            if (entite.isFile()) {
                if (entite.getName().endsWith(extensionFichier)) {
                    listeClasses.add(entite.getPath());
                }
            } else {
                if (estPaquet(entite, extensionFichier)) {
                    listeDossiers.add(entite.getPath());
                }
                listerFichiers(entite, extensionFichier, listeClasses, listeDossiers);
            }
        }
    }

    /**
     * @param entite fichier/dossier
     * @param extensionFichier type de code à analyser
     * @return boolean true si la ligne est un paquet, sinon false
     */
    public static boolean estPaquet(File entite, String extensionFichier) {
        File[] listeEntites = entite.listFiles();
        for (File sousEntite : listeEntites) {
            if (sousEntite.getName().endsWith(extensionFichier)) {
                return true;
            }
        }
        return false;
    }
}