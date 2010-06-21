package br.gov.serpro.tools.junit;

public class GlobalFlags {

    private static boolean newHashMapUsed;

    private static boolean newHashSet;

    public static boolean isNewHashMapUsed() {
        return newHashMapUsed;
    }

    public static void setNewHashMapUsed(boolean newHashMapUsed) {
        GlobalFlags.newHashMapUsed = newHashMapUsed;
    }
    public static boolean isNewHashSet() {
        return newHashSet;
    }
    public static void setNewHashSet(boolean newHashSet) {
        GlobalFlags.newHashSet = newHashSet;
    }



}
