package gov.grantsolutions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import gov.grantsolutions.StatemachineApplication.CommtimentStates;
import gov.grantsolutions.StatemachineApplication.CommitmentEvents;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import org.springframework.statemachine.transition.Transition;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {StatemachineApplication.class})
public class StatemachineApplicationTests {
    
    @Autowired
    private StateMachine<String, String> commitmentStateMachine;
    
    @Autowired
    private CommitmentEvent commitmentStateEvent;

    @Test
    public void contextLoads() {
    }

    
    @Test
    public void testForMultiThread() throws InterruptedException, ExecutionException {
            change1();
    }
    
    
    private void  change1() {
        System.out.println("Calling Change 1");
        synchronized(commitmentStateMachine) {
            commitmentStateEvent.change(CommitmentEvents.TO_RFA.name(), CommtimentStates.ENTERED.name());
            assertThat(commitmentStateMachine.getState().getIds(), contains(CommtimentStates.READY_FOR_APPROVAL.name()));
        }
    }
    
    private void change2() {
        System.out.println("Calling Change 2");
        commitmentStateEvent.change(CommitmentEvents.RECONCILE.name(), CommtimentStates.CREATED_IN_ACCOUNTING.name());
        assertThat(commitmentStateMachine.getState().getIds(), contains(CommtimentStates.COMPLETE.name()));
    }
    
    private void change3() {
        System.out.println("Calling Change 3");
        commitmentStateEvent.change(CommitmentEvents.RESEND.name(), CommtimentStates.ACCOUNTING_ERROR.name());
        assertThat(commitmentStateMachine.getState().getIds(), contains(CommtimentStates.SENT_TO_ACCOUNTING.name()));
    }
    
    @Test
    public void change4() {
        commitmentStateEvent.change(CommitmentEvents.APPROVE.name(), CommtimentStates.READY_FOR_APPROVAL.name());
        assertThat(commitmentStateMachine.getState().getIds(), contains(CommtimentStates.SENT_TO_ACCOUNTING.name()));
    }
    
    @Test
    public void change5() {
        commitmentStateEvent.change(CommitmentEvents.REJECTED.name(), CommtimentStates.SENT_TO_ACCOUNTING.name());
        assertThat(commitmentStateMachine.getState().getIds(), contains(CommtimentStates.ACCOUNTING_ERROR.name()));
    }
    
    
    private void test(final int threadCount) throws InterruptedException, ExecutionException {
        Callable<Long> task1 = new Callable<Long>() {
            @Override
            public Long call() throws InterruptedException {
                change1();
                return 1l;
            }
        };
        
        Callable<Long> task2 = new Callable<Long>() {
            @Override
            public Long call() {
                change2();
                return 1l;
            }
        };
        
        Callable<Long> task3 = new Callable<Long>() {
            @Override
            public Long call() {
                change3();
                return 1l;
            }
        };
        
        List<Callable<Long>> tasks1 = Collections.nCopies(threadCount, task1);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Future<Long>> futures = executorService.invokeAll(tasks1);
        List<Long> resultList = new ArrayList<Long>(futures.size());
        // Check for exceptions
        for (Future<Long> future : futures) {
            // Throws an exception if an exception was thrown by the task.
            resultList.add(future.get());
        }
    }
}
