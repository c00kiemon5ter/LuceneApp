package application;

import core.Doc;
import ir.IndexUtils;
import ir.DocumentUtils;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class Application {

	private final String separetor = "-----------------------------------"
					 + "---------------------------------";
	private File file;
	private DocumentUtils docUtils;
	private IndexUtils idxutils;

	public static void main(String[] args) {
		Application app = new Application();
		try {
			app.parseArgs(args);
			app.manageDocuments();
			app.manageIndex();
			app.manageQueries();
		} catch (Exception ex) {
			Logger.getLogger(Application.class.getName()).log(Level.SEVERE, "Fatal Error", ex);
			System.exit(1);
		}
	}

	private void parseArgs(String[] args) {
		if (args.length != 1) {
			throw new IllegalArgumentException("Wrong argument count");
		}
		file = new File(args[0]);
	}

	private void manageDocuments() throws Exception {
		docUtils = new DocumentUtils();
		docUtils.createDocuments(file);
		System.out.printf("%s\nThe Documents:\n%s\n", separetor, separetor);
		docUtils.printDocuments();
	}

	private void manageIndex() throws CorruptIndexException,
					  LockObtainFailedException,
					  IOException {
		idxutils = new IndexUtils(docUtils.getDocuments());
		idxutils.createIndex();
		System.out.printf("%s\nThe Index Info:\n%s\n", separetor, separetor);
		idxutils.printIndexInfo();
		System.out.printf("%s\nThe Index:\n%s\n", separetor, separetor);
		idxutils.printIndex();
		System.out.printf("%s\nThe Document Vectors:\n%s\n", separetor, separetor);
		idxutils.printDocumentVectors();
	}

	private void manageQueries() throws ParseException, CorruptIndexException, IOException {
		List<String> queries = initQueries();
		searchQueries(queries);
	}

	private List<String> initQueries() {
		List<String> queries = new LinkedList<String>();
		queries.add("image retrieval engines");
		queries.add("image retrieval");
		queries.add("image retrieval image");
		queries.add("processing with programming languages processes");
		queries.add("Visual multimedia");
		queries.add("java");
		queries.add("Visual and semantic multimedia retrieval");
		queries.add("models");
		return queries;
	}

	private void searchQueries(List<String> queries) throws CorruptIndexException, IOException, ParseException {
		IndexSearcher searcher = new IndexSearcher(idxutils.getIndex(), true);
		for (String field : new String[]{Doc.Fields.TITLE, Doc.Fields.BODY}) {
			QueryParser queryParser = new QueryParser(Version.LUCENE_29, field, idxutils.getSimpleAnalyzer());
			for (String query : queries) {
				TopScoreDocCollector collector = TopScoreDocCollector.create(10, true);
				System.out.printf("%s\nSearching %s for: %s\n%s\n", separetor, field, query, separetor);
				searcher.search(queryParser.parse(query), collector);
				for (ScoreDoc scoreDoc : collector.topDocs().scoreDocs) {
					System.out.printf("%3s:%s\n\t%s\n",
							  searcher.doc(scoreDoc.doc).getField(Doc.Fields.DOCID).stringValue(),
							  searcher.doc(scoreDoc.doc).getField(Doc.Fields.TITLE).stringValue(),
							  searcher.doc(scoreDoc.doc).getField(Doc.Fields.BODY).stringValue());
				}
			}
		}
		searcher.close();
	}
}
