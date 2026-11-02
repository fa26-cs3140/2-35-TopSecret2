package model;

import java.util.List;

public interface DataStoreInterface {
    List<Mission> listMissions();

    Mission getMission(int id);

    /**
     * assigns an agent to a mission (joint operations: an agent may be on many missions,
     * and may come from any facility)
     *
     * @param agentID   id of the agent to assign
     * @param missionID id of the mission to assign the agent to
     * @throws IllegalArgumentException when the agent or mission does not exist, or the agent is already assigned to the mission
     */
    void assignAgentToMission(int agentID, int missionID);

    /**
     * removes an agent's assignment to a mission
     *
     * @param agentID   id of the agent to unassign
     * @param missionID id of the mission to remove the agent from
     * @throws IllegalArgumentException when the agent is not assigned to the mission
     */
    void unassignAgentFromMission(int agentID, int missionID);

    /**
     * get every mission a given agent is assigned to
     *
     * @param agentID id of the agent
     * @return {@code List<model.Mission>}, empty if the agent has no missions
     */
    List<Mission> listMissionsForAgent(int agentID);

    /**
     * get every agent assigned to a given mission, regardless of the agent's facility
     *
     * @param missionID id of the mission
     * @return {@code List<model.Agent>}, empty if the mission has no agents
     */
    List<Agent> listAgentsForMission(int missionID);

    /**
     * get every brief belonging to a given mission
     *
     * @param missionID id of the mission
     * @return {@code List<model.Brief>}, empty if the mission has no briefs
     */
    List<Brief> listBriefsForMission(int missionID);


    void initDatabase();

    void importDatasetTsv(String fileName, String table);


    /**
     * adds a new mission to DB
     *
     * @param mission
     */
    Mission addMission(Mission mission);

    /**
     * query database to get all agents and return them as {@code List<model.Agent>}
     *
     * @return List<model.Agent>
     */
    List<Agent> listAgents();

    /**
     * query database to get all facilites and return them as {@code List<model.Facility>}
     *
     * @return List<model.Facility>
     */
    List<Facility> listFacilities();

    /**
     * query database to get all Briefs and return them as {@code List<model.Brief>}
     *
     * @return List<model.Brief>
     */
    List<Brief> listBriefs();

    /**
     * get an agent from database by id
     *
     * @param id the id of the agent to get
     * @return model.Agent object representing the queried model.Agent null if no agent found
     */
    Agent getAgent(int id);

    /**
     * get a model.Facility from database by id
     *
     * @param id the id of the model.Facility to get
     * @return model.Facility object representing the queried model.Facility null if no model.Facility found
     */
    Facility getFacility(int id);

    /**
     * get a model.Brief from database by id
     *
     * @param id the id of the model.Brief to get
     * @return model.Brief object representing the queried model.Brief null if no model.Brief found
     */
    Brief getBrief(int id);

    /**
     * adds an model.Agent to the database, the agentID will be ignored and automatically assigned by the database
     *
     * @param agent
     * @return the final model.Agent representation in the database
     */
    Agent addAgent(Agent agent);

    /**
     * add a facility to the database, the facilityID will be ignored and automatically assigned by the database
     *
     * @param facility
     * @return the final model.Facility entry representation in the database
     */
    Facility addFacility(Facility facility);

    /**
     * add a model.Brief to the database, the briefID will be ignored and automatically assigned by the database
     *
     * @param brief
     * @return the final Bief entry representation in the database
     */
    Brief addBrief(Brief brief);


    /**
     * gets the list of logs from database
     *
     * @return {@code List<model.LoggedAction>}
     */
    List<LoggedAction> listLogs();

    /**
     * return the list of logs for a given userID from the database
     *
     * @param userID user id of the desired user for users table in Database
     * @return {@code List<model.LoggedAction>}
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
     * @return model.User or null if it does not exist
     */
    User getUser(int userID);

    /**
     * add a model.User to DB
     *
     * @param user
     */
    User addUser(User user);


}
