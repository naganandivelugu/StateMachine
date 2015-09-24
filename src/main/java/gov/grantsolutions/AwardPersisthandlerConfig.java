package gov.grantsolutions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler;

/**
 * Created by nnandivelugu on 9/24/15.
 */
@Configuration
public class AwardPersisthandlerConfig {

    private StateMachine<String,String> awardStateMachine;

    //@Bean(name = "awardStateMachineHandler")
    public PersistStateMachineHandler persistStateMachineHandler() {
        return new PersistStateMachineHandler(awardStateMachine);
    }

    //@Bean(name = "awardEvent")
    public AwardEvent persist() {return new AwardEvent(persistStateMachineHandler());}

}
