package core;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "doc")
public class Doc {

	public static class Fields {

		public final static String DOCID = "docID";
		public final static String TITLE = "title";
		public final static String BODY = "body";
	}
	@Element(name = Fields.DOCID)
	private String docID;
	@Element(name = Fields.TITLE)
	private String title;
	@Element(name = Fields.BODY)
	private String body;

	/**
	 * @return the id
	 */
	public String getDocID() {
		return docID;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the description
	 */
	public String getBody() {
		return body;
	}

	@Override
	public String toString() {
		return String.format("%s:%s\n%s:%s\n%s:%s",
				     Fields.DOCID, docID,
				     Fields.TITLE, title,
				     Fields.BODY, body);
	}
}
