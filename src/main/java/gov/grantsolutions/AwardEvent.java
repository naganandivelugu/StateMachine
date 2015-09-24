package gov.grantsolutions;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

/**
 * Created by nnandivelugu on 9/24/15.
 */
public class AwardEvent {

    private final PersistStateMachineHandler awardStateMachineHandler;

    private final PersistStateMachineHandler.PersistStateChangeListener listener = new LocalPersisStateChangeListener();

    public AwardEvent(PersistStateMachineHandler handler) {
        this.awardStateMachineHandler = handler;
        this.awardStateMachineHandler.addPersistStateChangeListener(listener);
    }

    public void change(String event,String currentState) {
        awardStateMachineHandler.handleEventWithState(MessageBuilder.withPayload(event).build(), currentState);
    }

    private class LocalPersisStateChangeListener implements PersistStateMachineHandler.PersistStateChangeListener {

        public LocalPersisStateChangeListener() {
        }

        @Override
        public void onPersist(State<String, String> state, Message<String> msg, Transition<String, String> trnstn, StateMachine<String, String> sm) {
            //TODO handle the event and propograte the status back.
        }
    }
}
