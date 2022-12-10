package net.digihippo.aoc2022;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Seven extends SolutionTemplate<Long, Long> {
    Pattern fileSize = Pattern.compile("([0-9]+) (.*)");

    @Override
    Solution<Long> partOne() {
        return new DirectoryParser(directorySizes -> {
            long total = 0;
            for (Map.Entry<String, Long> stringLongEntry : directorySizes.entrySet()) {
                long size = stringLongEntry.getValue();
                if (size <= 100000) {
                    total += size;
                }
            }

            return total;
        });
    }

    @Override
    Solution<Long> partTwo() {
        return new DirectoryParser(directorySizes -> {
            long diskSize = 70000000;
            long requiredSpace = 30000000;
            long rootSize = directorySizes.get("/");
            long deleteThreshold = requiredSpace - (diskSize - rootSize);

            long bestSoFar = Long.MAX_VALUE;
            for (Map.Entry<String, Long> stringLongEntry : directorySizes.entrySet()) {
                long size = stringLongEntry.getValue();
                if (size >= deleteThreshold && size < bestSoFar) {
                    bestSoFar = size;
                }
            }

            return bestSoFar;
        });
    }

    private class DirectoryParser implements Solution<Long> {
        private final Deque<String> directory = new ArrayDeque<>();
        private final Map<String, Long> directorySizes = new HashMap<>();
        private final Function<Map<String, Long>, Long> resultComputer;

        public DirectoryParser(Function<Map<String, Long>, Long> resultComputer) {
            this.resultComputer = resultComputer;
        }

        @Override
        public Long result() {
            while (!currentWorkingDirectory().equals("/"))
            {
                popDir();
            }

            return resultComputer.apply(directorySizes);
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public void accept(String line) {
            if (line.startsWith("$ cd"))
            {
                if (line.endsWith(".."))
                {
                    popDir();
                }
                else
                {
                    pushDir(line.split("\\s")[2]);
                }
            }
            else if (line.startsWith("$ ls") || line.startsWith("dir"))
            {
                // just ignore it
            }
            else
            {
                Matcher matcher = fileSize.matcher(line);
                if (matcher.find())
                {
                    long size = Long.parseLong(matcher.group(1));
                    addSize(size);
                }
            }
        }

        private void addSize(long size) {
            String currentDir = currentWorkingDirectory();
            directorySizes.putIfAbsent(currentDir, 0L);
            directorySizes.put(
                    currentDir,
                    directorySizes.get(currentDir) + size
            );
        }

        private String currentWorkingDirectory() {
            return "/" + String.join("/", directory);
        }

        private void popDir() {
            final String leavingDirectory = currentWorkingDirectory();
            directory.removeLast();
            addSize(directorySizes.getOrDefault(leavingDirectory, 0L));
        }

        private void pushDir(String dir) {
            if (!dir.equals("/")) {
                directory.addLast(dir);
            }
        }
    }
}
