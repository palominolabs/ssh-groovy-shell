package com.palominolabs.ssh.auth;

import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import com.palominolabs.ssh.auth.publickey.PublicKeyParser;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public final class AuthorizedKeyParserTest {

    private AuthorizedKeyParser authorizedKeyParser;

    @Before
    public void setUp() {
        authorizedKeyParser = new AuthorizedKeyParser(Lists.<PublicKeyParser>newArrayList(new DummyParser()));
    }

    @Test
    public void testParseValidLine() throws IOException {
        ByteArrayInputStream is =
            new ByteArrayInputStream("dummy aaa comment1\ndummy bbb comment2".getBytes(UTF_8));

        List<AuthorizedKey> keys = authorizedKeyParser.parse(is);

        assertEquals(2, keys.size());

        assertKey(keys.get(0), "aaa", "comment1");
        assertKey(keys.get(1), "bbb", "comment2");
    }

    private void assertKey(AuthorizedKey k0, String data, String comment) {
        BaseEncoding b64 = BaseEncoding.base64();
        assertArrayEquals(b64.decode(data), ((DummyPublicKey) k0.getPublicKey()).data);

        assertEquals(comment, k0.getComment());
        assertEquals("dummy", k0.getKeyType());
    }

    private static class DummyParser implements PublicKeyParser {

        @Nonnull
        @Override
        public String getKeyType() {
            return "dummy";
        }

        @Override
        public PublicKey parse(byte[] data) {
            return new DummyPublicKey(data);
        }
    }

    private static class DummyPublicKey implements PublicKey {

        private final byte[] data;

        private DummyPublicKey(byte[] data) {
            this.data = data;
        }

        @Override
        public String getAlgorithm() {
            return "dummy";
        }

        @Override
        public String getFormat() {
            return "dummy";
        }

        @Override
        public byte[] getEncoded() {
            return data;
        }
    }
}
