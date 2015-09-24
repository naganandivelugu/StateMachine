/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.grantsolutions;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler.PersistStateChangeListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

/**
 *
 * @author nnandivelugu
 */
public class CommitmentEvent {
    
    private final PersistStateMachineHandler commitmentStateMachineHandler;
    
    private final PersistStateChangeListener listener = new LocalPersisStateChangeListener();

    public CommitmentEvent(PersistStateMachineHandler handler) {
        this.commitmentStateMachineHandler = handler;
        this.commitmentStateMachineHandler.addPersistStateChangeListener(listener);
    }
    
    public void change(String event,String currentState) {
        commitmentStateMachineHandler.handleEventWithState(MessageBuilder.withPayload(event).build(), currentState);
    } 
    
    private class LocalPersisStateChangeListener implements PersistStateChangeListener {

        public LocalPersisStateChangeListener() {
        }

        @Override
        public void onPersist(State<String, String> state, Message<String> msg, Transition<String, String> trnstn, StateMachine<String, String> sm) {
               //TODO handle the event and propograte the status back. 
        }
    }
}
