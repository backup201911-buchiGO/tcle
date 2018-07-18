package org.blogsite.youngsoft.tableview.model;

import android.content.Context;
import android.util.DisplayMetrics;

import java.util.HashMap;
import java.util.Map;

/**
 * dp 단위의 절대적인 열의 폭을 보관 유지하는 {@link TableColumnModel} 구현
 *
 */
public class TableColumnDpWidthModel implements TableColumnModel {

    private static final int DEFAULT_COLUMN_WIDTH_IN_DP = 100;

    private final Map<Integer, Integer> columnWidths;
    private final DisplayMetrics displayMetrics;
    private int columnCount;
    private int defaultColumnWidth;

    /**
     * 지정된 렬수로 새로운 TableColumnModel를 작성합니다. 모든 열은 기본적으로 너비가 100dp 임
     *
     * @param context     dp로부터 픽셀을 계산하는데 필요한 {@link DisplayMetrics}를 관찰하기 위한 {@link Context}
     * @param columnCount 컬럼 수
     */
    public TableColumnDpWidthModel(final Context context, final int columnCount) {
        this(context, columnCount, DEFAULT_COLUMN_WIDTH_IN_DP);
    }

    /**
     * 지정된 컬럼 수로 새로운 TableColumnModel를 작성합니다. 모든 열은 기본적으로 너비가 100dp임
     *
     * @param displayMetrics dp의 픽셀을 계산하기 위해서 필요한 {@link DisplayMetrics}.
     * @param columnCount    컬럼 수
     */
    public TableColumnDpWidthModel(final DisplayMetrics displayMetrics, final int columnCount) {
        this(displayMetrics, columnCount, DEFAULT_COLUMN_WIDTH_IN_DP);
    }

    /**
     * 지정된 컬럼 수로 새로운 TableColumnModel를 작성하며 모든 컬럼에는 지정된 기본 너비가 있음
     *
     * @param context                The {@link Context} to observe the {@link DisplayMetrics} needed for calculate the pixels from the dp.
     * @param columnCount            The number of columns.
     * @param defaultColumnWidthInDp The default column width in dp.
     */
    public TableColumnDpWidthModel(final Context context, final int columnCount,
                                   final int defaultColumnWidthInDp) {

        this(context.getResources().getDisplayMetrics(), columnCount, defaultColumnWidthInDp);
    }

    /**
     * Creates a new TableColumnModel with the given number of columns. Every column has the given default width.
     *
     * @param displayMetrics         The {@link DisplayMetrics} needed for calculate the pixels from the dp.
     * @param columnCount            The number of columns.
     * @param defaultColumnWidthInDp The default column width in dp.
     */
    public TableColumnDpWidthModel(final DisplayMetrics displayMetrics, final int columnCount,
                                   final int defaultColumnWidthInDp) {

        this.columnWidths = new HashMap<>();
        this.displayMetrics = displayMetrics;
        this.columnCount = columnCount;
        this.defaultColumnWidth = toPixel(defaultColumnWidthInDp);
    }

    /**
     * Sets the column width for the column at the given index.
     *
     * @param columnIndex     The index of the column.
     * @param columnWidthInDp The width of the column in dp.
     */
    public void setColumnWidth(int columnIndex, int columnWidthInDp) {
        this.columnWidths.put(columnIndex, toPixel(columnWidthInDp));
    }

    @Override
    public void setColumnCount(final int columnCount) {
        this.columnCount = columnCount;
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public int getColumnWidth(int columnIndex, int tableWidthInPx) {
        final Integer columnWidth = columnWidths.get(columnIndex);
        if (columnWidth == null) {
            return defaultColumnWidth;
        }
        return columnWidth;
    }

    private int toPixel(final int dp) {
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
