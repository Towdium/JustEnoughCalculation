package me.towdium.jecalculation.utils;


import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Version implements Comparable<Version> {
    private int major = 0, minor = 0, patch = 0;
    private String preRelease = "";
    private final boolean success;

    private static final String PRE_RELEASE_PREFIX = "-";

    public Version(int major, int minor, int patch, String preRelease) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.preRelease = preRelease;
        this.success = true;
    }

    public Version(String version) {
        String[] split = version.split(PRE_RELEASE_PREFIX);
        if (split.length == 0) {
            success = false;
        } else {
            String[] versions = split[0].split("\\.");
            if (versions.length != 3)
                success = false;
            else {
                major = Integer.parseInt(versions[0]);
                minor = Integer.parseInt(versions[1]);
                patch = Integer.parseInt(versions[2]);
                success = true;
            }
        }

        if (split.length > 1) {
            preRelease = split[1];
        }
    }

    @Override
    public int compareTo(Version o) {
        assert this.isSuccess();
        assert o.isSuccess();
        if (this.getMajor() != o.getMajor())
            return this.getMajor() < o.getMajor() ? -1 : 1;
        if (this.getMinor() != o.getMinor())
            return this.getMinor() < o.getMinor() ? -1 : 1;
        if (this.getPatch() != o.getPatch())
            return this.getPatch() < o.getPatch() ? -1 : 1;
        return 0;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public String getPreRelease() {
        return preRelease;
    }

    public boolean isSuccess() {
        return success;
    }
}
