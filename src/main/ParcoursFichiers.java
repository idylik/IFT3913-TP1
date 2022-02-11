package main;

import java.io.File;
import java.util.ArrayList;

/**
 * Crée l'arborescence du dossier avec ses classes et sous-dossiers.
 */
public class ParcoursFichiers {

    /**
     * Ajoute les classes et les sous-dossiers dans les listes correspondantes.
     *
     * @param chemin chemin du dossier/paquet à analyser
     * @param extensionFichier type de code à analyser
     * @param listeClasses liste des classes du dossier
     * @param listeDossiers liste des sous-dossiers du dossier
     */
    public static void listerFichiers(File chemin, String extensionFichier, ArrayList<String> listeClasses,
                                      ArrayList<String> listeDossiers) {

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
     * Vérifie si l'entité passée en paramètre est un paquet.
     *
     * @param entite fichier ou paquet
     * @param extensionFichier type de code à analyser
     * @return true si la ligne est un paquet, false sinon
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