package tests;

import main.CalculMetriques;
import org.junit.jupiter.api.Test;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;


public class CalculMetriquesTests {

    private static final String CHEMIN_1 = new File(System.getProperty("user.dir")) + "/projet-test/classe1.java";
    private static final String CHEMIN_2 = new File(System.getProperty("user.dir")) + "/projet-test/dossier2/dossier4/classe2.java";
    private static final String CHEMIN_3 = new File(System.getProperty("user.dir")) + "/projet-test/dossier1/classe3.java";

    @Test
    public void testNbLignesNonVidesTotal() {
        var expected1 = 18;
        var actual1 = CalculMetriques.compterLignes(CHEMIN_1);
        assertEquals(expected1, actual1);

        var expected2 = 6;
        var actual2 = CalculMetriques.compterLignes(CHEMIN_2);
        assertEquals(expected2, actual2);

        var expected3 = 231;
        var actual3 = CalculMetriques.compterLignes(CHEMIN_3);
        assertEquals(expected3, actual3);
    }

    @Test
    public void testNbLignesCode() {


    }

    @Test
    public void testNbLignesCommentaire() {

    }


}
