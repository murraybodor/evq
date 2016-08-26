package ca.aeso.evq.client.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DelegatingChangeListenerCollection;
import com.google.gwt.user.client.ui.DelegatingClickListenerCollection;
import com.google.gwt.user.client.ui.DelegatingFocusListenerCollection;
import com.google.gwt.user.client.ui.DelegatingKeyboardListenerCollection;
import com.google.gwt.user.client.ui.FiresSuggestionEvents;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.FocusListenerAdapter;
import com.google.gwt.user.client.ui.HasFocus;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SourcesChangeEvents;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.SourcesFocusEvents;
import com.google.gwt.user.client.ui.SourcesKeyboardEvents;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestionHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;

/**
 * Customized SuggestBox. 
 * Instead of showing a popup with the suggestions, this class populates a standard ListBox.
 * 
 * @author mbodor
 *
 */
public final class EvqSuggestBox extends Composite implements HasText, HasFocus,
    SourcesClickEvents, SourcesFocusEvents, SourcesChangeEvents,
    SourcesKeyboardEvents, FiresSuggestionEvents {

  private static final String STYLENAME_DEFAULT = "gwt-SuggestBox";

  private int limit = 2000;
  private StartsWithSuggestOracle oracle;
  private String currentText;
  private String currentFilter;
  private String newFilter;
  private final TextBoxBase box;
  private ArrayList suggestionHandlers = null;
  private DelegatingClickListenerCollection clickListeners;
  private DelegatingChangeListenerCollection changeListeners;
  private DelegatingFocusListenerCollection focusListeners;
  private DelegatingKeyboardListenerCollection keyboardListeners;
  private ListBox suggestionListBox;

  private final Callback callBack = new Callback() {
    public void onSuggestionsReady(Request request, Response response) {
    	loadSuggestionListBox(response.getSuggestions());
    }
  };

  private void loadSuggestionListBox(Collection suggestions) {

	  suggestionListBox.clear();

	  if (suggestions!=null) {
		  for (Iterator iterator = suggestions.iterator(); iterator.hasNext();) {
			  SuggestOracle.Suggestion item = (SuggestOracle.Suggestion) iterator.next();
			  suggestionListBox.addItem(item.getDisplayString());
		  }
	  }

  }
  /**
   * Constructor for {@link SuggestBox}. Creates a
   * {@link MultiWordSuggestOracle} and {@link TextBox} to use with this
   * {@link SuggestBox}.
   */
  public EvqSuggestBox() {
    this(new StartsWithSuggestOracle(), new ListBox());
  }

  /**
   * Constructor for {@link SuggestBox}. Creates a {@link TextBox} to use with
   * this {@link SuggestBox}.
   * 
   * @param oracle the oracle for this <code>SuggestBox</code>
   */
  public EvqSuggestBox(StartsWithSuggestOracle oracle, ListBox targetListBox) {
    this(oracle, new TextBox(), targetListBox);
  }

  /**
   * Constructor for {@link SuggestBox}. The text box will be removed from it's
   * current location and wrapped by the {@link SuggestBox}.
   * 
   * @param oracle supplies suggestions based upon the current contents of the
   *          text widget
   * @param box the text widget
   */
  public EvqSuggestBox(StartsWithSuggestOracle oracle, TextBoxBase box, ListBox targetListBox) {
    this.box = box;
    this.suggestionListBox = targetListBox;
    initWidget(box);

    addListeners();
    setOracle(oracle);
    setStyleName(STYLENAME_DEFAULT);
  }

  /**
   * Adds a listener to recieve change events on the SuggestBox's text box.
   * The source Widget for these events will be the SuggestBox.
   *
   * @param listener the listener interface to add
   */
  public final void addChangeListener(ChangeListener listener) {
    if (changeListeners == null) {
      changeListeners = new DelegatingChangeListenerCollection(this, box);
    }
    changeListeners.add(listener);
  }

  public void setFilter(String filter) {
	  this.newFilter = filter;
  }
  /**
   * Adds a listener to recieve click events on the SuggestBox's text box.
   * The source Widget for these events will be the SuggestBox.
   *
   * @param listener the listener interface to add
   */
  public final void addClickListener(ClickListener listener) {
    if (clickListeners == null) {
      clickListeners = new DelegatingClickListenerCollection(this, box);
    }
    clickListeners.add(listener);
  }

  public final void addEventHandler(SuggestionHandler handler) {
    if (suggestionHandlers == null) {
      suggestionHandlers = new ArrayList();
    }
    suggestionHandlers.add(handler);
  }

  /**
   * Adds a listener to recieve focus events on the SuggestBox's text box.
   * The source Widget for these events will be the SuggestBox.
   *
   * @param listener the listener interface to add
   */
  public final void addFocusListener(FocusListener listener) {
    if (focusListeners == null) {
      focusListeners = new DelegatingFocusListenerCollection(this, box);
    }
    focusListeners.add(listener);
  }

  /**
   * Adds a listener to recieve keyboard events on the SuggestBox's text box.
   * The source Widget for these events will be the SuggestBox.
   *
   * @param listener the listener interface to add
   */
  public final void addKeyboardListener(KeyboardListener listener) {
    if (keyboardListeners == null) {
      keyboardListeners = new DelegatingKeyboardListenerCollection(this, box);
    }
    keyboardListeners.add(listener);
  }

  /**
   * Gets the limit for the number of suggestions that should be displayed for
   * this box. It is up to the current {@link SuggestOracle} to enforce this
   * limit.
   * 
   * @return the limit for the number of suggestions
   */
  public final int getLimit() {
    return limit;
  }

  /**
   * Gets the suggest box's {@link com.google.gwt.user.client.ui.SuggestOracle}.
   * 
   * @return the {@link SuggestOracle}
   */
  public final SuggestOracle getSuggestOracle() {
    return oracle;
  }

  public final int getTabIndex() {
    return box.getTabIndex();
  }

  public final String getText() {
    return box.getText();
  }

  public final void removeChangeListener(ChangeListener listener) {
    if (changeListeners != null) {
      changeListeners.remove(listener);
    }
  }

  public final void removeClickListener(ClickListener listener) {
    if (clickListeners != null) {
      clickListeners.remove(listener);
    }
  }

  public final void removeEventHandler(SuggestionHandler handler) {
    if (suggestionHandlers == null) {
      return;
    }
    suggestionHandlers.remove(handler);
  }

  public final void removeFocusListener(FocusListener listener) {
    if (focusListeners != null) {
      focusListeners.remove(listener);
    }
  }

  public final void removeKeyboardListener(KeyboardListener listener) {
    if (keyboardListeners != null) {
      keyboardListeners.remove(listener);
    }
  }

  public final void setAccessKey(char key) {
    box.setAccessKey(key);
  }

  public final void setFocus(boolean focused) {
    box.setFocus(focused);
  }

  /**
   * Sets the limit to the number of suggestions the oracle should provide. It
   * is up to the oracle to enforce this limit.
   * 
   * @param limit the limit to the number of suggestions provided
   */
  public final void setLimit(int limit) {
    this.limit = limit;
  }

  public final void setTabIndex(int index) {
    box.setTabIndex(index);
  }

  public final void setText(String text) {
    box.setText(text);
  }


  /**
   * Add focus and keyboard listeners to the suggest box
   */
  private void addListeners() {

	  box.addFocusListener(new FocusListenerAdapter() {
		  public void onFocus(Widget sender) {
			  refreshSuggestions();
		  }

	  }) ;

	  box.addKeyboardListener(new KeyboardListenerAdapter() {
		  public void onKeyUp(Widget sender, char keyCode, int modifiers) {
			  refreshSuggestions();
		  }

	  });
  }

  /**
   * Based on the current filter and text, refresh the list of suggestions
   */
  private void refreshSuggestions() {

      String text = box.getText().toLowerCase();

      // figure out what has changed
      if (newFilter==null) {
    	  if (currentFilter==null) {
    		  if (text.equals(currentText)) {
				  return;
    		  }
    	  }
      } else {
    	  if (currentFilter!=null) {
    		  if (newFilter.equals(currentFilter) && text.equals(currentText)) {
    			  return;
    		  }
    	  }
      }
    	  
      currentText = text;
      currentFilter = newFilter;
      showSuggestions(currentText, currentFilter);
  }

  private void setOracle(StartsWithSuggestOracle oracle) {
    this.oracle = oracle;
  }

  private void showSuggestions(String query, String filter) {
    oracle.requestSuggestions(new Request(query, limit), filter, callBack);
  }
}
