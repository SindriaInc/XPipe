package org.sindria.xpipe.core.lib.nanorest.kernel;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandCompleter implements Completer {

    private final Set<String> commands;

    public CommandCompleter(Set<String> commands) {
        this.commands = commands;
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        String buffer = line.word().startsWith("/") ? line.word().substring(1) : line.word();

        // Suggest commands that start with the typed string
        List<String> matches = commands.stream()
                .filter(cmd -> cmd.startsWith(buffer))
                .collect(Collectors.toList());

        for (String match : matches) {
            candidates.add(new Candidate("/" + match));
        }
    }
}
