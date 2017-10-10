package me.towdium.jecalculation.network;

/**
 * Author: towdium
 * Date:   17-10-10.
 */
public interface IProxy {
    default void initPre() {
    }

    default void init() {
    }

    default void initPost() {
    }

    default void displayCalculator() {
    }
}
