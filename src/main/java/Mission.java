public class Mission {
    private int id;
    private String title;
    private String date;
    private String brief;

    public Mission(int id, String title, String date, String brief) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.brief = brief;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getBrief() {
        return brief;
    }
}
