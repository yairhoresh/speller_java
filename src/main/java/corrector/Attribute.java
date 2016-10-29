package corrector;

public class Attribute {
	
	private int recordId;
	private String table;
	private String column;
	
	public Attribute(int recordId, String table, String column) {

		this.recordId = recordId;
		this.table = table;
		this.column = column;
	}

	public int getRecordId() {
		return recordId;
	}

	public String getTable() {
		return table;
	}

	public String getColumn() {
		return column;
	}
}
