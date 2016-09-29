package sample.dto;

/**
 * Created by ducdmse61486 on 9/29/2016.
 */

public class BusRoute {
    private int id;
    private String code;
    private String name;
    private String tickets;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTickets() {
        return tickets;
    }

    public void setTickets(String tickets) {
        this.tickets = tickets;
    }
}
