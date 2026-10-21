package client.ihmshare;

/**
 * Implementation of the Data calls IHM Share Interface
 *
 * @author IHM-Share 
 * @version 1.0
 */
public class DataCallsIHMShareImpl implements Interface_Data_Calls_IHMShare {

    private IHMShareCore core;

    /**
     * Class constructor. Keep a pointer to IHMShareCore object
     * 
     * @param core (IHMShareCore) pointer to the parent object
     */
    public DataCallsIHMShareImpl(IHMShareCore core) {
        this.core = core;
    }
}