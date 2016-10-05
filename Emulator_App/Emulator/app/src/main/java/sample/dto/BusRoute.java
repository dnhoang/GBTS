package sample.dto;

/**
 * Created by ducdmse61486 on 9/29/2016.
 */

public class BusRoute {
    private String id;
    private String code;
    private String name;


    public String getId() {
        return id;
    }

    public BusRoute(String id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public BusRoute() {
    }

    public void setId(String id) {
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

}
