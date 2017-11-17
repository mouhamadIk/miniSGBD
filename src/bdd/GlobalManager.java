package bdd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import constant.Constant;
import manager.BufferManager;
import manager.HeapFile;

public class GlobalManager {

	private static Dbdef db;
	private static ArrayList<HeapFile> heapFiles;

	public static void init() throws IOException {
		try {
			File file = new File("Catalog.def");
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			db = (Dbdef) ois.readObject();
			ois.close();
			fis.close();
		} catch (IOException | ClassNotFoundException e) {
			db = new Dbdef();
		} finally {
			BufferManager.bufferManager();
			heapFiles = new ArrayList<HeapFile>();
			refreshHeapFiles();
		}
	}

	/**
	 * 
	 * @param userInput
	 *            , tab of String containing user's values. Add a relation in
	 *            the database.
	 */
	public static void createRelation(String[] userInput) throws IOException {
		RelSchema relSchema = new RelSchema(userInput);
		int sizeRecord = calculRecordSize(relSchema);
		int slotCount = (int) (Constant.PAGESIZE / (sizeRecord + 1));
		int index = db.getCompteurRel();
		RelDef relDef = new RelDef(relSchema, index, sizeRecord, slotCount);

		db.addRelationToDBAtIndex(index, relDef);
		HeapFile heapFile = new HeapFile(relDef);
		heapFiles.add(index, heapFile);
		heapFiles.get(index).createHeader();
	}

	/**
	 * Save the DataBase db into a file Catalog.def
	 * 
	 * @throws IOException
	 */
	public static void finish() throws IOException {
		File file = new File("Catalog.def");
		try (FileOutputStream fos = new FileOutputStream(file); ObjectOutputStream oos = new ObjectOutputStream(fos);) {
			oos.writeObject(db);
		}
		BufferManager.flushAll();
	}

	public static void refreshHeapFiles() throws IOException {
		RelDef relDef;
		RelSchema relSchema;
		int sizeRecord;

		for (int i = 0; i < db.getListRelation().size(); i++){
				relSchema = db.getListRelation().get(i).getRelSchema();
				sizeRecord = calculRecordSize(relSchema);
				relDef = new RelDef(relSchema, i, sizeRecord, Constant.PAGESIZE / (sizeRecord + 1));
				heapFiles.add(i, new HeapFile(relDef));
				heapFiles.get(i).createHeader();
			}
	}

	public static void insert(String name, String[] userInput) throws IOException {
		Record record = new Record();
		List<String> values = new ArrayList<>(userInput.length - 2);
		int indexOfRelDef;
		for (int i = 0; i < userInput.length - 2; i++)
			values.add(i, userInput[i + 2]);

		record.setValues(values);

		indexOfRelDef = db.getIndexOfRelSchemaByName(name);
		if (indexOfRelDef != -1)
			heapFiles.get(indexOfRelDef).insertRecord(record);

		// TODO the else

	}

	public static int calculRecordSize(RelSchema relSchema) {
		List<String> typeColumns = relSchema.getTypeColumns();
		String type;
		int longueur = 0;
		int recordSize = 0;

		for (int i = 0; i < typeColumns.size(); i++) {
			if (typeColumns.get(i).charAt(0) == 'S' || typeColumns.get(i).charAt(0) == 's') {
				type = typeColumns.get(i).substring(0, 6);
				longueur = Integer.parseInt((typeColumns.get(i).substring(6)));
			} else
				type = typeColumns.get(i);

			switch (type) {
			case "int":
			case "Int":
			case "INT":
				recordSize += 4;
			case "float":
			case "Float":
			case "FLOAT":
				recordSize += 4;
				break;
			case "string":
			case "String":
			case "STRING":
				recordSize += 2 * longueur;
				break;
			}
		}

		return recordSize;
	}

	/**
	 * Display of each relation, begins at 0.
	 */
	public static void displayRelSchema() {
		int i = 0;
		for (RelDef r : db.getListRelation()) {
			System.out.print("Relation " + i++ + " : ");
			r.getRelSchema().display();
		}
	}

	public static void fill(String[] userInput) throws IOException {
		String relName = userInput[1];

		if (userInput[2].substring(userInput[2].length() - 4).equals(".cvs")) {
			File file = new File(userInput[2]);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			int indexOfRelDef = db.getIndexOfRelSchemaByName(relName);
			HeapFile hf = heapFiles.get(indexOfRelDef);
			ArrayList<String> values = new ArrayList<>();
			StringTokenizer st = new StringTokenizer("", ",");
			String s = "";
			Record record = new Record();

			raf.seek(0);

			for (s = raf.readLine(); !s.equals(""); s = raf.readLine()) {
				st = new StringTokenizer(s, ",");
				while (st.hasMoreTokens())
					values.add(st.nextToken());
				record.setValues(values);
				hf.insertRecord(record);
				values.clear();
			}
			
			raf.close();

		}

	}

	public static void selectAll(String relName) throws IOException {
		heapFiles.get(db.getIndexOfRelSchemaByName(relName)).printAllRecords();

	}

	public static void select(String[] userInput) throws IOException {

		int index = db.getIndexOfRelSchemaByName(userInput[1]);
		int indexColumn = Integer.parseInt(userInput[2]);
		String value = userInput[3];

		heapFiles.get(index).printAllRecordsWithFilter(indexColumn, value);
	}

	public static void clean() {
		File file = new File("Catalog.def");
		file.delete();
		for (int i = 0; i < heapFiles.size(); i++) {
			 file = new File("BDD" + File.separator + "Data_" + i + ".rf");
			file.delete();
		}

		db.clean();
		heapFiles.clear();
	}

}
