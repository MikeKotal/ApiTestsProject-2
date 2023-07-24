package praktikum.helpers;

import io.restassured.RestAssured;
import org.junit.Before;

import static praktikum.Constants.STELLAR_BURGERS_URL;

public class Initializer {

    @Before
    public void setUp() {
        RestAssured.baseURI = STELLAR_BURGERS_URL;
    }

}
