/*
Copyright 2011 Software Freedom Conservancy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.seleniumhq.selenium.fluent;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FluentWebElement extends BaseFluentWebElement {

    protected final WebElement currentElement;

    public FluentWebElement(WebDriver delegate, WebElement currentElement, Context context) {
        super(delegate, context);
        this.currentElement = currentElement;
    }

    protected WebElement getWebElement() {
        return currentElement;
    }

    @Override
    protected WebElement findIt(By by) {
        return actualFindIt(by);
    }

    @Override
    protected List<WebElement> findThem(By by) {
        return actualFindThem(by);
    }

    @Override
    protected WebElement actualFindIt(By by) {
        return currentElement.findElement(by);
    }

    @Override
    protected List<WebElement> actualFindThem(By by) {
        return currentElement.findElements(by);
    }

    public FluentWebElement click() {
        Context ctx = Context.singular(context, "click");
        decorateExecution(new Click(), ctx);
        return new FluentWebElement(delegate, currentElement, ctx);
    }

    /**
     *  Use this instead of clear() to clear an WebElement
     */

    public FluentWebElement clearField() {
        Context ctx = Context.singular(context, "clearField");
        decorateExecution(new Clear(), ctx);
        return new FluentWebElement(delegate, currentElement, ctx);
    }


    public FluentWebElement submit() {
        decorateExecution(new Submit(), Context.singular(context, "submit"));
        return new FluentWebElement(delegate, currentElement, context);
    }

    // These are as they would be in the WebElement API

    public FluentWebElement sendKeys(final CharSequence... keysToSend) {

        decorateExecution(new SendKeys(keysToSend), Context.singular(context, "sendKeys", null, charSeqArrayAsHumanString(keysToSend)));
        return new FluentWebElement(delegate, currentElement, context);
    }

    public TestableString getTagName() {
        return new TestableString(new GetTagName(), Context.singular(context, "getTagName"));
    }

    public boolean isSelected() {
        return decorateExecution(new IsSelected(), Context.singular(context, "isSelected"));
    }

    public boolean isEnabled() {
        return decorateExecution(new IsEnabled(), Context.singular(context, "isEnabled"));
    }

    public boolean isDisplayed() {
        return decorateExecution(new IsDisplayed(), Context.singular(context, "isDisplayed"));
    }

    public Point getLocation() {
        return decorateExecution(new GetLocation(), Context.singular(context, "getLocation"));
    }

    public Dimension getSize() {
        return decorateExecution(new GetSize(), Context.singular(context, "getSize"));
    }

    public TestableString cssValue(final String cssName) {
        return new TestableString(new GetCssValue(cssName), Context.singular(context, "getCssValue", null, cssName)).within(getPeriod());
    }

    public TestableString attribute(final String attr) {
        return new TestableString(new GetAttribute(attr), Context.singular(context, "getAttribute", null, attr)).within(getPeriod());
    }

    public TestableString getText() {
        return new TestableString(new GetText(), Context.singular(context, "getText")).within(getPeriod());
    }

    //@Override
    public WebElementValue<Point> location() {
        return new WebElementValue<Point>(currentElement.getLocation(), Context.singular(context, "location"));
    }

    //@Override
    public WebElementValue<Dimension> size() {
        return new WebElementValue<Dimension>(currentElement.getSize(), Context.singular(context, "size"));
    }

    //@Override
    public WebElementValue<String> tagName() {
        return new WebElementValue<String>(currentElement.getTagName(), Context.singular(context, "tagName"));
    }

    //@Override
    public WebElementValue<Boolean> selected() {
        return new WebElementValue<Boolean>(currentElement.isSelected(), Context.singular(context, "selected"));
    }

    //@Override
    public WebElementValue<Boolean> enabled() {
        return new WebElementValue<Boolean>(currentElement.isEnabled(), Context.singular(context, "enabled"));
    }

    //@Override
    public WebElementValue<Boolean> displayed() {
        return new WebElementValue<Boolean>(currentElement.isDisplayed(), Context.singular(context, "isDisplayed"));
    }

    //@Override
    public WebElementValue<String> text() {
        return new WebElementValue<String>(currentElement.getText(), Context.singular(context, "text()"));
    }

    public FluentWebElement within(Period period) {
        return new RetryingFluentWebElement(delegate, currentElement, Context.singular(context, "within", null, period), period);
    }

    public FluentWebDriver without(Period period) {
        return null;
    }


    private class RetryingFluentWebElement extends FluentWebElement {

        private final Period period;

        public RetryingFluentWebElement(WebDriver webDriver, WebElement currentElement, Context context, Period period) {
            super(webDriver, currentElement, context);
            this.period = period;
        }

        @Override
        protected Period getPeriod() {
            return period;
        }

        @Override
        protected WebElement findIt(By by) {
            return retryingFindIt(by);
        }

        @Override
        protected List<WebElement> findThem(By by) {
            return retryingFindThem(by);
        }

        @Override
        protected void changeTimeout() {
            delegate.manage().timeouts().implicitlyWait(period.howLong(), period.timeUnit());
        }

        @Override
        protected void resetTimeout() {
            delegate.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        }

    }

    private class Clear implements Execution<Boolean> {
        public Boolean execute() {
            currentElement.clear();
            return true;
        }
    }

    private class GetTagName implements Execution<String> {
        public String execute() {
            return currentElement.getTagName();
        }
    }

    private class Click implements Execution<Boolean> {
        public Boolean execute() {
            currentElement.click();
            return true;
        }
    }

    private class GetAttribute implements Execution<String> {
        private final String attr;

        public GetAttribute(String attr) {
            this.attr = attr;
        }

        public String execute() {
            return currentElement.getAttribute(attr);
        }
    }

    private class GetCssValue implements Execution<String> {
        private final String cssName;

        public GetCssValue(String cssName) {
            this.cssName = cssName;
        }

        public String execute() {
            return currentElement.getCssValue(cssName);
        }
    }

    private class GetText implements Execution<String> {
        public String execute() {
            return currentElement.getText();
        }
    }

    private class GetSize implements Execution<Dimension> {
        public Dimension execute() {
            return currentElement.getSize();
        }
    }

    private class GetLocation implements Execution<Point> {
        public Point execute() {
            return currentElement.getLocation();
        }
    }

    private class IsDisplayed implements Execution<Boolean> {
        public Boolean execute() {
            return currentElement.isDisplayed();
        }
    }

    private class IsEnabled implements Execution<Boolean> {
        public Boolean execute() {
            return currentElement.isEnabled();
        }
    }

    private class IsSelected implements Execution<Boolean> {
        public Boolean execute() {
            return currentElement.isSelected();
        }
    }

    private class SendKeys implements Execution<Boolean> {
        private final CharSequence[] keysToSend;

        public SendKeys(CharSequence... keysToSend) {
            this.keysToSend = keysToSend;
        }

        public Boolean execute() {
            currentElement.sendKeys(keysToSend);
            return true;
        }
    }

    private class Submit implements Execution<Boolean> {
        public Boolean execute() {
            currentElement.submit();
            return true;
        }
    }

}
