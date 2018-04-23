package uk.gov.dwp.queue.triage.web.component.status;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.status.StatusHistoryResponse;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.web.component.WebComponentTest;
import uk.gov.dwp.queue.triage.web.component.login.LoginApiGivenStage;
import uk.gov.dwp.queue.triage.web.server.api.Constants;

import java.time.Instant;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.isJson;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.withJsonPath;
import static uk.gov.dwp.queue.triage.core.client.FailedMessageStatus.FAILED;
import static uk.gov.dwp.queue.triage.core.client.FailedMessageStatus.RESENDING;

public class StatusHistoryApiComponentTest extends WebComponentTest<StatusHistoryGivenStage, StatusHistoryWhenStage, StatusHistoryThenStage> {

    private static final FailedMessageId FAILED_MESSAGE_ID_1 = FailedMessageId.newFailedMessageId();
    private static final Instant NOW = Instant.now();

    @ScenarioStage
    private LoginApiGivenStage loginApiGivenStage;

    @Test
    public void requestStatusHistoryForFailedMessage() {
        given().aFailedMessageWithId$Has$(
                FAILED_MESSAGE_ID_1,
                new StatusHistoryResponse(FAILED, NOW),
                new StatusHistoryResponse(RESENDING, NOW.plusSeconds(5)));
        loginApiGivenStage.given().and().theUserHasSuccessfullyLoggedOn();
        when().theStatusHistoryIsRequestedForFailedMessage$(FAILED_MESSAGE_ID_1);
        then().theStatusHistoryResponseForFailedMessage(FAILED_MESSAGE_ID_1, isJson(allOf(
                withJsonPath("$.records", hasSize(2)),
                withJsonPath("$.status", equalTo("success")),
                withJsonPath("$.total", equalTo(2)))
        ));
        then().theStatusHistoryResponseForFailedMessage(FAILED_MESSAGE_ID_1, isJson(allOf(
                withJsonPath("$.records[0].recid", equalTo(NOW.toEpochMilli() + "-failed")),
                withJsonPath("$.records[0].status", equalTo("Failed")),
                withJsonPath("$.records[0].effectiveDateTime", equalTo(Constants.toIsoDateTimeWithMs(NOW).get()))
        )));
        then().theStatusHistoryResponseForFailedMessage(FAILED_MESSAGE_ID_1, isJson(allOf(
                withJsonPath("$.records[1].recid", equalTo(NOW.plusSeconds(5).toEpochMilli() + "-resending")),
                withJsonPath("$.records[1].status", equalTo("Resending")),
                withJsonPath("$.records[1].effectiveDateTime", equalTo(Constants.toIsoDateTimeWithMs(NOW.plusSeconds(5)).get()))
        )));
    }
}
