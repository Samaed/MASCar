package sma.Agents.Behaviors.Lights;

import java.io.Serializable;

public enum MessagesTypes implements Serializable {
    SEND_STATE,
    GET_STATE,
    GET_NB_CARS,
    COMP_NB_CARS;
}
