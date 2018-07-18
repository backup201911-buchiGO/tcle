package org.blogsite.youngsoft.tableview.listeners;

/**
 * 테이블 데이터에 대한 긴 클릭 리스너
 *
 */
public interface TableDataLongClickListener<T> {

    /**
     * 이 메소드는 특정 테이블 데이터를 클릭했을 때 호출됨
     *
     * @param rowIndex    클릭 한 행의 인덱스
     * @param clickedData 클릭 한 데이터
     * @return 리스너가 "comsumed" 이벤트의 경우의 플래그
     *          이벤트가 "comsumed" 인 경우 {@link TableDataClickListener}는 통지되지 않음
     */
    boolean onDataLongClicked(final int rowIndex, final T clickedData);

}
