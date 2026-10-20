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

        importDatasetTsv("facilities.tsv", "facilities");
        importDatasetTsv("missions.tsv", "missions");
        importDatasetTsv("briefs.tsv", "briefs");
        importDatasetTsv("agents.tsv", "agents");
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

            String usersInitSql = """
                    CREATE TABLE IF NOT EXISTS users (
                        user_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT NOT NULL UNIQUE
                    )
                    """;

            String logsInitSql = """
                    CREATE TABLE IF NOT EXISTS logs (
                        log_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_ID INTEGER  NOT NULL REFERENCES users(user_ID),
                        action INTEGER NOT NULL,
                        record TEXT NOT NULL,
                        time TEXT NOT NULL
                    )
                    """;

            String facilitiesInitSql = """
                    CREATE TABLE IF NOT EXISTS facilities (
                        facility_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE,
                        abbreviation TEXT NOT NULL UNIQUE
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
                statement.execute(usersInitSql);
                statement.execute(logsInitSql);
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

    /**
     * adds a new mission to DB
     *
     * @param mission
     * @throws IllegalArgumentException when facility does not exist in DB
     */
    @Override
    public void addMission(Mission mission) {
        try {
            throw new SQLException();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to add mission. facility may not exist", e);
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

    public void importDatasetTsv(String fileName, String table) {
        //checking if table is empty. if empty, import tsv
        String sql = "SELECT COUNT(*) FROM " + table;
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

            importTsvReader(reader, table);


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


    private void importTsvReader(BufferedReader reader, String table) throws IOException {
        String header = reader.readLine();

        if (header == null) {
            throw new IllegalArgumentException("The TSV file is empty.");
        }
        String[] cols = header.split("\t");

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
                        if (field.isEmpty()) {
                            throw new IllegalArgumentException(
                                    "TSV row contains an empty field: " + line);
                        }
                    }

                    for (int i = 1; i <= fields.length; i++) {
                        statement.setString(i, fields[i - 1]);
                    }

                    if (fields.length < cols.length) {
                        for (int i = fields.length + 1; i <= cols.length; i++) {
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
     * @throws IllegalArgumentException when mission or facility may not exist, or mission's facility does not match agent's facility
     */
    @Override
    public Agent addAgent(Agent agent) {
        try {
            throw new SQLException();
        } catch (SQLException e) {
            throw new IllegalArgumentException("Failed to add Agent. mission or facility may not exist," +
                    " or mission's facility does not match agent's facility", e);
        }
    }

    /**
     * add a facility to the database, the facilityID will be ignored and automatically assigned by the database
     *
     * @param facility
     * @return the final Facility entry representation in the database
     * @throws IllegalArgumentException when facility name or abbreviation already exists
     */
    @Override
    public Facility addFacility(Facility facility) {
        try{
            throw  new SQLException();
        }catch (SQLException e){
            throw new IllegalArgumentException("Failed to add facility. facility name or abbreviation already exists", e);
        }
    }

    /**
     * add a Brief to the database, the briefID will be ignored and automatically assigned by the database
     *
     * @param brief
     * @return the final Bief entry representation in the database
     * @throws IllegalArgumentException when mission does not exist
     */
    @Override
    public Brief addBrief(Brief brief) {
        try {
            throw new SQLException();
        } catch (SQLException e) {
            throw new IllegalArgumentException("Failed to add Breif. mission may not exist", e);
        }
    }

    /**
     * gets the list of logs from database
     *
     * @return {@code List<Log>}
     */
    @Override
    public List<LoggedAction> listLogs() {
        List<LoggedAction> logs = new ArrayList<>();

        String sql = """
                SELECT * FROM logs
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                int userID = results.getInt("user_ID");
                User user = getUser(userID);

                logs.add(new LoggedAction(
//                        results.getInt("log_ID"),
                        results.getInt("action"),
                        user,
                        results.getString("time"),
                        results.getString("record")
                ));
            }

            return logs;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to retrieve missions.", e);
        }
    }

    /**
     * return the list of logs for a given userID from the database
     *
     * @param userID user id of the desired user for users table in Database
     * @return {@code List<LoggedAction>}
     */
    @Override
    public List<LoggedAction> listLogs(int userID) {
        String sql = """
                SELECT * FROM logs WHERE user_ID = ?;
                """;

        List<LoggedAction> logs = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = getUser(userID);

                    logs.add(new LoggedAction(
//                        results.getInt("log_ID"),
                            rs.getInt("action"),
                            user,
                            rs.getString("time"),
                            rs.getString("record")
                    ));
                }

                return logs;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user with ID: " + userID, e);
        }

    }

    /**
     * add a log to DB
     *
     * @param log
     * @throws IllegalArgumentException when user may not exist in DB
     */
    @Override
    public void addLog(LoggedAction log) {
        try {
            throw new SQLException();
        } catch (SQLException e) {
            throw new IllegalArgumentException("Failed to add log. User may not exist", e);
        }
    }

    /**
     * get all users from database
     *
     * @return
     */
    @Override
    public List<User> listUsers() {
        List<User> users = new ArrayList<>();

        String sql = """
                SELECT * FROM users
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("user_ID"),
                        rs.getString("username")
                );

                users.add(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving users from database", e);
        }

        return users;
    }

    /**
     * get the user from database with the given user_ID
     *
     * @param userID
     * @return User or null if it does not exist
     */
    @Override
    public User getUser(int userID) {
        String sql = """
                SELECT * FROM users WHERE user_ID = ?;
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_ID"),
                            rs.getString("username")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user with ID: " + userID, e);
        }

        return null;
    }

    /**
     * add a User to DB
     *
     * @param user
     * @throws IllegalArgumentException when user may already exist.
     */
    @Override
    public void addUser(User user) {
        try {
            throw new SQLException();
        } catch (SQLException e) {
            throw new IllegalArgumentException("Failed to add user. User may already exist.", e);
        }
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