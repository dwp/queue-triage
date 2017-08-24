package uk.gov.dwp.queue.triage.jgiven;

import com.tngtech.jgiven.annotation.IntroWord;
import com.tngtech.jgiven.base.StageBase;

public class GivenStage<SELF extends GivenStage<?>> extends StageBase<SELF> {

    public GivenStage() {}

    @IntroWord
    public SELF given() {
        return this.self();
    }

    @IntroWord
    public SELF and() {
        return this.self();
    }
}
