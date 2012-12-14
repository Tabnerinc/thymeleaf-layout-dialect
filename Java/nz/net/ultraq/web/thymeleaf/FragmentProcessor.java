
package nz.net.ultraq.web.thymeleaf;

import static nz.net.ultraq.web.thymeleaf.LayoutDialect.LAYOUT_PREFIX;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;

/**
 * Processor for the 'layout:fragment' attribute, replaces the content and tag
 * of the decorator fragment with those of the same name from the content page.
 * 
 * @author Emanuel Rabina
 */
public class FragmentProcessor extends AbstractProcessor {

	private static final Logger logger = LoggerFactory.getLogger(FragmentProcessor.class);

	private static final String HTML_ELEMENT_TITLE = "title";

	static final String ATTRIBUTE_NAME_FRAGMENT = "fragment";
	static final String ATTRIBUTE_NAME_FRAGMENT_FULL = LAYOUT_PREFIX + ":" + ATTRIBUTE_NAME_FRAGMENT;

	static final String FRAGMENT_NAME_PREFIX = "fragment-name::";

	/**
	 * Constructor, sets this processor to work on the 'fragment' attribute.
	 */
	public FragmentProcessor() {

		super(ATTRIBUTE_NAME_FRAGMENT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getPrecedence() {

		return 1;
	}

	/**
	 * Includes or replaces the content of fragments into the corresponding
	 * fragment placeholder.
	 * 
	 * @param arguments
	 * @param element
	 * @param attributeName
	 * @return Processing result
	 */
	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {

		// Emit a warning if found in the <title> element
		if (element.getOriginalName().equals(HTML_ELEMENT_TITLE)) {
			logger.warn("You don't need to put the layout:fragment attribute into the <title> element - " +
					"the decoration process will automatically override the <title> with the one in " +
					"your content page, if present.");
		}

		// Locate the page fragment that corresponds to this decorator/include fragment
		String fragmentname = element.getAttributeValue(attributeName);
		Element pagefragment = (Element)arguments.getLocalVariable(FRAGMENT_NAME_PREFIX + fragmentname);
		element.removeAttribute(attributeName);

		// Replace the decorator/include fragment with the page fragment
		if (pagefragment != null) {
			pagefragment.removeAttribute(attributeName);
			mergeAttributes(element, pagefragment);
			pullTargetContent(element, pagefragment);
		}

		return ProcessorResult.OK;
	}
}
