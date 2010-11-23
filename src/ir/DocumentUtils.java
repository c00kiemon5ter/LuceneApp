package ir;

import core.Doc;
import core.DocumentList;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.simpleframework.xml.core.Persister;

public class DocumentUtils {

	private Collection<Document> documents;

	public void createDocuments(File xmlfile) throws Exception {
		Document document = null;
		DocumentList documentList = new Persister().read(DocumentList.class, xmlfile);
		documents = new ArrayList<Document>(documentList.getDocumentList().size());
		for (Doc doc : documentList.getDocumentList()) {
			document = new Document();
			document.add(new Field(Doc.Fields.DOCID, doc.getDocID(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
			document.add(new Field(Doc.Fields.TITLE, doc.getTitle(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
			document.add(new Field(Doc.Fields.BODY, doc.getBody(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
			documents.add(document);
		}
	}

	public void printDocuments() {
		printDocuments(documents);
	}

	public void printDocuments(Collection<Document> documents) {
		for (Document document : documents) {
			System.out.printf("%s:%s\n%s:%s\n%s:%s\n",
					  Doc.Fields.DOCID, document.getField(Doc.Fields.DOCID).stringValue(),
					  Doc.Fields.TITLE, document.getField(Doc.Fields.TITLE).stringValue(),
					  Doc.Fields.BODY, document.getField(Doc.Fields.BODY).stringValue());
		}
	}

	public Collection<Document> getDocuments() {
		return documents;
	}
}
