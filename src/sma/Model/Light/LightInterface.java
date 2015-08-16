package sma.Model.Light;

public interface LightInterface {
    public void setLightState(State state);

    public Model getModel();

    public boolean isWaiting();
    public void setWaiting(boolean waiting);

    public boolean isReadyConvers();

    public void setReadyConvers(boolean readyConvers);

    public boolean isLastGreen();
    public void setLastGreen(boolean lastGreen);
}
