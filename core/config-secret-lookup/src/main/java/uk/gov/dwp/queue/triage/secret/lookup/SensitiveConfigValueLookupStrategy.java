package uk.gov.dwp.queue.triage.secret.lookup;


import uk.gov.dwp.queue.triage.secret.lookup.domain.DecryptedValue;

public interface SensitiveConfigValueLookupStrategy {

    int FIRST_EVAL_ORDER = 0;
    int LAST_EVAL_ORDER = 100;

    int evaluationOrder();

    DecryptedValue retrieveSecret(String secret);

    boolean matches(String secret);

}
