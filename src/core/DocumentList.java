package core;

import java.util.List;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "documents")
public class DocumentList {

	@ElementList(inline = true)
	private List<Doc> documentList;

	public List<Doc> getDocumentList() {
		return documentList;
	}
}
