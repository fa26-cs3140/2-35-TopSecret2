package database;

import java.util.List;

public interface DataStoreInterface {
    //must delete this for now (view ERD for more info)
    List<Mission> listMissions();
    //must delete this for now (view ERD for more info)
    Mission getMission(int id);





    void initDatabase();
    void importTsv(String path);



    /**
     * query database to get all agents and return them as {@code List<Agent>}
     * @return List<Agent>
     */
    List<Agent> listAgents();

    /**
     * query database to get all facilites and return them as {@code List<Facility>}
     * @return List<Facility>
     */
    List<Facility> listFacilities();

    /**
     * query database to get all Briefs and return them as {@code List<Brief>}
     * @return List<Brief>
     */
    List<Brief> listBriefs();

    /**
     * get an agent from database by id
     * @param id the id of the agent to get
     * @return Agent object representing the queried Agent null if no agent found
     */
    Agent getAgent(int id);

    /**
     * get a Facility from database by id
     * @param id the id of the Facility to get
     * @return Facility object representing the queried Facility null if no Facility found
     */
    Facility getFacility(int id);

    /**
     * get a Brief from database by id
     * @param id the id of the Brief to get
     * @return Brief object representing the queried Brief null if no Brief found
     */
    Brief getBrief(int id);


    void addAgent(Agent agent);
    void addFacility(Facility facility);
    void addBrief(Brief brief);

}
