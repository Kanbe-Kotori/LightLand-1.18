package dev.xkmc.lightland.content.magic.common;

public class OrientalElement {

    private final String _name;

    private OrientalElement(String name) {
        this._name = name;
    }

    public static final OrientalElement GOLD = new OrientalElement("GOLD");
    public static final OrientalElement WOOD = new OrientalElement("WOOD");
    public static final OrientalElement EARTH = new OrientalElement("EARTH");
    public static final OrientalElement WATER = new OrientalElement("WATER");
    public static final OrientalElement FIRE = new OrientalElement("FIRE");
    public static final OrientalElement SUN = new OrientalElement("SUN");
    public static final OrientalElement MOON = new OrientalElement("MOON");
}
