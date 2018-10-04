package com.coreoz.windmill;

import com.coreoz.windmill.files.FileSource;
import com.coreoz.windmill.files.FileTypeGuesser;
import com.coreoz.windmill.imports.FileParser;
import com.coreoz.windmill.imports.Parsers;
import com.coreoz.windmill.imports.Row;
import com.coreoz.windmill.utils.BeanPropertyUtils;

import java.util.function.Function;
import java.util.stream.Stream;

public interface Importer<T> {

    Stream<T> stream();

    interface InitialState<T> {
        ParserState<T> source(FileSource fileSource);
    }

    interface ParserState<T> extends HeaderState<T> {
        MutationState<T> parser(FileParser fileParser);
    }

    interface HeaderState<T> extends MutationState<T> {
        MutationState<T> withHeaders();
        MutationState<T> withoutHeaders();
    }

    interface MutationState<T> extends Importer<Row> {
        Importer<T> withType(Class<T> targetClass);
        Importer<T> withType(Function<? super Row, ? extends T> transformer);
    }

    class Builder<T> implements InitialState<T>, ParserState<T>, HeaderState<T>, MutationState<T> {

        private FileParser parser;
        private FileSource fileSource;
        private long skip = 0;

        @Override
        public ParserState<T> source(FileSource fileSource) {
            this.fileSource = fileSource;
            this.parser = Parsers.forType(FileTypeGuesser.guess(fileSource));
            return this;
        }

        @Override
        public Importer<T> withType(Class<T> targetClass) {
            return withType(BeanPropertyUtils.rowTransformer(targetClass));
        }

        @Override
        public Importer<T> withType(Function<? super Row, ? extends T> transformer) {
            return () -> parser.parse(fileSource).skip(skip).map(transformer);
        }

        @Override
        public Stream<Row> stream() {
            return parser.parse(fileSource).skip(skip);
        }

        @Override
        public MutationState<T> parser(FileParser fileParser) {
            this.parser = fileParser;
            return this;
        }

        @Override
        public MutationState<T> withHeaders() {
            this.skip = 1;
            return this;
        }

        @Override
        public MutationState<T> withoutHeaders() {
            return this;
        }
    }

    static <T> InitialState<T> builder() {
        return new Builder<>();
    }
}
