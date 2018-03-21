package uk.gov.dwp.vault;

import uk.gov.dwp.vault.domain.DecryptedValue;

public interface SensitiveConfigValueLookupStrategy {

    int FIRST_EVAL_ORDER = 0;
    int LAST_EVAL_ORDER = 100;

    int evaluationOrder();

    DecryptedValue retrieveSecret(String secret);

    boolean matches(String secret);

}
