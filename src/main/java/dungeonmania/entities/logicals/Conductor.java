package dungeonmania.entities.logicals;

public interface Conductor {
    public boolean isActivated();

    public int getTickActivated();

    public void setTickActivated(int tick);
}
