package org.cosmy;

import org.cosmy.model.Preferences;

import java.util.Stack;

public class PaginationContext {
    private boolean prevButtonEnabled = true;
    private boolean nextButtonDisabled = true;
    private String currentContinuationToken;
    private Stack<String> pages = new Stack<>();

    public boolean isPrevButtonEnabled() {
        return prevButtonEnabled;
    }

    public void setPrevButtonDisabled(boolean prevButtonEnabled) {
        this.prevButtonEnabled = prevButtonEnabled;
    }

    public boolean isNextButtonDisabled() {
        return nextButtonDisabled;
    }

    public void setNextButtonDisabled(boolean nextButtonDisabled) {
        this.nextButtonDisabled = nextButtonDisabled;
    }

    public String getContinuationToken() {
        return currentContinuationToken;
    }

    public void setContinuationToken(String currentContinuationToken) {
        this.currentContinuationToken = currentContinuationToken;
        pages.push(currentContinuationToken);
    }

    public String getPrevContinuationToken(){
        return pages.pop();
    }

    public int getPreferredPageLength(){
        System.out.println(Preferences.getInstance().getPageLength());
        return Preferences.getInstance().getPageLength();
    }

    public void clear() {
        setNextButtonDisabled(true);
        setContinuationToken(null);
        setPrevButtonDisabled(true);
        pages = new Stack<>();
    }
}
