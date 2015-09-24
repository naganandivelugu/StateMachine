/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.grantsolutions.REST;

import gov.grantsolutions.CommitmentEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 *
 * @author nnandivelugu
 */
@Controller
@RequestMapping("/statemachine")
public class StateMachineController {

    @Autowired
    private CommitmentEvent commitmentStateEvent;

    @Resource
    private StateMachine<String, String> commitmentState;

    @RequestMapping(value = "/commitment/{event}/{currentState}", method = RequestMethod.GET)
    public @ResponseBody
    String nextState(@PathVariable final String event, @PathVariable final String currentState) {
        synchronized (commitmentState) {
            commitmentStateEvent.change(event, currentState);
            return commitmentState.getState().getId();
        }
    }

    @RequestMapping(value = "/commitment/initialize", method = RequestMethod.GET)
    public @ResponseBody
    String initializeCommitment() {
        return commitmentState.getInitialState().getId();
    }

    @RequestMapping(value = "/commitment/availableEvents/{currentState}", method = RequestMethod.GET)
    public @ResponseBody
    List<String> availableCommitmentEvents(@PathVariable final String currentState) {
        List<String> list = new ArrayList<String>();
        if (!StringUtils.isEmpty(currentState)) {
            Iterator<Transition<String, String>> transitions = commitmentState.getTransitions().iterator();
            while (transitions.hasNext()) {
                Transition<String, String> transition = transitions.next();
                if (currentState.equalsIgnoreCase(transition.getSource().getId())) {
                    list.add(transition.getTrigger().getEvent());
                }
            }
        }
        return list;
    }
}
