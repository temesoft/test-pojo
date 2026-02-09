package io.github.temesoft.testpojo.model;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class Pojo_BadToString {
    @Override
    public String toString() {
        final byte[] buff = new byte[128];
        new SecureRandom().nextBytes(buff);
        return new String(buff, StandardCharsets.UTF_8);
    }
}