package uk.gov.dwp.queue.triage.jgiven;

import com.tngtech.jgiven.annotation.IntroWord;
import com.tngtech.jgiven.base.StageBase;

public class ThenStage<SELF extends ThenStage<?>> extends StageBase<SELF> {

    @IntroWord
    public SELF then() {
        return this.self();
    }

    @IntroWord
    public SELF and() {
        return this.self();
    }
}
