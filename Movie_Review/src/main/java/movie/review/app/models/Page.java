package movie.review.app.models;

public class Page {
    /* Determines the number of the current page */
    private int pageNum;

    /* Determines if this is the page that the user is on  */
    private boolean isCurrent;

    /* Determines if this page is a separator */
    private boolean isSeparator;

    /*
    * Represents a page in the pagination.
    */
    public Page(int number, boolean isCurrent) {
        this.pageNum = number;
        this.isCurrent = isCurrent;
        this.isSeparator = false;
    }

    /*
    * Add the "..." between page numbers
    */
    public static Page setSeparator() {
        Page item = new Page(0, false);
        item.isSeparator = true;
        return item;
    }

    public int getPageNum() {
        return pageNum;
    }

}