package org.clyze.cleaner;

import java.io.File;

class ArchiveEntry {
    final File archive;
    final long entrySize;

    ArchiveEntry(File archive, long entrySize) {
        this.archive = archive;
        this.entrySize = entrySize;
    }

    @Override
    public String toString() {
        return "entry{size=" + entrySize + "}@" + archive;
    }
}
