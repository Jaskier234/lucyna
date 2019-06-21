package test.java;

import main.java.index.FileParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;

public class FileParserTest {

    @Test
    public void LanguageDetectorTest() {
        FileParser parser = new FileParser();

        assertThat(parser.getLanguage("To jest zdanie po polsku")).isEqualTo("pl");
    }
}
