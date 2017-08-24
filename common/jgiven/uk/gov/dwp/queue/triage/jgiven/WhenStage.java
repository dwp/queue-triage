package uk.gov.dwp.queue.triage.jgiven;

import com.tngtech.jgiven.annotation.IntroWord;
import com.tngtech.jgiven.base.StageBase;

public class WhenStage<SELF extends WhenStage<?>> extends StageBase<SELF> {

    public WhenStage() {}

    @IntroWord
    public SELF when() {
        return this.self();
    }

    @IntroWord
    public SELF and() {
        return this.self();
    }
}
