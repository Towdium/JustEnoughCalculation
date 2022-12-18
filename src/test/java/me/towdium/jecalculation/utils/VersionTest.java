package me.towdium.jecalculation.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Please use IDEA test instead Gradle. (In settings 'Gradle -> Run Tests Using')
 */
class VersionTest {

    @Test
    void newInstance_withTwoDotRelease_isParsedCorrectly() {
        final Version version = new Version("2.12.24");
        assertTrue(version.isSuccess());
        assertEquals(version.getMajor(), 2);
        assertEquals(version.getMinor(), 12);
        assertEquals(version.getPatch(), 24);
    }

    @Test
    void newInstance_withTwoDotReleaseAndPreReleaseName_isParsedCorrectly() {
        final Version version = new Version("1.26.2-DEBUG");
        assertTrue(version.isSuccess());
        assertEquals(version.getMajor(), 1);
        assertEquals(version.getMinor(), 26);
        assertEquals(version.getPatch(), 2);
        assertEquals(version.getPreRelease(), "DEBUG");
    }

    @Test
    void compareTo_withEarlierVersion_isGreaterThan() {
        assertEquals(new Version("2.0.0").compareTo(new Version("1.0.0")), 1);
    }

    @Test
    void compareTo_withSameVersion_isEqual() {
        assertEquals(new Version("2.0.0").compareTo(new Version("2.0.0")), 0);
    }

    @Test
    void compareTo_withLaterVersion_isLessThan() {
        assertEquals(new Version("1.0.0").compareTo(new Version("2.0.0")), -1);
    }

    @Test
    void compareTo_withPreReleaseName_isGreaterThan() {
        assertEquals(new Version("2.1.2-GTNH").compareTo(new Version("2.0.1-GTHN")), 1);
    }

    @Test
    void fix_not_number() {
        Version version = new Version("1.0.6c");
        assert !version.isSuccess();
    }
}
