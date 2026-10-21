package client.ihmshare;

/**
 * Core of the IHM Share component. Keeps the interfaces used to call the other
 * components (Communication, IHM Main, Data) and the implementations of the
 * interfaces exposed to these components.
 *
 * @author IHM-Share
 * @version 1.0
 */
public class IHMShareCore {

    private Interface_IHMShare_Calls_Communication interface_from_communication;
    private Interface_IHMShare_Calls_IHMMain interface_from_ihm_main;
    private Interface_IHMShare_Calls_Data interface_from_data;

    private CommunicationCallsIHMShareImpl impl_interface_for_communication;
    private IHMMainCallsIHMShareImpl impl_interface_for_ihm_main;
    private DataCallsIHMShareImpl impl_interface_for_data;

    /**
     * Class constructor. Creates the implementations of the interfaces exposed
     * to the other components, each one keeping a pointer to this object
     */
    public IHMShareCore() {
        this.impl_interface_for_communication = new CommunicationCallsIHMShareImpl(this);
        this.impl_interface_for_ihm_main = new IHMMainCallsIHMShareImpl(this);
        this.impl_interface_for_data = new DataCallsIHMShareImpl(this);
    }

    /**
     * Set the interface used by IHM Share to call the Communication component
     *
     * @param I (InterfaceIHMShareCallsCommunication) interface to the Communication component
     */
    public void setInterfaceFromCommunication(Interface_IHMShare_Calls_Communication I) {
        this.interface_from_communication = I;
    }

    /**
     * Set the interface used by IHM Share to call the IHM Main component
     *
     * @param I (InterfaceIHMShareCallsIHMMain) interface to the IHM Main component
     */
    public void setInterfaceFromIHMMain(Interface_IHMShare_Calls_IHMMain I) {
        this.interface_from_ihm_main = I;
    }

    /**
     * Set the interface used by IHM Share to call the Data component
     *
     * @param I (InterfaceIHMShareCallsData) interface to the Data component
     */
    public void setInterfaceFromData(Interface_IHMShare_Calls_Data I) {
        this.interface_from_data = I;
    }

    /**
     * Get the interface used by IHM Share to call the Communication component
     *
     * @return (InterfaceIHMShareCallsCommunication) interface to the Communication component
     */
    public Interface_IHMShare_Calls_Communication getInterfaceFromCommunication() {
        return this.interface_from_communication;
    }

    /**
     * Get the interface used by IHM Share to call the IHM Main component
     *
     * @return (InterfaceIHMShareCallsIHMMain) interface to the IHM Main component
     */
    public Interface_IHMShare_Calls_IHMMain getInterfaceFromIHMMain() {
        return this.interface_from_ihm_main;
    }

    /**
     * Get the interface used by IHM Share to call the Data component
     *
     * @return (InterfaceIHMShareCallsData) interface to the Data component
     */
    public Interface_IHMShare_Calls_Data getInterfaceFromData() {
        return this.interface_from_data;
    }

    /**
     * Set the implementation of the interface exposed to the Communication component
     *
     * @param I (CommunicationCallsIHMShareImpl) implementation used by the Communication component
     */
    public void setInterfaceForCommunication(CommunicationCallsIHMShareImpl I) {
        this.impl_interface_for_communication = I;
    }

    /**
     * Set the implementation of the interface exposed to the IHM Main component
     *
     * @param I (IHMMainCallsIHMShareImpl) implementation used by the IHM Main component
     */
    public void setInterfaceForIHMMain(IHMMainCallsIHMShareImpl I) {
        this.impl_interface_for_ihm_main = I;
    }

    /**
     * Set the implementation of the interface exposed to the Data component
     *
     * @param I (DataCallsIHMShareImpl) implementation used by the Data component
     */
    public void setInterfaceForData(DataCallsIHMShareImpl I) {
        this.impl_interface_for_data = I;
    }

    /**
     * Get the implementation of the interface exposed to the Communication component
     *
     * @return (CommunicationCallsIHMShareImpl) implementation used by the Communication component
     */
    public CommunicationCallsIHMShareImpl getInterfaceForCommunication() {
        return this.impl_interface_for_communication;
    }

    /**
     * Get the implementation of the interface exposed to the IHM Main component
     *
     * @return (IHMMainCallsIHMShareImpl) implementation used by the IHM Main component
     */
    public IHMMainCallsIHMShareImpl getInterfaceForIHMMain() {
        return this.impl_interface_for_ihm_main;
    }

    /**
     * Get the implementation of the interface exposed to the Data component
     *
     * @return (DataCallsIHMShareImpl) implementation used by the Data component
     */
    public DataCallsIHMShareImpl getInterfaceForData() {
        return this.impl_interface_for_data;
    }
}