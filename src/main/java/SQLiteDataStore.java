import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteDataStore implements DataStoreInterface, AutoCloseable {


    private static final String DATABASE_URL = "jdbc:sqlite:data.db";
//    private static final String TSV_RESOURCE = "dataset/mission_briefs.tsv";

    private final String databaseUrl;
    private Connection connection;

    public SQLiteDataStore() {
        this(DATABASE_URL);
    }

    // Package-private constructor for tests using a temporary database.
    SQLiteDataStore(String databaseUrl) {
        this.databaseUrl = databaseUrl;
        initDatabase();

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
     * adds a new mission to DB
     *
     * @param mission
     * @return the created Mission
     * @throws IllegalArgumentException when facility does not exist in DB
     */
    @Override
    public Mission addMission(Mission mission) {
        String sql = """
            INSERT INTO missions (title, facility_ID)
            VALUES (?, ?)
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, mission.getTitle());
            pstmt.setInt(2, mission.getFacilityID());

            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Mission(
                            keys.getInt(1),
                            mission.getTitle(),
                            mission.getFacilityID()
                    );
                }
            }

            throw new SQLException("Failed to retrieve generated mission ID");

        } catch (SQLException e) {
            throw new IllegalArgumentException(
                    "Failed to add mission. Facility may not exist", e);
        }
    }


    /**
     * query database to get all agents and return them as {@code List<Agent>}
     *
     * @return List<Agent>
     */
    @Override
    public List<Agent> listAgents() {
        String sql = """
                SELECT * FROM agents
                """;

        List<Agent> agents = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Integer missionIDValue = rs.getInt("mission_ID");
                missionIDValue = rs.wasNull() ? null : missionIDValue;
                Agent agent = new Agent(
                        rs.getInt("agent_ID"),
                        missionIDValue,
                        rs.getInt("facility_ID"),
                        rs.getString("name"),
                        rs.getString("date_of_birth"),
                        rs.getString("date_of_death"),
                        rs.getString("notes")
                );

                agents.add(agent);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to retrieve agents", e);
        }

        return agents;
    }

    /**
     * query database to get all facilites and return them as {@code List<Facility>}
     *
     * @return List<Facility>
     */
    @Override
    public List<Facility> listFacilities() {
        String sql = """
                SELECT * FROM facilities
                """;

        List<Facility> facilities = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Facility facility = new Facility(
                        rs.getInt("facility_ID"),
                        rs.getString("name"),
                        rs.getString("abbreviation")
                );

                facilities.add(facility);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to retrieve facilities", e);
        }

        return facilities;
    }

    /**
     * query database to get all Briefs and return them as {@code List<Brief>}
     *
     * @return List<Brief>
     */
    @Override
    public List<Brief> listBriefs() {
        String sql = """
                SELECT * FROM briefs
                """;

        List<Brief> briefs = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Brief brief = new Brief(
                        rs.getInt("brief_ID"),
                        rs.getInt("mission_ID"),
                        rs.getString("title"),
                        rs.getString("date"),
                        rs.getString("content")
                );

                briefs.add(brief);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to retrieve briefs", e);
        }

        return briefs;
    }


    /**
     * get an Agent from database by id.
     *
     * @param id the id of the Agent to get
     * @return Agent object representing the queried Agent null if no Agent found
     *
     */
    @Override
    public Agent getAgent(int id) {
        String sql = """
                SELECT * FROM agents
                WHERE agent_ID = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int missionIDValue = rs.getInt("mission_ID");
                    Integer missionID = rs.wasNull() ? null : missionIDValue;

                    return new Agent(
                            rs.getInt("agent_ID"),
                            missionID,
                            rs.getInt("facility_ID"),
                            rs.getString("name"),
                            rs.getString("date_of_birth"),
                            rs.getString("date_of_death"),
                            rs.getString("notes")
                    );
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to retrieve agent with ID: " + id, e);
        }

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
        String sql = """
                SELECT * FROM facilities
                WHERE facility_ID = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Facility(
                            rs.getInt("facility_ID"),
                            rs.getString("name"),
                            rs.getString("abbreviation")
                    );
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to retrieve facility with ID: " + id, e);
        }

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
        String sql = """
                SELECT * FROM briefs
                WHERE brief_ID = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Brief(
                            rs.getInt("brief_ID"),
                            rs.getInt("mission_ID"),
                            rs.getString("title"),
                            rs.getString("date"),
                            rs.getString("text")
                    );
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to retrieve brief with ID: " + id, e);
        }

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
        String sql = """
                INSERT INTO agents (mission_ID, facility_ID, name,
                                    date_of_birth, date_of_death, notes)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setObject(1, agent.getMissionID(), Types.INTEGER);
            pstmt.setInt(2, agent.getFacilityID());
            pstmt.setString(3, agent.getName());
            pstmt.setString(4, agent.getDate_of_birth());
            pstmt.setString(5, agent.getDate_of_death());
            pstmt.setString(6, agent.getNotes());

            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Agent(
                            keys.getInt(1),
                            agent.getMissionID(),
                            agent.getFacilityID(),
                            agent.getName(),
                            agent.getDate_of_birth(),
                            agent.getDate_of_death(),
                            agent.getNotes()
                    );
                }
            }

            throw new SQLException("Failed to retrieve generated agent ID");

        } catch (SQLException e) {
            throw new IllegalArgumentException(
                    "Failed to add Agent. Mission or facility may not exist, "
                            + "or mission's facility does not match agent's facility", e);
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
        String sql = """
                INSERT INTO facilities (name, abbreviation)
                VALUES (?, ?)
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, facility.getName());
            pstmt.setString(2, facility.getAbbreviation());

            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Facility(
                            keys.getInt(1),
                            facility.getName(),
                            facility.getAbbreviation()
                    );
                }
            }

            throw new SQLException("Failed to retrieve generated facility ID");

        } catch (SQLException e) {
            throw new IllegalArgumentException(
                    "Failed to add facility. Facility name or abbreviation already exists", e);
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
        String sql = """
                INSERT INTO briefs (mission_ID, title, date, text)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, brief.getMissionID());
            pstmt.setString(2, brief.getTitle());
            pstmt.setString(3, brief.getDate());
            pstmt.setString(4, brief.getContent());

            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Brief(
                            keys.getInt(1),
                            brief.getMissionID(),
                            brief.getTitle(),
                            brief.getDate(),
                            brief.getContent()
                    );
                }
            }

            throw new SQLException("Failed to retrieve generated brief ID");

        } catch (SQLException e) {
            throw new IllegalArgumentException(
                    "Failed to add Brief. Mission may not exist", e);
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
     * Adds a log to the database.
     *
     * @param log the log to add
     * @return the created LoggedAction
     * @throws IllegalArgumentException when the user may not exist in the database
     */
    @Override
    public LoggedAction addLog(LoggedAction log) {
        String sql = """
        INSERT INTO logs (user_ID, action, record, time)
        VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, log.getUser().getUserID());
            pstmt.setInt(2, log.getAction());
            pstmt.setString(3, log.getRecord());
            pstmt.setString(4, log.getDateTime());

            pstmt.executeUpdate();

            return log;

        } catch (SQLException e) {
            throw new IllegalArgumentException(
                    "Failed to add log. User may not exist", e);
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
     * Adds a user to the database.
     *
     * @param user the user to add
     * @return the created User with its generated ID
     * @throws IllegalArgumentException when the user may already exist
     */
    @Override
    public User addUser(User user) {
        String sql = """
        INSERT INTO users (username)
        VALUES (?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new User(
                            keys.getInt(1),
                            user.getUsername()
                    );
                }
            }

            throw new SQLException("Failed to retrieve generated user ID");

        } catch (SQLException e) {
            throw new IllegalArgumentException(
                    "Failed to add user. User may already exist.", e);
        }
    }

    /**
     * get a list of all missions in DB
     * @return {@code List<Mission>}
     */
    @Override
    public List<Mission> listMissions() {
        String sql = """
            SELECT * FROM missions
            """;

        List<Mission> missions = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Mission mission = new Mission(
                        rs.getInt("mission_ID"),
                        rs.getString("title"),
                        rs.getInt("facility_ID")
                );

                missions.add(mission);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to retrieve missions", e);
        }

        return missions;
    }

    /**
     * get the desired mission from DB based on its mission_ID
     * @param id id of the mission
     * @return the Mission object
     */
    @Override
    public Mission getMission(int id) {
        String sql = """
            SELECT * FROM missions
            WHERE mission_ID = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Mission(
                            rs.getInt("mission_ID"),
                            rs.getString("title"),
                            rs.getInt("facility_ID")
                    );
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to retrieve mission with ID: " + id, e);
        }

        return null;
    }

}