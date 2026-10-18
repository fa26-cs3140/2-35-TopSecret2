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
import java.util.List;

public class SQLiteDataStore implements DataStoreInterface, AutoCloseable {

    private static final String DATABASE_URL = "jdbc:sqlite:missions.db";
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
        importDefaultTsvIfEmpty();
    }

    @Override
    public void initDatabase() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(databaseUrl);
            }

            String sql = """
                    CREATE TABLE IF NOT EXISTS missions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        date TEXT NOT NULL,
                        brief TEXT NOT NULL
                    )
                    """;

            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to initialize the mission database.", e);
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
}