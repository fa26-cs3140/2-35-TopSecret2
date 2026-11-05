package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDataStore implements DataStoreInterface, AutoCloseable {


    private static final String DATABASE_URL = "jdbc:sqlite:data.db";

    private final String databaseUrl;
    private Connection connection;

    public SQLiteDataStore() {
        this(DATABASE_URL);
    }

    // Package-private constructor for tests using a temporary database.
    public SQLiteDataStore(String databaseUrl) {
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
                        facility_ID INTEGER NOT NULL REFERENCES facilities(facility_ID) ON DELETE CASCADE,
                        name TEXT NOT NULL,
                        date_of_birth TEXT NOT NULL,
                        date_of_death TEXT,
                        notes TEXT,
                        CHECK (
                            date_of_death IS NULL
                            OR date_of_death >= date_of_birth
                        )
                    )
                    """;
            String missionAgentsInitSql = """
                    CREATE TABLE IF NOT EXISTS mission_agents (
                        agent_ID INTEGER NOT NULL REFERENCES agents(agent_ID) ON DELETE CASCADE,
                        mission_ID INTEGER NOT NULL REFERENCES missions(mission_ID) ON DELETE CASCADE,
                        PRIMARY KEY (agent_ID, mission_ID)
                    )
                    """;

            String messagesInitSql = """
                    CREATE TABLE IF NOT EXISTS messages (
                        message_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        recipient_agent_ID INTEGER NOT NULL REFERENCES agents(agent_ID) ON DELETE CASCADE,
                        content TEXT NOT NULL
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
                statement.execute(missionAgentsInitSql);
                statement.execute(messagesInitSql);
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

        if (table.equals("agents")) {
            importAgentsTsvReader(reader, header);
            return;
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

    private void importAgentsTsvReader(BufferedReader reader, String header) throws IOException {
        String[] cols = header.strip().split("\t");

        if (cols.length != 4
                || !cols[0].equals("name")
                || !cols[1].equals("date_of_birth")
                || !cols[2].equals("facility_ID")
                || !cols[3].equals("mission_ID")) {
            throw new IllegalArgumentException("Invalid agents.tsv header.");
        }

        String agentSql = """
            INSERT INTO agents (name, date_of_birth, facility_ID)
            VALUES (?, ?, ?)
            """;

        String missionAgentSql = """
            INSERT INTO mission_agents (agent_ID, mission_ID)
            VALUES (?, ?)
            """;

        boolean originalAutoCommit;

        try {
            originalAutoCommit = connection.getAutoCommit();
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to retrieve database auto-commit.", e);
        }

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement agentStatement =
                         connection.prepareStatement(
                                 agentSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement missionAgentStatement =
                         connection.prepareStatement(missionAgentSql)) {

                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }

                    String[] fields = line.split("\t", -1);

                    if (fields.length != 4) {
                        throw new IllegalArgumentException(
                                "Invalid agent TSV row: " + line);
                    }

                    for (String field : fields) {
                        if (field.isEmpty()) {
                            throw new IllegalArgumentException(
                                    "TSV row contains an empty field: " + line);
                        }
                    }

                    agentStatement.setString(1, fields[0]);
                    agentStatement.setString(2, fields[1]);
                    agentStatement.setInt(3, Integer.parseInt(fields[2]));

                    agentStatement.executeUpdate();

                    try (ResultSet keys = agentStatement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException(
                                    "Failed to retrieve generated agent ID");
                        }

                        int agentID = keys.getInt(1);

                        missionAgentStatement.setInt(1, agentID);
                        missionAgentStatement.setInt(2, Integer.parseInt(fields[3]));

                        missionAgentStatement.executeUpdate();
                    }
                }

                connection.commit();

            } catch (Exception e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to import agents TSV data.", e);
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                throw new IllegalStateException(
                        "Failed to restore database auto-commit.", e);
            }
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
     * @return the created model.Mission
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
                    "Failed to add mission. model.Facility may not exist", e);
        }
    }

    /**
     * updates an existing mission in DB, the missionID selects which row to overwrite
     *
     * @param mission
     * @return the final model.Mission representation in the database
     * @throws IllegalArgumentException when no mission with that id exists, or the facility does not exist
     */
    @Override
    public Mission updateMission(Mission mission) {
        return null;
    }


    /**
     * query database to get all agents and return them as {@code List<model.Agent>}
     *
     * @return List<model.Agent>
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
                Agent agent = new Agent(
                        rs.getInt("agent_ID"),
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
     * query database to get all facilites and return them as {@code List<model.Facility>}
     *
     * @return List<model.Facility>
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
     * query database to get all Briefs and return them as {@code List<model.Brief>}
     *
     * @return List<model.Brief>
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
     * get an model.Agent from database by id.
     *
     * @param id the id of the model.Agent to get
     * @return model.Agent object representing the queried model.Agent null if no model.Agent found
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
                    return new Agent(
                            rs.getInt("agent_ID"),
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
     * get a model.Facility from database by id
     *
     * @param id the id of the model.Facility to get
     * @return model.Facility object representing the queried model.Facility null if no model.Facility found
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
     * get a model.Brief from database by id
     *
     * @param id the id of the model.Brief to get
     * @return model.Brief object representing the queried model.Brief null if no model.Brief found
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
                            rs.getString("content")
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
     * adds an model.Agent to the database, the agentID will be ignored and automatically assigned by the database
     *
     * @param agent
     * @return the final model.Agent representation in the database
     * @throws IllegalArgumentException when mission or facility may not exist, or mission's facility does not match agent's facility
     */
    @Override
    public Agent addAgent(Agent agent) {
        String sql = """
            INSERT INTO agents (facility_ID, name,
                                date_of_birth, date_of_death, notes)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, agent.getFacilityID());
            pstmt.setString(2, agent.getName());
            pstmt.setString(3, agent.getDate_of_birth());
            pstmt.setString(4, agent.getDate_of_death());
            pstmt.setString(5, agent.getNotes());

            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Agent(
                            keys.getInt(1),
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
                    "Failed to add model.Agent. Facility may not exist.", e);
        }
    }

    /**
     * updates an existing model.Agent in the database, the agentID selects which row to overwrite
     *
     * @param agent
     * @return the final model.Agent representation in the database
     * @throws IllegalArgumentException when no agent with that id exists, or the facility does not exist
     */
    @Override
    public Agent updateAgent(Agent agent) {
        return null;
    }

    /**
     * add a facility to the database, the facilityID will be ignored and automatically assigned by the database
     *
     * @param facility
     * @return the final model.Facility entry representation in the database
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
                    "Failed to add facility. model.Facility name or abbreviation already exists", e);
        }
    }

    /**
     * updates an existing facility in the database, the facilityID selects which row to overwrite
     *
     * @param facility
     * @return the final model.Facility entry representation in the database
     * @throws IllegalArgumentException when no facility with that id exists
     */
    @Override
    public Facility updateFacility(Facility facility) {
        return null;
    }

    /**
     * add a model.Brief to the database, the briefID will be ignored and automatically assigned by the database
     *
     * @param brief
     * @return the final Bief entry representation in the database
     * @throws IllegalArgumentException when mission does not exist
     */
    @Override
    public Brief addBrief(Brief brief) {
        String sql = """
                INSERT INTO briefs (mission_ID, title, date, content)
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
                    "Failed to add model.Brief. model.Mission may not exist", e);
        }
    }

    /**
     * updates an existing model.Brief in the database, the briefID selects which row to overwrite
     *
     * @param brief
     * @return the final Brief entry representation in the database
     * @throws IllegalArgumentException when no brief with that id exists, or the mission does not exist
     */
    @Override
    public Brief updateBrief(Brief brief) {
        return null;
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
     * @return {@code List<model.LoggedAction>}
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
     * @return the created model.LoggedAction
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
                    "Failed to add log. model.User may not exist", e);
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
     * @return model.User or null if it does not exist
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
     * @return the created model.User with its generated ID
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
                    "Failed to add user. model.User may already exist.", e);
        }
    }

    /**
     * get a list of all missions in DB
     * @return {@code List<model.Mission>}
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
     * @return the model.Mission object
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
    @Override
    public void assignAgentToMission(int agentID, int missionID) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void unassignAgentFromMission(int agentID, int missionID) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Mission> listMissionsForAgent(int agentID) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Agent> listAgentsForMission(int missionID) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Brief> listBriefsForMission(int missionID) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Message addMessage(Message newMessage) {
        String sql = """
        INSERT INTO messages (recipient_agent_ID, content)
        VALUES (?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            Agent messageRecipient = newMessage.getRecipient();
            pstmt.setInt(1, messageRecipient.getAgentID());
            pstmt.setString(2, newMessage.getMessageContent());

            pstmt.executeUpdate();

            return newMessage;

        } catch (SQLException e) {
            throw new IllegalArgumentException(
                    "Unable to create message", e);
        }
    }

    @Override
    public List<Message> listMessagesForAgent(Agent recipient) {
        String sql = """
                SELECT * FROM messages
                WHERE recipient_agent_ID = ?
            """;

        List<Message> messages = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, recipient.getAgentID());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Message currMessage = new Message(
                            recipient,
                            rs.getString("content")
                    );

                    messages.add(currMessage);
                }
            }
        }
        catch (SQLException e) {
            throw new IllegalStateException("Failed to retrieve messages", e);
        }

        return messages;

        }
}