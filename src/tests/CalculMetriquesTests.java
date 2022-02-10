package tests;

import main.CalculMetriques;
import main.Type;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.HashMap;

import static main.Type.*;
import static org.junit.jupiter.api.Assertions.*;


public class CalculMetriquesTests {

    private static final String CHEMIN_1 = new File(System.getProperty("user.dir")) + "/projet-test/classe1.java";
    private static final String CHEMIN_2 = new File(System.getProperty("user.dir")) + "/projet-test/dossier2/dossier4/classe2.java";
    private static final String CHEMIN_3 = new File(System.getProperty("user.dir")) + "/projet-test/dossier1/classe3.java";
    private static final String CHEMIN_4 = new File(System.getProperty("user.dir")) + "/projet-test/dossier1/classe4.java";

    @Test
    public void testNbLignesFichier() {
        // CHEMIN 1
        HashMap<Type, Integer> expected1 = new HashMap<>();
        expected1.put(LIGNES_NON_VIDES, 18);
        expected1.put(CLASSE_LOC, 6);
        expected1.put(CLASSE_CLOC, 13);

        var actual1 = CalculMetriques.compterLignes(CHEMIN_1);
        assertEquals(expected1.get(LIGNES_NON_VIDES), actual1.get(LIGNES_NON_VIDES));
        assertEquals(expected1.get(CLASSE_LOC), actual1.get(CLASSE_LOC));
        assertEquals(expected1.get(CLASSE_CLOC), actual1.get(CLASSE_CLOC));

        // CHEMIN 2
        HashMap<Type, Integer> expected2 = new HashMap<>();
        expected2.put(LIGNES_NON_VIDES, 6);
        expected2.put(CLASSE_LOC, 2);
        expected2.put(CLASSE_CLOC, 4);

        var actual2 = CalculMetriques.compterLignes(CHEMIN_2);
        assertEquals(expected2.get(LIGNES_NON_VIDES), actual2.get(LIGNES_NON_VIDES));
        assertEquals(expected2.get(CLASSE_LOC), actual2.get(CLASSE_LOC));
        assertEquals(expected2.get(CLASSE_CLOC), actual2.get(CLASSE_CLOC));

        // CHEMIN 3
        HashMap<Type, Integer> expected3 = new HashMap<>();
        expected3.put(LIGNES_NON_VIDES, 231);
        expected3.put(CLASSE_LOC, 102);
        expected3.put(CLASSE_CLOC, 130);

        var actual3 = CalculMetriques.compterLignes(CHEMIN_3);
        assertEquals(expected3.get(LIGNES_NON_VIDES), actual3.get(LIGNES_NON_VIDES));
        assertEquals(expected3.get(CLASSE_LOC), actual3.get(CLASSE_LOC));
        assertEquals(expected3.get(CLASSE_CLOC), actual3.get(CLASSE_CLOC));

        // CHEMIN 4
        HashMap<Type, Integer> expected4 = new HashMap<>();
        expected4.put(LIGNES_NON_VIDES, 14);
        expected4.put(CLASSE_LOC, 3);
        expected4.put(CLASSE_CLOC, 13);

        var actual4 = CalculMetriques.compterLignes(CHEMIN_4);
        assertEquals(expected4.get(LIGNES_NON_VIDES), actual4.get(LIGNES_NON_VIDES));
        assertEquals(expected4.get(CLASSE_LOC), actual4.get(CLASSE_LOC));
        assertEquals(expected4.get(CLASSE_CLOC), actual4.get(CLASSE_CLOC));
    }
}

/*
paquet_LOC
projet-test = 6 (classe1)
dossier1 = 105 (classe3 + classe4 = 102 + 3)
dossier2 = 19 (classe6)
dossier3 = 0
dossier4 = 23 (classe2 + classe7 + classe8 = 2 + 11 + 10)
dossier5 = 45 (classe5)

paquet_CLOC
projet-test = 13 (classe1)
dossier1 = 143 (classe3 + classe4 = 130 + 13)
dossier2 = 16 (classe6)
dossier3 = 0
dossier4 = 28 (classe2 + classe7 + classe8 = 4 + 22 + 2)
dossier5 = 14 (classe5)

WCP
projet-test = 30 (classe1 + dossier1 + dossier2 = 0 + 21 + 9)
dossier1 = 21 (classe3 + classe4 + classe5 = 13 + 0 + 8)
dossier2 = 9 (classe6 + classe2 + classe7 + classe8 = 4 + 0 + 2 + 3)
dossier3 = 8 (classe5)
dossier4 = 5 (classe2 + classe7 + classe8 = 0 + 2 + 3)
dossier5 = 8 (classe5)
 */