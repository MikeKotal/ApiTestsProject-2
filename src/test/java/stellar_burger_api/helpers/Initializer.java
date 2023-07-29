package stellar_burger_api.helpers;

import io.restassured.RestAssured;
import org.junit.Before;

import static stellar_burger_api.Constants.STELLAR_BURGERS_URL;

public class Initializer {

    @Before
    public void setUp() {
        RestAssured.baseURI = STELLAR_BURGERS_URL;
    }

}
