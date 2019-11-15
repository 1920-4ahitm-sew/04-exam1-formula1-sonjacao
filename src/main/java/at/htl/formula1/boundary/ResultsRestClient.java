package at.htl.formula1.boundary;

import at.htl.formula1.entity.Driver;
import at.htl.formula1.entity.Race;
import at.htl.formula1.entity.Result;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ResultsRestClient {


    @PersistenceContext
    EntityManager em;

    public static final String RESULTS_ENDPOINT = "http://vm90.htl-leonding.ac.at/results";
    private Client client;
    private WebTarget target;

    /**
     * Vom RestEndpoint werden alle Result abgeholt und in ein JsonArray gespeichert.
     * Dieses JsonArray wird an die Methode persistResult(...) übergeben
     */
    public void readResultsFromEndpoint() {
        client = ClientBuilder.newClient();
        target = client.target(RESULTS_ENDPOINT);
        Response response = target.request(MediaType.APPLICATION_JSON).get();

        JsonArray payload = response.readEntity(JsonArray.class);

        persistResult(payload);
    }

    /**
     * Das JsonArray wird durchlaufen (iteriert). Man erhäjt dabei Objekte vom
     * Typ JsonValue. diese werden mit der Methode .asJsonObject() in ein
     * JsonObject umgewandelt.
     * <p>
     * zB:
     * for (JsonValue jsonValue : resultsJson) {
     * JsonObject resultJson = jsonValue.asJsonObject();
     * ...
     * <p>
     * Mit den entsprechenden get-Methoden können nun die einzelnen Werte
     * (raceNo, position und driverFullName) ausgelesen werden.
     * <p>
     * Mit dem driverFullName wird der entsprechende Driver aus der Datenbank ausgelesen.
     * <p>
     * Dieser Driver wird dann dem neu erstellten Result-Objekt übergeben
     *
     * @param resultsJson
     */
    @Transactional
    void persistResult(JsonArray resultsJson) {
        for (JsonValue jsonValue : resultsJson) {
            JsonObject resultJson = jsonValue.asJsonObject();


            Driver driver = em
                    .createNamedQuery("Driver.findByName", Driver.class)
                    .setParameter("NAME", resultJson.getString("driverFullName"))
                    .getSingleResult();
            Race race = em.find(Race.class, (long) resultJson.getInt("raceNo"));

            Result result = new Result();
            result.setDriver(driver);
            result.setRace(race);
            result.setPosition(resultJson.getInt("position"));

            switch (result.getPosition()) {
                case 1:
                    result.setPoints(25);
                    break;
                case 2:
                    result.setPoints(18);
                    break;
                case 3:
                    result.setPoints(15);
                    break;
                case 4:
                    result.setPoints(12);
                    break;
                case 5:
                    result.setPoints(10);
                    break;
                case 6:
                    result.setPoints(8);
                    break;
                case 7:
                    result.setPoints(6);
                    break;
                case 8:
                    result.setPoints(4);
                    break;
                case 9:
                    result.setPoints(2);
                    break;
                case 10:
                    result.setPoints(1);
                    break;
                default:
                    result.setPoints(0);
            }

            em.persist(result);

        }
    }

}
