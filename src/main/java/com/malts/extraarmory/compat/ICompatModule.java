package com.malts.extraarmory.compat;

public interface ICompatModule {
    String getModId();
    String getName();
    default void preInit() {}
    default void init() {}
    default void postInit() {}
}