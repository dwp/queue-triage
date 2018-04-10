package uk.gov.dwp.queue.triage.core.dao.mongo.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.junit.Test;

import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InstantAsDateTimeCodecTest {

    private static final long EPOCH_MILLI = 1234;
    private static final Instant AN_INSTANT = Instant.ofEpochMilli(EPOCH_MILLI);

    private final EncoderContext encoderContext = EncoderContext.builder().build();
    private final DecoderContext decoderContext = DecoderContext.builder().build();
    private final BsonReader bsonReader = mock(BsonReader.class);
    private final BsonWriter bsonWriter = mock(BsonWriter.class);

    private final InstantAsDateTimeCodec underTest = new InstantAsDateTimeCodec();

    @Test
    public void encode() {
        underTest.encode(bsonWriter, AN_INSTANT, encoderContext);
        verify(bsonWriter).writeDateTime(EPOCH_MILLI);
    }

    @Test
    public void decode() {
        when(bsonReader.readDateTime()).thenReturn(EPOCH_MILLI);

        assertThat(underTest.decode(bsonReader, decoderContext), equalTo(AN_INSTANT));
    }
}