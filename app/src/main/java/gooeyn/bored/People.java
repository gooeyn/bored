package gooeyn.bored;

public class People {

    String name;
    String profile;
    String id;

    public People(String name, String profile, String id)
    {
        this.name = name;
        this.profile = profile;
        this.id = id;
    }

    public String getName()
    {
        return name;
    }
    public String getId()
    {
        return id;
    }

    public void setProfile(String profile)
    {
        this.profile = profile;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public void setId(String id)
    {
        this.id = id;
    }

}
