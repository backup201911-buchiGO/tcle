package org.blogsite.youngsoft.tableview.colorizers;

/**
 * 테이블 데이터 행의 백그라운드 칼라 프로 바이더의 인터페이
 *
 * @deprecated {@link TableDataRowColorizer} 는
 * {@link org.blogsite.youngsoft.tableview.providers.TableDataRowBackgroundProvider} 로
 * 대체되어 더 이상 사용하지 않음.
 */
@Deprecated
public interface TableDataRowColorizer<T> {

    /**
     * 지정된 데이터를 보관 유지하는 지정된 인덱스를 가지는 행의 행의 색을 지정
     *
     * @param rowIndex 배경색을 반환 할 행의 인덱스
     * @param rowData  행에 표시되는 데이터로 배경색을 반환
     * @return 지정된 행으로 사용되는 배경
     */
    int getRowColor(final int rowIndex, final T rowData);

}
