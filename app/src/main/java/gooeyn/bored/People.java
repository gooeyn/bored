package gooeyn.bored;

public class People {

    private String name;
    private String picture;
    private String id;
    private String status;

    public People(String name, String picture, String id, String status)
    {
        this.name = name;
        this.picture = picture;
        this.id = id;
        this.status = status;
    }

    /*
    GETTERS
     */
    public String getName()
    {
        return this.name;
    }
    public String getPicture()
    {
        return this.picture;
    }
    public String getId()
    {
        return this.id;
    }
    public String getStatus()
    {
        return this.status;
    }

    /*
    SETTERS
     */
    public void setName(String name)
    {
        this.name = name;
    }
    public void setPicture(String picture)
    {
        this.picture = picture;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }
}
