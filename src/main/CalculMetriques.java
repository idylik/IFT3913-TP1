package main;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        String codeSansComm = "";

        Scanner fichier;
        try {
            fichier = new Scanner(new File(cheminFichierAbsolu));

            while (fichier.hasNext()) {
                String ligne = fichier.nextLine();
                if (!ligne.isEmpty()) {
                    ligne = ligne.trim();
                    nbLignesNonVides++;
                    if (estCode(ligne)) {
                        classeLOC++;
                        codeSansComm += " "+ligne;
                    }
                    if (estCommentaire(ligne)) classeCLOC++;

                    }
                }

            fichier.close();
        } catch (FileNotFoundException ex) { }

        nbLignes.put(LIGNES_NON_VIDES, nbLignesNonVides);
        nbLignes.put(CLASSE_LOC, classeLOC);
        nbLignes.put(CLASSE_CLOC, classeCLOC);

        long wmc = calculerWMC(codeSansComm);

        nbLignes.put(WMC, wmc);

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
                long wmc =  (long) compte.get(WMC);

                int paquetLoc = (int) calculsDossier.get(PAQUET_LOC);
                int paquetCLoc = (int) calculsDossier.get(PAQUET_CLOC);
                long wcp = ((Number) calculsDossier.get(WCP)).longValue();

                calculsDossier.put(PAQUET_LOC, paquetLoc+loc);
                calculsDossier.put(PAQUET_CLOC, paquetCLoc+cLoc);
                calculsDossier.put(WCP, wcp+wmc);

            } else {
                //Sous-dossier:
                calculerPaquet(entite.getAbsoluteFile().toString(), calculsDossier);
            }
        }

    }

    public static HashMap calculerClasse(HashMap calculsClasse) {

        String cheminFichier = calculsClasse.get(CHEMIN).toString()+calculsClasse.get(CLASSE).toString();
        HashMap compte = compterLignes(cheminFichier);
        int loc = (int)compte.get(CLASSE_LOC);
        int cLoc = (int)compte.get(CLASSE_CLOC);
        float dc = 0;
        long wmc = (long)compte.get(WMC);
        float bc = 0;


        calculsClasse.put(CLASSE_LOC, loc);
        calculsClasse.put(CLASSE_CLOC, cLoc);
        calculsClasse.put(WMC, wmc);

        if (loc > 0 && cLoc > 0) {
            dc = (float)cLoc/(float)loc;
            calculsClasse.put(CLASSE_DC, dc);
        }

        if (dc > 0 && wmc > 0) {
            bc = (float)dc/(float)wmc;
            calculsClasse.put(CLASSE_BC, bc);
        }


        return calculsClasse;
    }

    public static long calculerWMC(String codeSansComm) {

        long compteur = 0;

        //Regex qui match une déclaration de méthode
        //(Inspiré de: https://stackoverflow.com/questions/68633/regex-that-will-match-a-java-method-declaration/847507#847507)
        Pattern methPattern = Pattern.compile("\\s(?:(?:public|protected|private)?\\s+)?(?:(static|final|native|synchronized|abstract|threadsafe|transient|(?:<[?\\w\\[\\] ,&]+>)|(?:<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>)|(?:<[^<]*<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>[^>]*>))\\s+){0,}(?!return)\\b([\\w.]+)\\b(?:|(?:<[?\\w\\[\\] ,&]+>)|(?:<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>)|(?:<[^<]*<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>[^>]*>))((?:\\[\\]){0,})\\s+\\b\\w+\\b\\s*\\(\\s*(?:\\b([\\w.]+)\\b(?:|(?:<[?\\w\\[\\] ,&]+>)|(?:<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>)|(?:<[^<]*<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>[^>]*>))((?:\\[\\]){0,})(\\.\\.\\.)?\\s+(\\w+)\\b(?![>\\[])\\s*(?:,\\s+\\b([\\w.]+)\\b(?:|(?:<[?\\w\\[\\] ,&]+>)|(?:<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>)|(?:<[^<]*<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>[^>]*>))((?:\\[\\]){0,})(\\.\\.\\.)?\\s+(\\w+)\\b(?![>\\[])\\s*){0,})?\\s*\\)(?:\\s*throws [\\w.]+(\\s*,\\s*[\\w.]+))?\\s*(?:\\{|;)[ \\t]*");
        Matcher methMatcher = methPattern.matcher(codeSansComm);

        Pattern predicats = Pattern.compile("else\\s*if|if|while|for\\s*\\(|else\\s*\\{*|\\s+case\\s+");
        Matcher predicatsMatcher = predicats.matcher(codeSansComm);

        compteur += methMatcher.results().count();
        compteur += predicatsMatcher.results().count();
        return compteur;
    }

}