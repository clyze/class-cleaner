package org.clyze.cleaner;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DuplicateRemover {
    List<File> archives = new ArrayList<>();
    Map<String, ArchiveEntry> entryCatalog = new HashMap<>();
    Map<File, Collection<String>> toDelete = new HashMap<>();

    public void read(String[] paths) {
        for (String arg : paths)
            processPath(new File(arg));
    }

    private void processPath(File f) {
        if (f.getName().toLowerCase(Locale.ROOT).endsWith(".jar")) {
            System.out.println("Using archive: " + f);
            archives.add(f);
        } else if (f.isDirectory()) {
            System.out.println("Reading directory: " + f);
            File[] files = f.listFiles();
            if (files != null)
                for (File file : files) {
                    processPath(file);
                }
        } else
            System.out.println("Ignoring non-JAR argument: " + f);

    }

    public void scan() {
        for (File archive : archives) {
            System.out.println("Processing archive: " + archive);
            try (ZipFile zip = new ZipFile(archive)) {
                Enumeration<? extends ZipEntry> entries = zip.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry zipEntry = entries.nextElement();
                    if (!zipEntry.isDirectory()) {
                        String name = zipEntry.getName();
                        if (name.endsWith(".class")) {
                            ArchiveEntry prevEntry = entryCatalog.get(name);
                            long entrySize = zipEntry.getSize();
                            if (prevEntry == null) {
//                            System.out.println("Registering: " + name);
                                entryCatalog.put(name, new ArchiveEntry(archive, entrySize));
                            } else {
                                System.out.println("Duplicate class " + name +
                                        " (size=" + entrySize + "): originally in " +
                                        prevEntry + ", also found in " + archive);
                                if (entrySize != prevEntry.entrySize)
                                    System.out.println("WARNING: duplicate classes have different size!");
                                toDelete.computeIfAbsent(archive, (k -> new ArrayList<>())).add(name);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("ERROR: could not process file: " + archive);
                e.printStackTrace();
            }
        }
//        entryCatalog.forEach((k, v) -> System.out.println(k + " -> " + v));
    }

    public void prune() {
        System.out.println("Removing duplicate classes...");
        for (Map.Entry<File, Collection<String>> fileDelEntry : toDelete.entrySet()) {
            File f = fileDelEntry.getKey();
            Collection<String> names = fileDelEntry.getValue();
            URI uri = URI.create("jar:" + f.toURI());
            System.out.println("Processing URI: " + uri);
            try (FileSystem zipfs = FileSystems.newFileSystem(uri, new HashMap<String, String>())) {
                for (String name : names)
                    try {
                        Files.delete(zipfs.getPath(name));
                    } catch (IOException e) {
                        System.out.println("ERROR: could not delete entry: " + name);
                        e.printStackTrace();
                    }
            } catch (IOException e) {
                System.out.println("ERROR: could not edit archive: " + f);
                e.printStackTrace();
            }
        }
    }

    public void runOn(String[] args) {
        read(args);
        scan();
        prune();
    }
}
