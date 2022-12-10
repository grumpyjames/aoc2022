package net.digihippo.aoc2022;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

final class Inputs
{
    static InputStream asInputStream(String input) {
        return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    }

    static InputStream puzzleInput(String puzzleFile) {
        return Inputs.class.getResourceAsStream("/" + puzzleFile);
    }
}
