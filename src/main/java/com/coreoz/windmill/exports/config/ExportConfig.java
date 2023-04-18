package com.coreoz.windmill.exports.config;

import java.util.List;
import java.util.function.Function;

/**
 * A builder that contains rows to export in a file
 * 
 * @param <T> The type of rows to export
 */
public class ExportConfig<T> {

	private final Iterable<T> rows;

	public ExportConfig(Iterable<T> rows) {
		this.rows = rows;
	}

	/**
	 * Prepare an export that will NOT contains a header row with the column names.
	 * A usage example:
	 * <pre>
	 * <code>
	 * Windmill
	 *   .export(Arrays.asList(bean1, bean2, bean3))
	 *   .withNoHeaderMapping(
	 *     Arrays.asList(
	 *       Bean::getName,
	 *       bean -> bean.getUser().getLogin()
	 *     )
	 *   )
	 * </code>
	 * </pre>
	 * 
	 * @param valuesMapping A list of mapping that will fetch a value for each row.
	 */
	public ExportRowsConfig<T> withNoHeaderMapping(List<Function<T, ?>> valuesMapping) {
		return new ExportRowsConfig<>(rows, new ExportNoHeaderMapping<>(valuesMapping));
	}

	/**
	 * Prepare an export that will NOT contains a header row with the column names.
	 * A usage example:
	 * <pre>
	 * <code>
	 * Windmill
	 *   .export(Arrays.asList(bean1, bean2, bean3))
	 *   .withNoHeaderMapping(
	 *     Bean::getName,
	 *     bean -> bean.getUser().getLogin()
	 *   )
	 * </code>
	 * </pre>
	 * 
	 * @param rowValueExtractor A mapping that will fetch a value for each row.
	 */
	@SafeVarargs
	public final ExportRowsConfig<T> withNoHeaderMapping(Function<T, ?> ...rowValueExtractor) {
		return new ExportRowsConfig<>(rows, new ExportNoHeaderMapping<>(rowValueExtractor));
	}

	/**
	 * Prepare an export that will contains a header row with the column names.
	 * A usage example:
	 * <pre>
	 * <code>
	 * Windmill
	 *   .export(Arrays.asList(bean1, bean2, bean3))
	 *   .withHeaderMapping(
	 *     new ExportHeaderMapping&lt;Bean&gt;()
	 *       .add("Name", Bean::getName)
	 *       .add("User login", bean -> bean.getUser().getLogin())
	 *   )
	 * </code>
	 * </pre>
	 * 
	 * @param mapping A mapping that will associate a name and a function to extract a row value
	 * for each columns of the export file
	 */
	public ExportRowsConfig<T> withHeaderMapping(ExportHeaderMapping<T> mapping) {
		return new ExportRowsConfig<>(rows, mapping);
	}

    /**
     * Prepare an export with a custom implementation for the mapping
     * A usage example:
	 * <pre>
	 * <code>
     * public class CustomMapping implements ExportMapping<Bean> { }
     * [...]
     * List<Bean> list = Arrays.asList(bean1, bean2, bean3);
	 * Windmill
	 *   .export(list)
	 *   .withMapping(new CustomMapping());
	 * </code>
	 * </pre>
	 * 
     * @param mapping
     * @return
     */
    public ExportRowsConfig<T> withMapping(ExportMapping<T> mapping) {
        return new ExportRowsConfig<>(rows, mapping);
    }

}
