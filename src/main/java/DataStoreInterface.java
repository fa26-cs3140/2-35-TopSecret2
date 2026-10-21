import java.util.List;

public interface DataStoreInterface {
    List<Mission> listMissions();

    Mission getMission(int id);


    void initDatabase();

    void importDatasetTsv(String fileName, String table);


    /**
     * adds a new mission to DB
     *
     * @param mission
     */
    Mission addMission(Mission mission);

    /**
     * query database to get all agents and return them as {@code List<Agent>}
     *
     * @return List<Agent>
     */
    List<Agent> listAgents();

    /**
     * query database to get all facilites and return them as {@code List<Facility>}
     *
     * @return List<Facility>
     */
    List<Facility> listFacilities();

    /**
     * query database to get all Briefs and return them as {@code List<Brief>}
     *
     * @return List<Brief>
     */
    List<Brief> listBriefs();

    /**
     * get an agent from database by id
     *
     * @param id the id of the agent to get
     * @return Agent object representing the queried Agent null if no agent found
     */
    Agent getAgent(int id);

    /**
     * get a Facility from database by id
     *
     * @param id the id of the Facility to get
     * @return Facility object representing the queried Facility null if no Facility found
     */
    Facility getFacility(int id);

    /**
     * get a Brief from database by id
     *
     * @param id the id of the Brief to get
     * @return Brief object representing the queried Brief null if no Brief found
     */
    Brief getBrief(int id);

    /**
     * adds an Agent to the database, the agentID will be ignored and automatically assigned by the database
     *
     * @param agent
     * @return the final Agent representation in the database
     */
    Agent addAgent(Agent agent);

    /**
     * add a facility to the database, the facilityID will be ignored and automatically assigned by the database
     *
     * @param facility
     * @return the final Facility entry representation in the database
     */
    Facility addFacility(Facility facility);

    /**
     * add a Brief to the database, the briefID will be ignored and automatically assigned by the database
     *
     * @param brief
     * @return the final Bief entry representation in the database
     */
    Brief addBrief(Brief brief);


    /**
     * gets the list of logs from database
     *
     * @return {@code List<LoggedAction>}
     */
    List<LoggedAction> listLogs();

    /**
     * return the list of logs for a given userID from the database
     *
     * @param userID user id of the desired user for users table in Database
     * @return {@code List<LoggedAction>}
     */
    List<LoggedAction> listLogs(int userID);

    /**
     * add a log to DB
     *
     * @param log
     */
    LoggedAction addLog(LoggedAction log);


    /**
     * get all users from database
     *
     * @return
     */
    List<User> listUsers();

    /**
     * get the user from database with the given user_ID
     *
     * @param userID
     * @return User or null if it does not exist
     */
    User getUser(int userID);

    /**
     * add a User to DB
     *
     * @param user
     */
    User addUser(User user);


}
