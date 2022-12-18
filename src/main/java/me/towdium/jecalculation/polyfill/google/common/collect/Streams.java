package me.towdium.jecalculation.polyfill.google.common.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

public class Streams {
    public interface FunctionWithIndex<T, R> {
        /** Applies this function to the given argument and its index within a stream. */
        R apply(T from, long index);
    }

    /**
     * Returns a stream consisting of the results of applying the given function to the elements of
     * {@code stream} and their indices in the stream. For example,
     *
     * <pre>{@code
     * mapWithIndex(
     *     Stream.of("a", "b", "c"),
     *     (str, index) -> str + ":" + index)
     * }</pre>
     *
     * <p>would return {@code Stream.of("a:0", "b:1", "c:2")}.
     *
     * <p>The resulting stream is <a
     * href="http://gee.cs.oswego.edu/dl/html/StreamParallelGuidance.html">efficiently splittable</a>
     * if and only if {@code stream} was efficiently splittable and its underlying spliterator
     * reported {@link Spliterator#SUBSIZED}. This is generally the case if the underlying stream
     * comes from a data structure supporting efficient indexed random access, typically an array or
     * list.
     *
     * <p>The order of the resulting stream is defined if and only if the order of the original stream
     * was defined.
     */
    public static <T, R> Stream<R> mapWithIndex(Stream<T> stream, FunctionWithIndex<? super T, ? extends R> function) {
        checkNotNull(stream);
        checkNotNull(function);
        boolean isParallel = stream.isParallel();
        Spliterator<T> fromSpliterator = stream.spliterator();

        if (!fromSpliterator.hasCharacteristics(Spliterator.SUBSIZED)) {
            Iterator<T> fromIterator = Spliterators.iterator(fromSpliterator);
            return StreamSupport.stream(
                    new Spliterators.AbstractSpliterator<R>(
                            fromSpliterator.estimateSize(),
                            fromSpliterator.characteristics() & (Spliterator.ORDERED | Spliterator.SIZED)) {
                        long index = 0;

                        @Override
                        public boolean tryAdvance(Consumer<? super R> action) {
                            if (fromIterator.hasNext()) {
                                action.accept(function.apply(fromIterator.next(), index++));
                                return true;
                            }
                            return false;
                        }
                    },
                    isParallel);
        }
        class Splitr extends MapWithIndexSpliterator<Spliterator<T>, R, Splitr> implements Consumer<T> {
            T holder;

            Splitr(Spliterator<T> splitr, long index) {
                super(splitr, index);
            }

            @Override
            public void accept(@Nullable T t) {
                this.holder = t;
            }

            @Override
            public boolean tryAdvance(Consumer<? super R> action) {
                if (fromSpliterator.tryAdvance(this)) {
                    try {
                        action.accept(function.apply(holder, index++));
                        return true;
                    } finally {
                        holder = null;
                    }
                }
                return false;
            }

            @Override
            Splitr createSplit(Spliterator<T> from, long i) {
                return new Splitr(from, i);
            }
        }
        return StreamSupport.stream(new Splitr(fromSpliterator, 0), isParallel);
    }

    private abstract static class MapWithIndexSpliterator<
                    F extends Spliterator<?>, R, S extends MapWithIndexSpliterator<F, R, S>>
            implements Spliterator<R> {
        final F fromSpliterator;
        long index;

        MapWithIndexSpliterator(F fromSpliterator, long index) {
            this.fromSpliterator = fromSpliterator;
            this.index = index;
        }

        abstract S createSplit(F from, long i);

        @Override
        public S trySplit() {
            @SuppressWarnings("unchecked")
            F split = (F) fromSpliterator.trySplit();
            if (split == null) {
                return null;
            }
            S result = createSplit(split, index);
            this.index += split.getExactSizeIfKnown();
            return result;
        }

        @Override
        public long estimateSize() {
            return fromSpliterator.estimateSize();
        }

        @Override
        public int characteristics() {
            return fromSpliterator.characteristics() & (Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED);
        }
    }
}
