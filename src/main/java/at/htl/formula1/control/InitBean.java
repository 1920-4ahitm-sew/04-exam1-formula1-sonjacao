package at.htl.formula1.control;

import at.htl.formula1.boundary.ResultsRestClient;
import at.htl.formula1.entity.Driver;
import at.htl.formula1.entity.Race;
import at.htl.formula1.entity.Team;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@ApplicationScoped
public class InitBean {

    private static final String TEAM_FILE_NAME = "teams.csv";
    private static final String RACES_FILE_NAME = "races.csv";

    @PersistenceContext
    EntityManager em;

    @Inject
    ResultsRestClient client;

    @Transactional
    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {

        readTeamsAndDriversFromFile(TEAM_FILE_NAME);
        readRacesFromFile(RACES_FILE_NAME);
        client.readResultsFromEndpoint();

    }

    /**
     * Einlesen der Datei "races.csv" und Speichern der Objekte in der Tabelle F1_RACE
     *
     * @param racesFileName
     */
    private void readRacesFromFile(String racesFileName) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(getClass()
                            .getResourceAsStream("/" + racesFileName),
                            StandardCharsets.UTF_8));
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(";");

                Race race = new Race();
                race.setId(Long.parseLong(row[0]));
                race.setCountry(row[1]);
                race.setDate(LocalDate.parse(row[2], dtf));
                em.persist(race);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Einlesen der Datei "teams.csv".
     * Das String-Array jeder einzelnen Zeile wird der Methode persistTeamAndDrivers(...)
     * übergeben
     *
     * @param teamFileName
     */
    private void readTeamsAndDriversFromFile(String teamFileName) {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(getClass()
                            .getResourceAsStream("/" + teamFileName),
                            StandardCharsets.UTF_8));
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(";");

                Team team = new Team();
                Driver driver1 = new Driver();
                Driver driver2 = new Driver();

                team.setName(row[0]);
                em.persist(team);
                team = em.find(Team.class, team.getId());

                driver1.setName(row[1]);
                driver1.setTeam(team);
                driver2.setName(row[2]);
                driver2.setTeam(team);

                em.persist(driver1);
                em.persist(driver2);


                persistTeamAndDrivers(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Es wird überprüft ob es das übergebene Team schon in der Tabelle F1_TEAM gibt.
     * Falls nicht, wird das Team in der Tabelle gespeichert.
     * Wenn es das Team schon gibt, dann liest man das Team aus der Tabelle und
     * erstellt ein Objekt (der Klasse Team).
     * Dieses Objekt wird verwendet, um die Fahrer mit Ihrem jeweiligen Team
     * in der Tabelle F!_DRIVER zu speichern.
     *
     * @param line String-Array mit den einzelnen Werten der csv-Datei
     */

    private void persistTeamAndDrivers(String[] line) {

    }


}
