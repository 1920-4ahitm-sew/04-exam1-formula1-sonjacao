package at.htl.formula1.boundary;

import at.htl.formula1.entity.Driver;
import at.htl.formula1.entity.Race;
import at.htl.formula1.entity.Result;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("results")
public class ResultsEndpoint {

    @PersistenceContext
    EntityManager em;

    /**
     * @param name als QueryParam einzulesen
     * @return JsonObject
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getPointsSumOfDriver(@QueryParam("name") String name) {
        Driver driver = em
                .createNamedQuery("Driver.findByName", Driver.class)
                .setParameter("NAME", name)
                .getSingleResult();
        Long driverPoints = em
                .createNamedQuery("Result.getPointsSumOfDriver", Long.class)
                .setParameter("DRIVER", driver)
                .getSingleResult();

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("driver", driver.getName());
        builder.add("points", driverPoints);
        return builder.build();
    }

    /**
     * @param country des Rennens
     * @return
     */
    @GET
    @Path("winner/{country}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findWinnerOfRace(@PathParam("country") String country) {
        Race race = em
                .createNamedQuery("Race.findByCountry", Race.class)
                .setParameter("COUNTRY", country)
                .getSingleResult();
        Driver winner = em
                .createNamedQuery("Result.findWinnerOfRace", Driver.class)
                .setParameter("RACE", race)
                .getSingleResult();
        return Response
                .ok(winner)
                .build();
    }


    // Ergänzen Sie Ihre eigenen Methoden ...

}
