package gov.grantsolutions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class StatemachineApplication {

    @Configuration
    @EnableStateMachine(name="commitmentStateMachine")
    static class StateMachineConfig extends StateMachineConfigurerAdapter<String, String> {

        @Override
        public void configure(StateMachineStateConfigurer<String, String> states)
                throws Exception {
            states.withStates().initial(CommtimentStates.ENTERED.name()).
                    state(CommtimentStates.READY_FOR_APPROVAL.name())
                    .state(CommtimentStates.SENT_TO_ACCOUNTING.name())
                    .state(CommtimentStates.ACCOUNTING_ERROR.name())
                    .state(CommtimentStates.CREATED_IN_ACCOUNTING.name())
                    .state(CommtimentStates.DISAPPROVED.name())
                    .state(CommtimentStates.ACCOUNTING_ERROR.name())
                    .state(CommtimentStates.CREATED_IN_ACCOUNTING.name())
                    .state(CommtimentStates.COMPLETE.name());
        }

        @Override
        public void configure(StateMachineTransitionConfigurer<String, String> transitions)
                throws Exception {
            //TODO make it enumeration that way its more type safe.
            transitions
                    .withExternal().
                    source(CommtimentStates.ENTERED.name()).target(CommtimentStates.READY_FOR_APPROVAL.name()).event(CommitmentEvents.TO_RFA.name())
                    .and()
                    .withExternal()
                    .source(CommtimentStates.READY_FOR_APPROVAL.name()).target(CommtimentStates.DISAPPROVED.name()).event(CommitmentEvents.DISAPPROVE.name())
                    .and()
                    .withExternal()
                    .source(CommtimentStates.READY_FOR_APPROVAL.name()).target(CommtimentStates.SENT_TO_ACCOUNTING.name()).event(CommitmentEvents.APPROVE.name())
                    .and()
                    .withExternal()
                    .source(CommtimentStates.SENT_TO_ACCOUNTING.name()).target(CommtimentStates.ACCOUNTING_ERROR.name()).event(CommitmentEvents.REJECTED.name())
                    .and()
                    .withExternal()
                    .source(CommtimentStates.ACCOUNTING_ERROR.name()).target(CommtimentStates.SENT_TO_ACCOUNTING.name()).event(CommitmentEvents.RESEND.name())
                    .and()
                    .withExternal()
                    .source(CommtimentStates.SENT_TO_ACCOUNTING.name()).target(CommtimentStates.CREATED_IN_ACCOUNTING.name()).event(CommitmentEvents.ACKNOWLEDGE.name())
                    .and()
                    .withExternal()
                    .source(CommtimentStates.CREATED_IN_ACCOUNTING.name()).target(CommtimentStates.COMPLETE.name()).event(CommitmentEvents.RECONCILE.name())
                    .and()
                    .withExternal()
                    .source(CommtimentStates.CREATED_IN_ACCOUNTING.name()).target(CommtimentStates.COMPLETE.name()).event(CommitmentEvents.EXPIRED.name())
                    .and();
        }
    }


    static class AwardStateMachine extends StateMachineConfigurerAdapter<String, String> {

        @Override
        public void configure(StateMachineStateConfigurer<String, String> states)
                throws Exception {
            states.withStates().initial(AwardStates.CREATE_DRAFT.name()).
                    state(AwardStates.ACCOUNTING_CLEARED.name());

        }

        @Override
        public void configure(StateMachineTransitionConfigurer<String, String> transitions)
                throws Exception {
            transitions
                    .withExternal().
                    source(AwardStates.CREATE_DRAFT.name()).target(AwardStates.PRE_COMPLETE_DELAY.name()).event(AwardEvents.TO_COMPLETED.name())
                    .and()
                    .withExternal().
                    source(AwardStates.CREATE_DRAFT.name()).target(AwardStates.DRAFTED.name()).event(AwardEvents.TO_DRAFTED.name())
                    .and()
                    .withExternal().
                    source(AwardStates.CREATE_DRAFT.name()).target(AwardStates.READY_TO_ISSUE.name()).event(AwardEvents.LOAD_TO_ISSUE.name())
                    .and()
                    .withExternal().

                    source(AwardStates.PRE_COMPLETE_DELAY.name()).target(AwardStates.WAITING_FOR_COMPLETION.name()).event(AwardEvents.YES.name())
                    .and()
                    .withExternal().
                    source(AwardStates.PRE_COMPLETE_DELAY.name()).target(AwardStates.COMPLETED.name()).event(AwardEvents.NO.name())
                    .and()
                    .withExternal().

                    source(AwardStates.WAITING_FOR_COMPLETION.name()).target(AwardStates.COMPLETED.name()).event(AwardEvents.EXPIRE.name())
                    .and()
                    .withExternal().
                    source(AwardStates.DRAFTED.name()).target(AwardStates.IN_REVIEW.name()).event(AwardEvents.READY_FOR_REVIEW.name())

                    .and()
                    .withExternal().
                    source(AwardStates.DRAFTED.name()).target(AwardStates.BO_REVIEW_REQUIRED.name()).event(AwardEvents.READY_FOR_APPROVAL.name())

                    .and()
                    .withExternal().
                    source(AwardStates.IN_REVIEW.name()).target(AwardStates.DRAFTED.name()).event(AwardEvents.RETURN.name())

                    .and()
                    .withExternal().
                    source(AwardStates.IN_REVIEW.name()).target(AwardStates.BO_REVIEW_REQUIRED.name()).event(AwardEvents.READY_FOR_APPROVAL.name())


                    .and()
                    .withExternal().
                    source(AwardStates.BO_REVIEW_REQUIRED.name()).target(AwardStates.READY_FOR_BUDGET_OFFICER_REVIEW.name()).event(AwardEvents.YES.name())

                    .and()
                    .withExternal().
                    source(AwardStates.BO_REVIEW_REQUIRED.name()).target(AwardStates.PO_APPORVAL_REQUIRED.name()).event(AwardEvents.NO.name())

                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_BUDGET_OFFICER_REVIEW.name()).target(AwardStates.DRAFTED.name()).event(AwardEvents.RECALL.name())
                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_BUDGET_OFFICER_REVIEW.name()).target(AwardStates.PO_APPORVAL_REQUIRED.name()).event(AwardEvents.BOCERTIFY.name())
                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_BUDGET_OFFICER_REVIEW.name()).target(AwardStates.REJECT_TO_DRAFT.name()).event(AwardEvents.REJECT.name())

                    .and()
                    .withExternal().
                    source(AwardStates.PO_APPORVAL_REQUIRED.name()).target(AwardStates.READY_FOR_PROGRAM_OFFICER_APPROVAL.name()).event(AwardEvents.YES.name())
                    .and()
                    .withExternal().
                    source(AwardStates.PO_APPORVAL_REQUIRED.name()).target(AwardStates.PM_APPROVAL_REQUIRED.name()).event(AwardEvents.NO.name())

                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_PROGRAM_OFFICER_APPROVAL.name()).target(AwardStates.PM_APPROVAL_REQUIRED.name()).event(AwardEvents.APPROVE.name())
                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_PROGRAM_OFFICER_APPROVAL.name()).target(AwardStates.REJECT_TO_DRAFT.name()).event(AwardEvents.REJECT.name())

                    .and()
                    .withExternal().
                    source(AwardStates.PM_APPROVAL_REQUIRED.name()).target(AwardStates.READY_FOR_PROGRAM_MANAGER_APPROVAL.name()).event(AwardEvents.YES.name())
                    .and()
                    .withExternal().
                    source(AwardStates.PM_APPROVAL_REQUIRED.name()).target(AwardStates.DIRECTOR_APPROVAL_REQUIRED.name()).event(AwardEvents.NO.name())

                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_PROGRAM_MANAGER_APPROVAL.name()).target(AwardStates.DIRECTOR_APPROVAL_REQUIRED.name()).event(AwardEvents.APPROVE.name())
                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_PROGRAM_MANAGER_APPROVAL.name()).target(AwardStates.REJECT_TO_DRAFT.name()).event(AwardEvents.REJECT.name())

                    .and()
                    .withExternal().
                    source(AwardStates.DIRECTOR_APPROVAL_REQUIRED.name()).target(AwardStates.READY_FOR_PROGRAM_DIRECTOR_APPROVAL.name()).event(AwardEvents.YES.name())
                    .and()
                    .withExternal().
                    source(AwardStates.DIRECTOR_APPROVAL_REQUIRED.name()).target(AwardStates.ADMINISTRATIVE_REVIEW_REQUIRED.name()).event(AwardEvents.NO.name())


                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_PROGRAM_DIRECTOR_APPROVAL.name()).target(AwardStates.ADMINISTRATIVE_REVIEW_REQUIRED.name()).event(AwardEvents.APPROVE.name())
                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_PROGRAM_DIRECTOR_APPROVAL.name()).target(AwardStates.REJECT_TO_DRAFT.name()).event(AwardEvents.REJECT.name())

                    .and()
                    .withExternal().
                    source(AwardStates.ADMINISTRATIVE_REVIEW_REQUIRED.name()).target(AwardStates.READY_FOR_ADMINISTRATIVE_REVIEW.name()).event(AwardEvents.YES.name())
                    .and()
                    .withExternal().
                    source(AwardStates.ADMINISTRATIVE_REVIEW_REQUIRED.name()).target(AwardStates.AUTHORIZATION_REQUIRED.name()).event(AwardEvents.NO.name())


                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_ADMINISTRATIVE_REVIEW.name()).target(AwardStates.AUTHORIZATION_REQUIRED.name()).event(AwardEvents.APPROVE.name())
                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_ADMINISTRATIVE_REVIEW.name()).target(AwardStates.REJECT_TO_DRAFT.name()).event(AwardEvents.REJECT.name())

                    .and()
                    .withExternal().
                    source(AwardStates.AUTHORIZATION_REQUIRED.name()).target(AwardStates.READY_FOR_AUTHORIZATION_REVIEW.name()).event(AwardEvents.YES.name())
                    .and()
                    .withExternal().
                    source(AwardStates.AUTHORIZATION_REQUIRED.name()).target(AwardStates.CERTIFY_BEFORE_FINAL_APPROVAL.name()).event(AwardEvents.NO.name())

                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_AUTHORIZATION_REVIEW.name()).target(AwardStates.CERTIFY_BEFORE_FINAL_APPROVAL.name()).event(AwardEvents.APPROVE.name())
                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_AUTHORIZATION_REVIEW.name()).target(AwardStates.REJECT_TO_DRAFT.name()).event(AwardEvents.REJECT.name())

                    .and()
                    .withExternal().
                    source(AwardStates.CERTIFY_BEFORE_FINAL_APPROVAL.name()).target(AwardStates.READY_TO_CERTIFY.name()).event(AwardEvents.YES.name())
                    .and()
                    .withExternal().
                    source(AwardStates.CERTIFY_BEFORE_FINAL_APPROVAL.name()).target(AwardStates.COO_APPROVAL_REQUIRED.name()).event(AwardEvents.NO.name())

                    .and()
                    .withExternal().
                    source(AwardStates.READY_TO_CERTIFY.name()).target(AwardStates.DRAFTED.name()).event(AwardEvents.RECALL.name())
                    .and()
                    .withExternal().
                    source(AwardStates.READY_TO_CERTIFY.name()).target(AwardStates.REJECT_TO_DRAFT.name()).event(AwardEvents.REJECT.name())
                    .and()
                    .withExternal().
                    source(AwardStates.READY_TO_CERTIFY.name()).target(AwardStates.CERTIFY_RULE.name()).event(AwardEvents.CERTIFY.name())

                    .and()
                    .withExternal().
                    source(AwardStates.REJECT_TO_DRAFT.name()).target(AwardStates.DRAFTED.name()).event(AwardEvents.YES.name())
                    .and()
                    .withExternal().
                    source(AwardStates.REJECT_TO_DRAFT.name()).target(AwardStates.IN_REVIEW.name()).event(AwardEvents.NO.name())


                    .and()
                    .withExternal().
                    source(AwardStates.CERTIFY_RULE.name()).target(AwardStates.READY_TO_ISSUE.name()).event(AwardEvents.POST_CERTIFY.name())
                    .and()
                    .withExternal().
                    source(AwardStates.CERTIFY_RULE.name()).target(AwardStates.COO_APPROVAL_REQUIRED.name()).event(AwardEvents.PRE_CERTIFY.name())

                    .and()
                    .withExternal().
                    source(AwardStates.COO_APPROVAL_REQUIRED.name()).target(AwardStates.READY_FOR_FINAL_APPROVAL.name()).event(AwardEvents.YES.name())
                    .and()
                    .withExternal().
                    source(AwardStates.COO_APPROVAL_REQUIRED.name()).target(AwardStates.CERTIFY_BEFORE_ISSUE.name()).event(AwardEvents.NO.name())


                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_FINAL_APPROVAL.name()).target(AwardStates.CERTIFY_BEFORE_ISSUE.name()).event(AwardEvents.APPROVE.name())
                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_FINAL_APPROVAL.name()).target(AwardStates.REJECT_TO_DRAFT.name()).event(AwardEvents.REJECT.name())
                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_FINAL_APPROVAL.name()).target(AwardStates.DRAFTED.name()).event(AwardEvents.RECALL.name())


                    .and()
                    .withExternal().
                    source(AwardStates.CERTIFY_BEFORE_ISSUE.name()).target(AwardStates.READY_TO_CERTIFY.name()).event(AwardEvents.YES.name())
                    .and()
                    .withExternal().
                    source(AwardStates.CERTIFY_BEFORE_ISSUE.name()).target(AwardStates.READY_TO_ISSUE.name()).event(AwardEvents.NO.name())

                    .and()
                    .withExternal().
                    source(AwardStates.READY_TO_ISSUE.name()).target(AwardStates.REJECT_TO_DRAFT.name()).event(AwardEvents.REJECT.name())
                    .and()
                    .withExternal().
                    source(AwardStates.READY_TO_ISSUE.name()).target(AwardStates.NOTIFICATION_DELAY.name()).event(AwardEvents.ISSUE.name())

                    .and()
                    .withExternal().
                    source(AwardStates.NOTIFICATION_DELAY.name()).target(AwardStates.WAITING_FOR_NOTIFICATION.name()).event(AwardEvents.YES.name())
                    .and()
                    .withExternal().
                    source(AwardStates.NOTIFICATION_DELAY.name()).target(AwardStates.GRANTEE_SIGNATURE_REQUIRED.name()).event(AwardEvents.NO.name())


                    .and()
                    .withExternal().
                    source(AwardStates.WAITING_FOR_NOTIFICATION.name()).target(AwardStates.GRANTEE_SIGNATURE_REQUIRED.name()).event(AwardEvents.EXPIRE.name())

                    .and()
                    .withExternal().
                    source(AwardStates.GRANTEE_SIGNATURE_REQUIRED.name()).target(AwardStates.AWAITING_ACCEPTANCE.name()).event(AwardEvents.YES.name())
                    .and()
                    .withExternal().
                    source(AwardStates.GRANTEE_SIGNATURE_REQUIRED.name()).target(AwardStates.AO_RELEASE_REQUIRED.name()).event(AwardEvents.NO.name())

                    .and()
                    .withExternal().
                    source(AwardStates.AWAITING_ACCEPTANCE.name()).target(AwardStates.AO_RELEASE_REQUIRED.name()).event(AwardEvents.GRANTEE_ASSIGNED.name())
                    .and()
                    .withExternal().
                    source(AwardStates.AWAITING_ACCEPTANCE.name()).target(AwardStates.AO_RELEASE_REQUIRED.name()).event(AwardEvents.GRANTS_STAFF_ASSIGNED.name())
                    .and()
                    .withExternal().
                    source(AwardStates.AWAITING_ACCEPTANCE.name()).target(AwardStates.REJECT_TO_DRAFT.name()).event(AwardEvents.REJECT.name())
                    .and()
                    .withExternal().
                    source(AwardStates.AWAITING_ACCEPTANCE.name()).target(AwardStates.REJECT_TO_DRAFT.name()).event(AwardEvents.DECLINE.name())


                    .and()
                    .withExternal().
                    source(AwardStates.AO_RELEASE_REQUIRED.name()).target(AwardStates.READY_FOR_AO_RELEASE.name()).event(AwardEvents.YES.name())
                    .and()
                    .withExternal().
                    source(AwardStates.AO_RELEASE_REQUIRED.name()).target(AwardStates.ACCOUNTING_REQUIRED.name()).event(AwardEvents.NO.name())
                    .and()
                    .withExternal().
                    source(AwardStates.ACCOUNTING_REQUIRED.name()).target(AwardStates.READY_FOR_ACCOUNTING.name()).event(AwardEvents.YES.name())
                    .and()
                    .withExternal().
                    source(AwardStates.ACCOUNTING_REQUIRED.name()).target(AwardStates.PRE_COMPLETE_DELAY.name()).event(AwardEvents.NO.name())

                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_ACCOUNTING.name()).target(AwardStates.SEND_TO_ACCOUNTING.name()).event(AwardEvents.SEND.name())
                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_ACCOUNTING.name()).target(AwardStates.ACCOUNTING_REJECTION.name()).event(AwardEvents.FAILURE.name())

                    .and()
                    .withExternal().
                    source(AwardStates.SEND_TO_ACCOUNTING.name()).target(AwardStates.RECALL_TO_DRAFT.name()).event(AwardEvents.RECALL.name())
                    .and()
                    .withExternal().
                    source(AwardStates.SEND_TO_ACCOUNTING.name()).target(AwardStates.PRE_COMPLETE_DELAY.name()).event(AwardEvents.VALIDATED.name())
                    .and()
                    .withExternal().
                    source(AwardStates.SEND_TO_ACCOUNTING.name()).target(AwardStates.ACCOUNTING_REJECTION.name()).event(AwardEvents.REJECTED.name())


                    .and()
                    .withExternal().
                    source(AwardStates.ACCOUNTING_REJECTION.name()).target(AwardStates.REJECT_TO_DRAFT.name()).event(AwardEvents.REDO.name())
                    .and()
                    .withExternal().
                    source(AwardStates.ACCOUNTING_REJECTION.name()).target(AwardStates.READY_FOR_ACCOUNTING.name()).event(AwardEvents.RESEND.name())
                    .and()
                    .withExternal().
                    source(AwardStates.ACCOUNTING_REJECTION.name()).target(AwardStates.ACCOUNTING_CLEARED.name()).event(AwardEvents.CLEARED.name())

                    .and()
                    .withExternal().
                    source(AwardStates.ACCOUNTING_REJECTION.name()).target(AwardStates.REJECT_TO_DRAFT.name()).event(AwardEvents.REDO.name())
                    .and()
                    .withExternal().
                    source(AwardStates.ACCOUNTING_REJECTION.name()).target(AwardStates.READY_FOR_ACCOUNTING.name()).event(AwardEvents.RESEND.name())
                    .and()
                    .withExternal().
                    source(AwardStates.ACCOUNTING_REJECTION.name()).target(AwardStates.ACCOUNTING_CLEARED.name()).event(AwardEvents.CLEARED.name())


                    .and()
                    .withExternal().
                    source(AwardStates.ACCOUNTING_CLEARED.name()).target(AwardStates.PRE_COMPLETE_DELAY.name()).event(AwardEvents.COMPLETE.name())
                    .and()
                    .withExternal().
                    source(AwardStates.ACCOUNTING_CLEARED.name()).target(AwardStates.ACCOUNTING_REJECTION.name()).event(AwardEvents.FAILURE.name())

                    .and()
                    .withExternal().
                    source(AwardStates.RECALL_TO_DRAFT.name()).target(AwardStates.DRAFTED.name()).event(AwardEvents.SUCCESS.name())
                    .and()
                    .withExternal().
                    source(AwardStates.RECALL_TO_DRAFT.name()).target(AwardStates.SEND_TO_ACCOUNTING.name()).event(AwardEvents.FAILURE.name())

                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_AO_RELEASE.name()).target(AwardStates.ACCOUNTING_REQUIRED.name()).event(AwardEvents.RELEASE.name())
                    .and()
                    .withExternal().
                    source(AwardStates.READY_FOR_AO_RELEASE.name()).target(AwardStates.REJECT_TO_DRAFT.name()).event(AwardEvents.REJECT.name());
        }
    }

    public static enum AwardStates {

        CREATE_DRAFT,
        PRE_COMPLETE_DELAY,
        WAITING_FOR_COMPLETION,
        COMPLETED, DRAFTED,
        IN_REVIEW,
        BO_REVIEW_REQUIRED,
        READY_FOR_BUDGET_OFFICER_REVIEW,
        PO_APPORVAL_REQUIRED,
        READY_FOR_PROGRAM_OFFICER_APPROVAL,
        PM_APPROVAL_REQUIRED,
        READY_FOR_PROGRAM_MANAGER_APPROVAL,
        DIRECTOR_APPROVAL_REQUIRED,
        READY_FOR_PROGRAM_DIRECTOR_APPROVAL,
        ADMINISTRATIVE_REVIEW_REQUIRED,
        READY_FOR_ADMINISTRATIVE_REVIEW,
        AUTHORIZATION_REQUIRED,
        READY_FOR_AUTHORIZATION_REVIEW,
        CERTIFY_BEFORE_FINAL_APPROVAL,
        READY_TO_CERTIFY,
        CERTIFY_RULE,
        COO_APPROVAL_REQUIRED,
        READY_FOR_FINAL_APPROVAL,
        CERTIFY_BEFORE_ISSUE,
        READY_TO_ISSUE,
        NOTIFICATION_DELAY,
        WAITING_FOR_NOTIFICATION,
        GRANTEE_SIGNATURE_REQUIRED,
        AWAITING_ACCEPTANCE,
        AO_RELEASE_REQUIRED,
        ACCOUNTING_REQUIRED,
        READY_FOR_AO_RELEASE,
        READY_FOR_ACCOUNTING,
        ACCOUNTING_REJECTION,
        ACCOUNTING_CLEARED,
        SEND_TO_ACCOUNTING,
        RECALL_TO_DRAFT,
        REJECT_TO_DRAFT;

        public static Set<String> getListOfStates() {
            Set<String> states = new HashSet<String>();
            for (AwardStates states12 : AwardStates.values()) {
                states.add(states12.name());
            }
            return states;
        }
    }

    public static enum AwardEvents {

        TO_COMPLETED, TO_DRAFTED, YES, NO, EXPIRE, READY_FOR_REVIEW, READY_FOR_APPROVAL, RETURN, RECALL, REJECT, BOCERTIFY, APPROVE, LOAD_TO_ISSUE, CERTIFY, PRE_CERTIFY, POST_CERTIFY
        ,DECLINE,GRANTS_STAFF_ASSIGNED,GRANTEE_ASSIGNED,RELEASE,RESEND,FAILURE,REDO,CLEARED,COMPLETE,VALIDATED,SEND,SUCCESS,ISSUE,REJECTED;
    }
    
    @Configuration
    static class PersistHandlerConfig {
        
        @Autowired
        private StateMachine<String,String> commitmentStateMachine;
        
        @Bean(name = "commitmentStateMachineHandler")
        public PersistStateMachineHandler persistStateMachineHandler() {
            return new PersistStateMachineHandler(commitmentStateMachine);
        }
        
        @Bean(name = "commitmentStateEvent")
        public CommitmentEvent persist() {return new CommitmentEvent(persistStateMachineHandler());}
    }

    public static enum CommtimentStates {
       CREATE_COMMITMENT, ENTERED, READY_FOR_APPROVAL, DISAPPROVED, SENT_TO_ACCOUNTING, CREATED_IN_ACCOUNTING, ACCOUNTING_ERROR, COMPLETE;
    }
    
    public static enum CommitmentEvents {
        TO_ENTERED, TO_RFA, APPROVE, ACKNOWLEDGE, RESEND, REJECTED, REDO, RECONCILE, EXPIRED, DISAPPROVE;
    }

    public static void main(String[] args) {
        SpringApplication.run(StatemachineApplication.class, args);
    }
}
