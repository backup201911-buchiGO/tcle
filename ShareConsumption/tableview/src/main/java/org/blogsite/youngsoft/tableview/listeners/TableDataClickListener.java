package org.blogsite.youngsoft.tableview.listeners;

/**
 * 표 데이터에 대한 클릭 리스너
 *
 */
public interface TableDataClickListener<T> {

    /**
     * 이 메소드는 특정 테이블 데이터를 클릭했을 때 호출
     *
     * @param rowIndex    클릭 된 행의 인덱스
     * @param clickedData 클릭 된 데이터
     */
    void onDataClicked(final int rowIndex, final T clickedData);

}
