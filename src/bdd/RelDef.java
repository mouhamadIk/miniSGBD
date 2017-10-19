package bdd;

import manager.PageId;

public class RelDef {
	
	private RelSchema relSchema;
	private PageId page;
	private int recordSize;
	

	public RelDef(String[] userInput,int page) {
		relSchema = new RelSchema(userInput);
		this.page = new PageId(page, 0);
	}	
	
	public RelDef(RelSchema relSchema, int page, int recordSize) {
		super();
		this.relSchema = relSchema;
		this.page = new PageId(page,0);
		this.recordSize = recordSize;
	}



	public String toString(){
		
		return relSchema.toString()+page.toString();
	}

	public RelSchema getRelSchema() {
		return relSchema;
	}

	public void setRelSchema(RelSchema relSchema) {
		this.relSchema = relSchema;
	}

	public PageId getPage() {
		return page;
	}

	public void setPage(PageId page) {
		this.page = page;
	}
	
}
