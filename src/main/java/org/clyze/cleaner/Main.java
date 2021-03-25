package org.clyze.cleaner;

public class Main {
    public static void main(String[] args) {
        DuplicateRemover remover = new DuplicateRemover();
        remover.runOn(args);
    }
}

