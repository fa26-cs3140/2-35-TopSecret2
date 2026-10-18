import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteDataStore implements DataStoreInterface, AutoCloseable {


    private static final String DATABASE_URL = "jdbc:sqlite:data.db";
    private static final String TSV_RESOURCE = "dataset/mission_briefs.tsv";

    private final String databaseUrl;
    private Connection connection;

    public SQLiteDataStore() {
        this(DATABASE_URL);
    }

    // Package-private constructor for tests using a temporary database.
    SQLiteDataStore(String databaseUrl) {
        this.databaseUrl = databaseUrl;
        initDatabase();
//        importDefaultTsvIfEmpty(); // delete this

        importDatasetTsv("facilities.tsv", "facilities", "name", "abbreviation");
        importDatasetTsv("missions.tsv", "missions", "facility_ID", "title");
        importDatasetTsv("briefs.tsv", "briefs", "mission_ID", "title", "date", "content");
        importDatasetTsv("agents.tsv", "agents", "mission_ID", "facility_ID", "name", "date_of_birth", "date_of_death", "notes");
    }

    @Override
    public void initDatabase() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(databaseUrl);
            }

//            String sql = """
//                    CREATE TABLE IF NOT EXISTS missions (
//                        id INTEGER PRIMARY KEY AUTOINCREMENT,
//                        title TEXT NOT NULL,
//                        date TEXT NOT NULL,
//                        brief TEXT NOT NULL
//                    )
//                    """;

            String facilitiesInitSql = """
                    CREATE TABLE IF NOT EXISTS facilities (
                        facility_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        abbreviation TEXT NOT NULL
                    )
                    """;

            String missionsInitSql = """
                    CREATE TABLE IF NOT EXISTS missions (
                        mission_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        facility_ID INTEGER NOT NULL REFERENCES facilities(facility_ID) ON DELETE CASCADE,
                        title TEXT NOT NULL,
                        UNIQUE (mission_ID, facility_ID)
                    )
                    """;

            String briefsInitSql = """
                    CREATE TABLE IF NOT EXISTS briefs (
                        brief_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        mission_ID INTEGER NOT NULL REFERENCES missions(mission_ID) ON DELETE CASCADE,
                        title TEXT NOT NULL,
                        date TEXT NOT NULL,
                        content TEXT NOT NULL
                    )
                    """;

            String agentsInitSql = """
                    CREATE TABLE IF NOT EXISTS agents (
                        agent_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        mission_ID INTEGER REFERENCES missions(mission_ID) ON DELETE SET NULL,
                        facility_ID INTEGER NOT NULL REFERENCES facilities(facility_ID) ON DELETE CASCADE,
                        name TEXT NOT NULL,
                        date_of_birth TEXT NOT NULL,
                        date_of_death TEXT,
                        notes TEXT,
                        FOREIGN KEY (mission_ID, facility_ID)
                                REFERENCES missions(mission_ID, facility_ID),
                        CHECK (
                            date_of_death IS NULL
                            OR date_of_death >= date_of_birth
                        )
                    )
                    """;

                    //use this if 2 foreign key constraint is not working for mission_ID
//            String clearAgentMissionTriggerSql = """
//                CREATE TRIGGER IF NOT EXISTS clear_agent_mission_on_delete
//                BEFORE DELETE ON missions
//                FOR EACH ROW
//                BEGIN
//                    UPDATE agents
//                    SET mission_ID = NULL
//                    WHERE mission_ID = OLD.mission_ID;
//                END;
//                """;


            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");

//                statement.execute(sql);
                statement.execute(facilitiesInitSql);
                statement.execute(missionsInitSql);
                statement.execute(briefsInitSql);
                statement.execute(agentsInitSql);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to initialize the data database.", e);
        }
    }

    @Override
    public void importTsv(String path) {
        try (BufferedReader reader = Files.newBufferedReader(
                Path.of(path), StandardCharsets.UTF_8)) {

            importTsvReader(reader);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to import missions from " + path, e);
        }
    }

    public void importDefaultTsv() {
        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(TSV_RESOURCE);

        if (input == null) {
            throw new IllegalStateException(
                    "Could not find resource: " + TSV_RESOURCE);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8))) {

            importTsvReader(reader);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to import the default mission TSV.", e);
        }
    }

    public void importDatasetTsv(String fileName, String table, String... cols){
        //checking if table is empty. if empty, import tsv
        String sql = "SELECT COUNT(*) FROM " +  table;
        try (Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(sql)) {

            if (!(results.next() && results.getInt(1) == 0)) {
                return;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to check the mission database.", e);
        }


        InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("dataset/" + fileName);

        if (input == null) {
            throw new IllegalStateException(
                    "Could not find resource: dataset/" + fileName);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8))) {

        importTsvReader(reader, table, cols);


        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to import the default tsv " + fileName, e);
        }
    }



    //replaced by importDatasetTsv
