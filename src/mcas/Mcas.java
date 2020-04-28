package mcas;

public class Mcas
{
    // Define enumerations for return values (commands)
    public enum Command
    {
        NONE, DOWN
    };

    // Declare enumerations for internal state
    // FOR TESTING: changed from private to public
    public enum State
    {
        INACTIVE, ARMED, ACTIVE
    };

    // Define the threshold AOA for activating, in degrees nose-up
    private final double AOA_THRESHOLD          = 12.0;

    // Define the minimum time delay between activations, in milliseconds
    private final long   ACTIVATION_INTERVAL_MS = 10000;

    // Declare member variables
    private State        state;
    private long         lastActivationTimeMs;

    // Constructor
    public Mcas()
    {
        state = State.INACTIVE; // safest starting state
        lastActivationTimeMs = 0; // arbitrarily long ago
    }

    // Method to control trim
    public Command trim(boolean autopilotOn, boolean flapsDown,
            double angleOfAttack)
    {
        // Initialize command to NONE
        Command command = Command.NONE;

        // Are we currently INACTIVE and conditions are such (autopilot off and
        // flaps up) that we should become ARMED?
        // decision D1
        if ((state == State.INACTIVE) && !autopilotOn && !flapsDown)
        {
            state = State.ARMED;
        }

        // Are we currently ARMED and conditions are such (autopilot on or flaps
        // down) that we should become INACTIVE?
        // decision D2
        if ((state == State.ARMED) && (autopilotOn || flapsDown))
        {
            state = State.INACTIVE;
        }

        // Are we currently ARMED and conditions are such (angle of attack is
        // too high) that we should be ACTIVE and generate a trim DOWN command?
        // decision D3
        if ((state == State.ARMED) && (angleOfAttack > AOA_THRESHOLD))
        {
            state = State.ACTIVE;
            command = Command.DOWN;
            lastActivationTimeMs = System.currentTimeMillis();
        }

        // Are we currently ACTIVE and conditions are such (autopilot on or
        // flaps down) that we should become INACTIVE?
        // decision D4
        if ((state == State.ACTIVE) && (autopilotOn || flapsDown))
        {
            state = State.INACTIVE;
        }

        // Are we currently ACTIVE and conditions are such (angle of attack is
        // no longer too high) that we should become ARMED?
        // decision D5
        if ((state == State.ACTIVE) && (angleOfAttack <= AOA_THRESHOLD))
        {
            state = State.ARMED;
        }

        // Are we currently ACTIVE and conditions are such (angle of attack is
        // still too high and the necessary time has elapsed since our last trim
        // DOWN command) that we should send a trim DOWN command?
        // decision D6
        if ((state == State.ACTIVE) && (angleOfAttack > AOA_THRESHOLD)
                && (System.currentTimeMillis() > (lastActivationTimeMs
                        + ACTIVATION_INTERVAL_MS)))
        {
            command = Command.DOWN;
            lastActivationTimeMs = System.currentTimeMillis();
        }

        // Return the desired trim command
        return command;
    }
    
    // FOR TESTING: TEST METHOD ADDED FOR OBSERVABILITY
    public State getState()
    {
        return state;
    }

}
