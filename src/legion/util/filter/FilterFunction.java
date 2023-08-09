package legion.util.filter;

import java.util.Objects;
import java.util.function.Function;

import legion.util.filter.FilterOperation.FilterCompareOp;

@FunctionalInterface
public interface FilterFunction<T, R> {
	R apply(T _t1, FilterCompareOp _op, T _t2) throws Exception;

	default <V> FilterFunction<T, V> andThen(Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (T t1, FilterCompareOp _op, T t2) -> after.apply(apply(t1, _op, t2));
	}
}