//    public void importFacilitiesTsv(){
//        InputStream input = getClass()
//                .getClassLoader()
//                .getResourceAsStream("dataset/mission_briefs.tsv");
//
//        if (input == null) {
//            throw new IllegalStateException(
//                    "Could not find resource: dataset/mission_briefs.tsv");
//        }
//
//        try (BufferedReader reader = new BufferedReader(
//                new InputStreamReader(input, StandardCharsets.UTF_8))) {
//
//
//
//
//        } catch (IOException e) {
//            throw new IllegalStateException(
//                    "Failed to import the default Facilities TSV.", e);
//        }
//    }




    // replaced by importDatasetTsv ----> delete
    private void importDefaultTsvIfEmpty() {
        String sql = "SELECT COUNT(*) FROM missions";

        try (Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(sql)) {

            if (results.next() && results.getInt(1) == 0) {
                importDefaultTsv();
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to check the mission database.", e);
        }
    }


    private void importTsvReader(BufferedReader reader, String table, String... cols) throws IOException {
        String header = reader.readLine();

        if (header == null) {
            throw new IllegalArgumentException("The TSV file is empty.");
        }

        String insertSql = """
            INSERT INTO %s (%s)
            VALUES (%s)
            """.formatted(
                table,
                String.join(", ", cols),
                String.join(", ", java.util.Collections.nCopies(cols.length, "?"))
        );

        try {
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement statement =
                         connection.prepareStatement(insertSql)) {

                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }

                    String[] fields = line.split("\t", cols.length);

                    //delete this
//                    if (fields.length != cols.length) {
//                        throw new IllegalArgumentException(
//                                "Invalid TSV row: " + line);
//                    }


                    for (String field : fields) {
                        if (field.isEmpty()){
                            throw new IllegalArgumentException(
                                    "TSV row contains an empty field: " + line);
                        }
                    }

                    for (int i = 1; i <= fields.length; i++) {
                        statement.setString(i, fields[i - 1]);
                    }

                    if (fields.length< cols.length) {
                        for (int i = fields.length+1; i <= cols.length; i++) {
                            statement.setString(i, null);
                        }
                    }

                    statement.executeUpdate();
                }

                connection.commit();

            } catch (Exception e) {
                connection.rollback();
                throw e;

            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to import TSV data into the database.", e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to import TSV data.", e);
        }
    }

    //replaced by importTsvReader(BufferedReader reader, String table, String... cols)  ---------> delete
    private void importTsvReader(BufferedReader reader) throws IOException {
        String header = reader.readLine();

        if (header == null) {
            throw new IllegalArgumentException("The TSV file is empty.");
        }

        String insertSql = """
                INSERT INTO missions (title, date, brief)
                VALUES (?, ?, ?)
                """;

        try {
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement statement =
                         connection.prepareStatement(insertSql)) {

                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }

                    String[] fields = line.split("\t", 3);

                    if (fields.length != 3) {
                        throw new IllegalArgumentException(
                                "Invalid TSV row: " + line);
                    }

                    String title = fields[0].trim();
                    String date = fields[1].trim();
                    String brief = fields[2].trim();

                    if (title.isEmpty() || date.isEmpty() || brief.isEmpty()) {
                        throw new IllegalArgumentException(
                                "TSV row contains an empty field: " + line);
                    }

                    statement.setString(1, title);
                    statement.setString(2, date);
                    statement.setString(3, brief);
                    statement.executeUpdate();
                }

                connection.commit();

            } catch (Exception e) {
                connection.rollback();
                throw e;

            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to import TSV data into the database.", e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to import TSV data.", e);
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to close the database connection.", e);
        }
    }


    /**
     * query database to get all agents and return them as {@code List<Agent>}
     *
     * @return List<Agent>
     */
    @Override
    public List<Agent> listAgents() {
        return List.of();
    }

    /**
     * query database to get all facilites and return them as {@code List<Facility>}
     *
     * @return List<Facility>
     */
    @Override
    public List<Facility> listFacilities() {
        return List.of();
    }

    /**
     * query database to get all Briefs and return them as {@code List<Brief>}
     *
     * @return List<Brief>
     */
    @Override
    public List<Brief> listBriefs() {
        return List.of();
    }

    /**
     * get an Agent from database by id
     *
     * @param id the id of the Agent to get
     * @return Agent object representing the queried Agent null if no Agent found
     */
    @Override
    public Agent getAgent(int id) {
        return null;
    }

    /**
     * get a Facility from database by id
     *
     * @param id the id of the Facility to get
     * @return Facility object representing the queried Facility null if no Facility found
     */
    @Override
    public Facility getFacility(int id) {
        return null;
    }

    /**
     * get a Brief from database by id
     *
     * @param id the id of the Brief to get
     * @return Brief object representing the queried Brief null if no Brief found
     */
    @Override
    public Brief getBrief(int id) {
        return null;
    }

    /**
     * adds an Agent to the database, the agentID will be ignored and automatically assigned by the database
     *
     * @param agent
     * @return the final Agent representation in the database
     */
    @Override
    public Agent addAgent(Agent agent) {
        return null;
    }

    /**
     * add a facility to the database, the facilityID will be ignored and automatically assigned by the database
     *
     * @param facility
     * @return the final Facility entry representation in the database
     */
    @Override
    public Facility addFacility(Facility facility) {
        return null;
    }

    /**
     * add a Brief to the database, the briefID will be ignored and automatically assigned by the database
     *
     * @param brief
     * @return the final Bief entry representation in the database
     */
    @Override
    public Brief addBrief(Brief brief) {
        return null;
    }

    /**
     * gets the list of logs from database
     *
     * @return {@code List<Log>}
     */
    @Override
    public List<Log> listLogs() {
        return List.of();
    }

    @Override
    public List<Log> listLogs() {
        return List.of();
    }
    @Override
    public List<Mission> listMissions() {
        List<Mission> missions = new ArrayList<>();

        String sql = """
                SELECT id, title, date, brief
                FROM missions
                ORDER BY id
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                missions.add(new Mission(
                        results.getInt("id"),
                        results.getString("title"),
                        results.getString("date"),
                        results.getString("brief")
                ));
            }

            return missions;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to retrieve missions.", e);
        }
    }

    @Override
    public Mission getMission(int id) {
        String sql = """
                SELECT id, title, date, brief
                FROM missions
                WHERE id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return new Mission(
                            results.getInt("id"),
                            results.getString("title"),
                            results.getString("date"),
                            results.getString("brief")
                    );
                }

                return null;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to retrieve mission with ID " + id, e);
        }
    }

}