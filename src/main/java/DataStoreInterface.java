import java.util.List;

public interface DataStoreInterface {
    List<Mission> listMissions();

    Mission getMission(int id);

    void initDatabase();

    void importTsv(String path);
}
