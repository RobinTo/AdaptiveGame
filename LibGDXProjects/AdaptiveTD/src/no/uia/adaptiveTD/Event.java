package no.uia.adaptiveTD;

import com.badlogic.gdx.math.Vector2;

public class Event {

    EventType type;
    public EventType getType()
    {
        return type;
    }
    Vector2 tilePosition;
    public Vector2 getTilePosition()
    {
        return tilePosition;
    }
    String towerType;
    public String getTowerType()
    {
        return towerType;
    }

    public Event(EventType type, Vector2 tilePosition, String towerType)
    {
        this.type = type;
        this.tilePosition = tilePosition;
        this.towerType = towerType;
    }

    public String SaveString()
    {
        return "e:"+ type.name() + ":" + towerType + ":" + tilePosition.x + ":" + tilePosition.y;
    }
}
