package gooeyn.bored;

public class MyChat {

    private String name;
    private String picture;
    private String id;
    private String message;

    public MyChat(String name, String picture, String id, String message)
    {
        this.name = name;
        this.picture = picture;
        this.id = id;
        this.message = message;
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
    public String getMessage()
    {
        return this.message;
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
    public void setMessage(String message)
    {
        this.message = message;
    }

}
