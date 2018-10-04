package com.coreoz.windmill;

import com.coreoz.windmill.files.FileSource;
import com.coreoz.windmill.files.FileTypeGuesser;
import com.coreoz.windmill.imports.FileParser;
import com.coreoz.windmill.imports.Parsers;
import com.coreoz.windmill.imports.Row;

import java.util.stream.Stream;

public interface Importer {

    Stream<Row> stream();

    interface InitialState {
        ParserState source(FileSource fileSource);
    }

    interface ParserState extends HeaderState {
        HeaderState parser(FileParser fileParser);
    }

    interface HeaderState {
        Importer withHeaders();
        Importer withoutHeaders();
    }

    class Builder implements InitialState, ParserState, HeaderState {

        private FileParser parser;
        private FileSource fileSource;

        @Override
        public ParserState source(FileSource fileSource) {
            this.fileSource = fileSource;
            this.parser = Parsers.forType(FileTypeGuesser.guess(fileSource));
            return this;
        }

        @Override
        public HeaderState parser(FileParser fileParser) {
            this.parser = fileParser;
            return this;
        }

        @Override
        public Importer withHeaders() {
            return () -> parser.parse(fileSource).skip(1);
        }

        @Override
        public Importer withoutHeaders() {
            return () -> parser.parse(fileSource);
        }
    }

    static  InitialState builder() {
        return new Builder();
    }
}
