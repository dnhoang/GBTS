package sample.dto;

/**
 * Created by ducdmse61486 on 10/21/2016.
 */

public class OfflineTicket {
    private Integer id;
    private String cardid;
    private String tickettypeid;
    private String routecode;
    private String boughtdate;

    public OfflineTicket() {
    }


    public OfflineTicket(Integer id, String cardid, String tickettypeid, String routecode, String boughtdate) {
        this.id = id;
        this.cardid = cardid;
        this.tickettypeid = tickettypeid;
        this.routecode = routecode;
        this.boughtdate = boughtdate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCardid() {
        return cardid;
    }

    @Override
    public String toString() {
        return "OfflineTicket{" +
                "id=" + id +
                ", cardid='" + cardid + '\'' +
                ", tickettypeid='" + tickettypeid + '\'' +
                ", routecode='" + routecode + '\'' +
                ", boughtdate='" + boughtdate + '\'' +
                '}';
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public String getTickettypeid() {
        return tickettypeid;
    }

    public void setTickettypeid(String tickettypeid) {
        this.tickettypeid = tickettypeid;
    }

    public String getRoutecode() {
        return routecode;
    }

    public void setRoutecode(String routecode) {
        this.routecode = routecode;
    }

    public String getBoughtdate() {
        return boughtdate;
    }

    public void setBoughtdate(String boughtdate) {
        this.boughtdate = boughtdate;
    }
}
